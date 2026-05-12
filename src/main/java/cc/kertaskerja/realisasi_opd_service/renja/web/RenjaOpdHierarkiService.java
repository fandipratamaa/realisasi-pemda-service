package cc.kertaskerja.realisasi_opd_service.renja.web;

import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguRepository;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class RenjaOpdHierarkiService {
    public enum DataSource {
        TARGET,
        PAGU
    }

    private final RenjaTargetRepository renjaTargetRepository;
    private final RenjaPaguRepository renjaPaguRepository;

    public RenjaOpdHierarkiService(RenjaTargetRepository renjaTargetRepository, RenjaPaguRepository renjaPaguRepository) {
        this.renjaTargetRepository = renjaTargetRepository;
        this.renjaPaguRepository = renjaPaguRepository;
    }

    public Mono<RenjaOpdHierarkiResponse> getHierarkiByKodeOpdTahunBulan(String kodeOpd, String tahun, String bulan, DataSource dataSource) {
        Mono<List<RenjaTarget>> targetsMono = renjaTargetRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd).collectList();
        Mono<List<RenjaPagu>> pagusMono = renjaPaguRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd).collectList();

        return Mono.zip(targetsMono, pagusMono)
                .map(tuple -> buildResponse(kodeOpd, tahun, bulan, tuple.getT1(), tuple.getT2(), dataSource));
    }

    private RenjaOpdHierarkiResponse buildResponse(String kodeOpd, String tahun, String bulan, List<RenjaTarget> targets, List<RenjaPagu> pagus, DataSource dataSource) {
        LinkedHashSet<String> renjaIds = new LinkedHashSet<>();
        for (RenjaTarget target : targets) {
            renjaIds.add(target.jenisRenjaId());
        }
        for (RenjaPagu pagu : pagus) {
            renjaIds.add(pagu.jenisRenjaId());
        }

        List<RenjaOpdHierarkiResponse.DataItem> dataItems = new ArrayList<>();
        for (String renjaId : renjaIds) {
            dataItems.add(buildDataItem(kodeOpd, tahun, bulan, renjaId, targets, pagus, dataSource));
        }

        return new RenjaOpdHierarkiResponse(dataItems);
    }

    private RenjaOpdHierarkiResponse.DataItem buildDataItem(
            String kodeOpd,
            String tahun,
            String bulan,
            String renjaId,
            List<RenjaTarget> allTargets,
            List<RenjaPagu> allPagus,
            DataSource dataSource
    ) {
        Map<String, Node> nodes = new HashMap<>();
        long paguTotal = 0;

        for (RenjaTarget target : allTargets) {
            if (!Objects.equals(renjaId, target.jenisRenjaId())) {
                continue;
            }
            String kodeRenja = target.kodeRenja();
            if (kodeRenja == null || kodeRenja.isBlank()) {
                continue;
            }
            Node node = nodes.computeIfAbsent(kodeRenja, ignored -> new Node());
            node.targets.add(new RenjaOpdHierarkiResponse.TargetItem(
                    target.id(),
                    target.targetId(),
                    target.target(),
                    target.realisasi() == null ? null : target.realisasi().toString(),
                    target.satuan(),
                    target.jenisRealisasi() == null ? null : target.jenisRealisasi().name(),
                    target.status() == null ? null : target.status().name(),
                    target.createdBy(),
                    target.lastModifiedBy(),
                    target.capaian(),
                    target.keteranganCapaian()
            ));
            if (target.indikatorId() != null || target.indikator() != null) {
                node.indikators.add(new RenjaOpdHierarkiResponse.IndikatorItem(target.indikatorId(), target.indikator()));
            }
        }

        for (RenjaPagu pagu : allPagus) {
            if (!Objects.equals(renjaId, pagu.jenisRenjaId())) {
                continue;
            }
            String kodeRenja = pagu.kodeRenja();
            if (kodeRenja == null || kodeRenja.isBlank()) {
                continue;
            }
            Node node = nodes.computeIfAbsent(kodeRenja, ignored -> new Node());
            node.pagus.add(new RenjaOpdHierarkiResponse.PaguItem(
                    pagu.id(),
                    pagu.realisasi() == null ? null : pagu.realisasi().toString(),
                    pagu.pagu(),
                    pagu.status() == null ? null : pagu.status().name(),
                    pagu.createdBy(),
                    pagu.lastModifiedBy(),
                    pagu.capaian(),
                    pagu.keteranganCapaian()
            ));
            paguTotal += pagu.realisasi() == null ? 0 : pagu.realisasi();
        }

        List<RenjaOpdHierarkiResponse.RenjaItem> programItems = buildProgramRoot(nodes, dataSource);
        return new RenjaOpdHierarkiResponse.DataItem(
                kodeOpd,
                tahun,
                bulan,
                paguTotal,
                renjaId,
                programItems
        );
    }

    private List<RenjaOpdHierarkiResponse.RenjaItem> buildProgramRoot(Map<String, Node> nodes, DataSource dataSource) {
        List<String> allCodes = new ArrayList<>(nodes.keySet());
        allCodes.sort(this::compareKodeRenja);

        List<RenjaOpdHierarkiResponse.RenjaItem> result = new ArrayList<>();
        for (String programCode : allCodes) {
            if (!isProgram(programCode)) {
                continue;
            }
            Node programNode = nodes.get(programCode);
            result.add(new RenjaOpdHierarkiResponse.RenjaItem(
                    programCode,
                    null,
                    "PROGRAM",
                    dedupTargets(programNode.targets),
                    dedupPagus(programNode.pagus),
                    dedupIndikators(programNode.indikators),
                    null,
                    buildKegiatan(nodes, allCodes, programCode, dataSource),
                    null
            ));
        }
        return result;
    }

    private List<RenjaOpdHierarkiResponse.RenjaItem> buildKegiatan(Map<String, Node> nodes, List<String> allCodes, String programCode, DataSource dataSource) {
        List<RenjaOpdHierarkiResponse.RenjaItem> result = new ArrayList<>();
        for (String kegiatanCode : allCodes) {
            if (!isKegiatan(kegiatanCode) || !isDirectChild(programCode, kegiatanCode)) {
                continue;
            }
            Node kegiatanNode = nodes.get(kegiatanCode);
            result.add(new RenjaOpdHierarkiResponse.RenjaItem(
                    kegiatanCode,
                    null,
                    "KEGIATAN",
                    dedupTargets(kegiatanNode.targets),
                    dedupPagus(kegiatanNode.pagus),
                    dedupIndikators(kegiatanNode.indikators),
                    null,
                    null,
                    buildSubkegiatan(nodes, allCodes, kegiatanCode, dataSource)
            ));
        }
        return result;
    }

    private List<RenjaOpdHierarkiResponse.RenjaItem> buildSubkegiatan(Map<String, Node> nodes, List<String> allCodes, String kegiatanCode, DataSource dataSource) {
        List<RenjaOpdHierarkiResponse.RenjaItem> result = new ArrayList<>();
        for (String subkegiatanCode : allCodes) {
            if (!isSubkegiatan(subkegiatanCode) || !isDirectChild(kegiatanCode, subkegiatanCode)) {
                continue;
            }
            Node subkegiatanNode = nodes.get(subkegiatanCode);
            result.add(new RenjaOpdHierarkiResponse.RenjaItem(
                    subkegiatanCode,
                    null,
                    "SUBKEGIATAN",
                    dedupTargets(subkegiatanNode.targets),
                    dedupPagus(subkegiatanNode.pagus),
                    dedupIndikators(subkegiatanNode.indikators),
                    null,
                    null,
                    null
            ));
        }
        return result;
    }

    private boolean isDirectChild(String parent, String child) {
        List<String> parentParts = splitKode(parent);
        List<String> childParts = splitKode(child);

        if (isProgram(parent) && isKegiatan(child)) {
            if (childParts.size() != parentParts.size() + 2) {
                return false;
            }
        } else if (isKegiatan(parent) && isSubkegiatan(child)) {
            if (childParts.size() != parentParts.size() + 1) {
                return false;
            }
        } else if (childParts.size() != parentParts.size() + 1) {
            return false;
        }

        for (int i = 0; i < parentParts.size(); i++) {
            if (!Objects.equals(parentParts.get(i), childParts.get(i))) {
                return false;
            }
        }
        return true;
    }

    private int levelOf(String kodeRenja) {
        return splitKode(kodeRenja).size();
    }

    private boolean isProgram(String kodeRenja) {
        return levelOf(kodeRenja) == 3;
    }

    private boolean isKegiatan(String kodeRenja) {
        return levelOf(kodeRenja) == 5;
    }

    private boolean isSubkegiatan(String kodeRenja) {
        return levelOf(kodeRenja) == 6;
    }

    private List<String> splitKode(String kodeRenja) {
        return Arrays.asList(kodeRenja.split("\\."));
    }

    // RANGKAI KODE RENJA
    private int compareKodeRenja(String a, String b) {
        List<String> aParts = splitKode(a);
        List<String> bParts = splitKode(b);
        int min = Math.min(aParts.size(), bParts.size());
        for (int i = 0; i < min; i++) {
            int cmp = comparePart(aParts.get(i), bParts.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(aParts.size(), bParts.size());
    }

    private int comparePart(String a, String b) {
        try {
            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        } catch (NumberFormatException ignored) {
            return a.compareTo(b);
        }
    }

    private List<RenjaOpdHierarkiResponse.TargetItem> dedupTargets(List<RenjaOpdHierarkiResponse.TargetItem> items) {
        LinkedHashMap<String, RenjaOpdHierarkiResponse.TargetItem> unique = new LinkedHashMap<>();
        for (RenjaOpdHierarkiResponse.TargetItem item : items) {
            String key = String.join("|",
                    item.targetRealisasiId() == null ? "" : item.targetRealisasiId().toString(),
                    item.idTarget() == null ? "" : item.idTarget(),
                    item.target() == null ? "" : item.target(),
                    item.realisasi() == null ? "" : item.realisasi(),
                    item.satuan() == null ? "" : item.satuan(),
                    item.jenisRealisasi() == null ? "" : item.jenisRealisasi(),
                    item.status() == null ? "" : item.status(),
                    item.createdBy() == null ? "" : item.createdBy(),
                    item.lastModifiedBy() == null ? "" : item.lastModifiedBy(),
                    item.capaian() == null ? "" : item.capaian(),
                    item.keteranganCapaian() == null ? "" : item.keteranganCapaian());
            unique.putIfAbsent(key, item);
        }
        return new ArrayList<>(unique.values());
    }

    private List<RenjaOpdHierarkiResponse.PaguItem> dedupPagus(List<RenjaOpdHierarkiResponse.PaguItem> items) {
        LinkedHashMap<String, RenjaOpdHierarkiResponse.PaguItem> unique = new LinkedHashMap<>();
        for (RenjaOpdHierarkiResponse.PaguItem item : items) {
            String key = String.join("|",
                    item.paguRealisasiId() == null ? "" : item.paguRealisasiId().toString(),
                    item.realisasi() == null ? "" : item.realisasi(),
                    item.pagu() == null ? "" : item.pagu().toString(),
                    item.status() == null ? "" : item.status(),
                    item.createdBy() == null ? "" : item.createdBy(),
                    item.lastModifiedBy() == null ? "" : item.lastModifiedBy(),
                    item.capaian() == null ? "" : item.capaian(),
                    item.keteranganCapaian() == null ? "" : item.keteranganCapaian());
            unique.putIfAbsent(key, item);
        }
        return new ArrayList<>(unique.values());
    }

    private List<RenjaOpdHierarkiResponse.IndikatorItem> dedupIndikators(List<RenjaOpdHierarkiResponse.IndikatorItem> items) {
        LinkedHashMap<String, RenjaOpdHierarkiResponse.IndikatorItem> unique = new LinkedHashMap<>();
        for (RenjaOpdHierarkiResponse.IndikatorItem item : items) {
            String key = (item.idIndikator() == null ? "" : item.idIndikator()) + "|" + (item.indikator() == null ? "" : item.indikator());
            unique.putIfAbsent(key, item);
        }
        return new ArrayList<>(unique.values());
    }

    private static class Node {
        final List<RenjaOpdHierarkiResponse.TargetItem> targets = new ArrayList<>();
        final List<RenjaOpdHierarkiResponse.PaguItem> pagus = new ArrayList<>();
        final List<RenjaOpdHierarkiResponse.IndikatorItem> indikators = new ArrayList<>();

        private Node() {
        }
    }
}
