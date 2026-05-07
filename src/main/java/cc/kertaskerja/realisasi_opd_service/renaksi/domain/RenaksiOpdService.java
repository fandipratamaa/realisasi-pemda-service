package cc.kertaskerja.realisasi_opd_service.renaksi.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.RenaksiOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.detail_bulanan_response.RenaksiOpdDetailBulananResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.detail_bulanan_response.RenaksiOpdDetailBulananRow;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.renaksi_triwulan_response.RenaksiTriwulanRekapResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.renaksi_triwulan_response.TriwulanDetailResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RenaksiOpdService {
    private final RenaksiOpdRepository renaksiOpdRepository;

    public RenaksiOpdService(RenaksiOpdRepository renaksiOpdRepository) {
        this.renaksiOpdRepository = renaksiOpdRepository;
    }

    public Mono<RenaksiOpd> submitRealisasiRenaksi(RenaksiOpdRequest req) {
        return Mono.just(buildUncheckedRealisasiRenaksi(req)).flatMap(renaksiOpdRepository::save);
    }

    public Flux<RenaksiOpd> batchSubmitRealisasiRenaksi(@Valid List<RenaksiOpdRequest> requests) {
        return Flux.fromIterable(requests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renaksiOpdRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> renaksiOpdRepository.save(buildUpdated(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> renaksiOpdRepository.save(buildUncheckedRealisasiRenaksi(req))));
                    }

                    return renaksiOpdRepository
                            .findFirstByKodeOpdAndBulanAndRekinIdAndRenaksiId(req.kodeOpd(), req.bulan(), req.rekinId(), req.renaksiId())
                            .flatMap(existing -> renaksiOpdRepository.save(buildUpdated(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renaksiOpdRepository.save(buildUncheckedRealisasiRenaksi(req))));
                });
    }

    public Mono<Void> deleteRealisasiRenaksi(Long id) {
        return renaksiOpdRepository.deleteById(id);
    }

    public Flux<RenaksiTriwulanRekapResponse> getRekapTriwulanByTahun(String kodeOpd, String tahun) {
        return renaksiOpdRepository.findAllByKodeOpdAndTahun(kodeOpd, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<GroupKey, List<RenaksiOpd>> grouped = new LinkedHashMap<>();
                    for (RenaksiOpd item : list) {
                        GroupKey key = new GroupKey(item.renaksiId(), item.renaksi(), item.rekinId(), item.rekin(), item.targetId());
                        grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
                    }

                    return Flux.fromIterable(grouped.entrySet().stream().map(e -> buildRow(e.getKey(), e.getValue())).toList());
                });
    }

    public Flux<RenaksiOpd> getRealisasiRenaksiByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Mono<RenaksiOpdDetailBulananResponse> getDetailBulanan(
            String kodeOpd,
            String tahun,
            String triwulan,
            String renaksiId,
            String targetId
    ) {
        Integer tw = parseTriwulan(triwulan);
        if (tw == null) {
            return Mono.error(new IllegalArgumentException("Parameter triwulan harus 1-4"));
        }

        List<String> months = monthsByTriwulan(tw);

        return renaksiOpdRepository
                .findAllByKodeOpdAndTahunAndRenaksiIdAndTargetId(kodeOpd, tahun, renaksiId, targetId)
                .collectList()
                .map(items -> {
                    Map<String, Integer> sumByMonth = items.stream()
                            .filter(it -> it.bulan() != null)
                            .collect(Collectors.toMap(
                                    it -> it.bulan().trim(),
                                    it -> it.realisasi() == null ? 0 : it.realisasi(),
                                    Integer::sum,
                                    LinkedHashMap::new
                            ));

                    List<RenaksiOpdDetailBulananRow> rows = months.stream()
                            .map(m -> new RenaksiOpdDetailBulananRow(renaksiId, targetId, m, sumByMonth.getOrDefault(m, 0)))
                            .toList();

                    return new RenaksiOpdDetailBulananResponse(kodeOpd, tahun, rows);
                });
    }

    private RenaksiOpd buildUncheckedRealisasiRenaksi(RenaksiOpdRequest req) {
        return RenaksiOpd.of(req.renaksiId(), req.renaksi(), req.rekinId(), req.rekin(), req.targetId(), req.target(),
                req.realisasi(), req.satuan(), req.bulan(), req.tahun(), req.jenisRealisasi(), req.kodeOpd(), RenaksiOpdStatus.UNCHECKED);
    }

    private RenaksiOpd buildUpdated(RenaksiOpd existing, RenaksiOpdRequest req) {
        return new RenaksiOpd(existing.id(), existing.renaksiId(), existing.renaksi(), existing.rekinId(), existing.rekin(),
                existing.targetId(), existing.target(), req.realisasi(), req.satuan(), req.bulan(), req.tahun(), req.jenisRealisasi(),
                req.kodeOpd(), RenaksiOpdStatus.UNCHECKED, existing.createdBy(), existing.lastModifiedBy(), existing.createdDate(),
                existing.lastModifiedDate(), existing.version());
    }

    private RenaksiTriwulanRekapResponse buildRow(GroupKey key, List<RenaksiOpd> items) {
        Map<Integer, List<RenaksiOpd>> triwulan = Map.of(1, new ArrayList<RenaksiOpd>(), 2, new ArrayList<>(), 3, new ArrayList<>(), 4, new ArrayList<>());
        for (RenaksiOpd item : items) {
            int tw = toTriwulan(item.bulan());
            if (tw != 0) triwulan.get(tw).add(item);
        }

        return new RenaksiTriwulanRekapResponse(
                key.renaksiId(), key.renaksi(), key.rekinId(), key.rekin(), key.targetId(),
                buildTriwulanDetail(triwulan.get(1), items),
                buildTriwulanDetail(triwulan.get(2), items),
                buildTriwulanDetail(triwulan.get(3), items),
                buildTriwulanDetail(triwulan.get(4), items)
        );
    }

    private TriwulanDetailResponse buildTriwulanDetail(List<RenaksiOpd> twItems, List<RenaksiOpd> allItems) {
        String targetRaw = allItems.stream().map(RenaksiOpd::target).filter(Objects::nonNull).findFirst().orElse("0");
        BigDecimal target = parseBigDecimal(targetRaw);
        int realisasi = twItems.stream().map(RenaksiOpd::realisasi).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        String satuan = twItems.stream().map(RenaksiOpd::satuan).filter(Objects::nonNull).findFirst()
                .orElseGet(() -> allItems.stream().map(RenaksiOpd::satuan).filter(Objects::nonNull).findFirst().orElse(null));
        JenisRealisasi jenis = twItems.stream().map(RenaksiOpd::jenisRealisasi).filter(Objects::nonNull).findFirst()
                .orElseGet(() -> allItems.stream().map(RenaksiOpd::jenisRealisasi).filter(Objects::nonNull).findFirst().orElse(JenisRealisasi.NAIK));

        double capaianValue = new Capaian((double) realisasi, target.toPlainString(), jenis).hasilCapaian();
        String capaian = BigDecimal.valueOf(capaianValue).stripTrailingZeros().toPlainString();
        String keterangan = capaianValue > 100 ? "nilai capaian lebih dari 100%" : null;

        return new TriwulanDetailResponse(target, realisasi, satuan, capaian, keterangan);
    }

    private int toTriwulan(String bulan) {
        Integer m = toMonthNumber(bulan);
        if (m == null) return 0;
        return ((m - 1) / 3) + 1;
    }

    private Integer parseTriwulan(String triwulan) {
        if (triwulan == null || triwulan.isBlank()) return null;
        try {
            int n = Integer.parseInt(triwulan.trim());
            return (n >= 1 && n <= 4) ? n : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private List<String> monthsByTriwulan(int triwulan) {
        int start = ((triwulan - 1) * 3) + 1;
        return List.of(String.valueOf(start), String.valueOf(start + 1), String.valueOf(start + 2));
    }

    private Integer toMonthNumber(String bulan) {
        if (bulan == null || bulan.isBlank()) return null;
        try {
            int n = Integer.parseInt(bulan.trim());
            return (n >= 1 && n <= 12) ? n : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (Exception ignored) {
            return BigDecimal.ZERO;
        }
    }

    private record GroupKey(String renaksiId, String renaksi, String rekinId, String rekin, String targetId) {
    }
}
