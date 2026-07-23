package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenghambatSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenunjangSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.LaporanRealisasiSasaranResponse;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranRequest;
import cc.kertaskerja.integration.upload.UploadClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranPemdaClient;
import cc.kertaskerja.integration.penetapan.sasaran_pemda.PenetapanSasaranPemda;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.PenetapanSasaranPemdaListResponse;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranPemdaPenetapanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SasaranService {
    private final SasaranRepository sasaranRepository;
    private final UploadClient uploadClient;
    private final PenetapanSasaranPemdaClient penetapanClient;

    public SasaranService(SasaranRepository sasaranRepository, UploadClient uploadClient, PenetapanSasaranPemdaClient penetapanClient) {
        this.sasaranRepository = sasaranRepository;
        this.uploadClient = uploadClient;
        this.penetapanClient = penetapanClient;
    }

    public Flux<Sasaran> getAllRealisasiSasaranByTahunAndBulan(String tahun, String bulan) {
        return sasaranRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Mono<Sasaran> submitRealisasiSasaran(SasaranRequest req) {
        String bukti = req.buktiPendukung() != null ? req.buktiPendukung() : "";

            if (req.targetRealisasiId() != null) {
                return sasaranRepository.findById(req.targetRealisasiId())
                        .flatMap(existing -> sasaranRepository.save(buildUpdatedRealisasiSasaran(existing, req, bukti)))
                        .switchIfEmpty(Mono.defer(() -> {
                            Sasaran baru = buildUnchekcedRealisasiSasaran(
                                    req.kodeSasaranPemda(), req.kodeIndikator(), req.kodeTarget(),
                                    req.realisasi(), req.satuan(),
                                    req.tahun(), req.bulan(),
                                    req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                            return sasaranRepository.save(baru);
                        }));
            }
            return sasaranRepository
                    .findFirstByKodeSasaranPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                            req.kodeSasaranPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                    .flatMap(existing -> sasaranRepository.save(buildUpdatedRealisasiSasaran(existing, req, bukti)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Sasaran baru = buildUnchekcedRealisasiSasaran(
                                req.kodeSasaranPemda(), req.kodeIndikator(), req.kodeTarget(),
                                req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(),
                                req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                        return sasaranRepository.save(baru);
                    }));
    }

    private static Sasaran buildUpdatedRealisasiSasaran(Sasaran existing, SasaranRequest req, String buktiPendukung) {
        return new Sasaran(
                existing.id(),
                existing.kodeSasaranPemda(),
                existing.kodeIndikator(),
                existing.kodeTarget(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                SasaranStatus.UNCHECKED,
                buktiPendukung != null && !buktiPendukung.isBlank() ? buktiPendukung : existing.buktiPendukung(),
                req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
    }

    public static Sasaran buildUnchekcedRealisasiSasaran(String kodeSasaranPemda, String kodeIndikator, String kodeTarget, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String buktiPendukung, String keteranganBuktiPendukung) {
        return Sasaran.of(kodeSasaranPemda,
                kodeIndikator,
                kodeTarget, realisasi, satuan, tahun, bulan,
                "",
                "",
                jenisRealisasi,
                SasaranStatus.UNCHECKED,
                buktiPendukung,
                keteranganBuktiPendukung);
    }

    public Flux<LaporanRealisasiSasaranResponse> getLaporanRealisasi(String tahun, JenisLaporan jenisLaporan, String bulan) {
        return sasaranRepository.findAllByTahun(tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<Sasaran>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(s -> s.kodeIndikator() + "|" + s.kodeTarget()));
                    
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        Sasaran first = groupList.get(0);
                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(s -> bulan.equals(s.bulan()))
                                        .filter(s -> s.realisasi() != null)
                                        .mapToDouble(Sasaran::realisasi)
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (Sasaran s : groupList) {
                                    if (s.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(s.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, s.realisasi(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (Sasaran s : groupList) {
                                    if (s.realisasi() == null) continue;
                                    String key = s.bulan();
                                    bulanMap.merge(key, s.realisasi(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        return new LaporanRealisasiSasaranResponse(tahun, first.kodeIndikator(), first.kodeTarget(), jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Mono<Sasaran> updateFaktorPenunjang(FaktorPenunjangSasaranRequest req) {
        return sasaranRepository
                .findFirstByKodeSasaranPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(req.kodeSasaranPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(existing -> {
                    Sasaran updated = new Sasaran(
                            existing.id(),
                            existing.kodeSasaranPemda(),
                            existing.kodeIndikator(),
                            existing.kodeTarget(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            SasaranStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.keteranganBuktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranRepository.save(updated);
                });
    }

    public Mono<Sasaran> updateFaktorPenghambat(FaktorPenghambatSasaranRequest req) {
        return sasaranRepository
                .findFirstByKodeSasaranPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(req.kodeSasaranPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(existing -> {
                    Sasaran updated = new Sasaran(
                            existing.id(),
                            existing.kodeSasaranPemda(),
                            existing.kodeIndikator(),
                            existing.kodeTarget(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            SasaranStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.keteranganBuktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranRepository.save(updated);
                });
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }

    public Mono<String> syncSasaranPemda(int tahun) {
        return penetapanClient.syncSasaranPemda(tahun);
    }

    public Mono<PenetapanSasaranPemdaListResponse> getPenetapanWithRealisasi(int tahun, String bulan) {
        return penetapanClient.fetchSasaranPemda(tahun)
                .flatMap(penetapanList -> {
                    var rootTahun = resolveRootTahun(penetapanList, tahun);
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(buildResponseWithoutBulan(penetapanList, rootTahun));
                    }
                    return buildResponseWithBulan(penetapanList, rootTahun, bulan);
                });
    }

    private Integer resolveRootTahun(List<PenetapanSasaranPemda.SasaranPenetapanPemdaData> list, int tahun) {
        if (list.isEmpty()) {
            return tahun;
        }
        var first = list.getFirst();
        return first.tahunAktif() != null ? first.tahunAktif() : tahun;
    }

    private Mono<PenetapanSasaranPemdaListResponse> buildResponseWithBulan(
            List<PenetapanSasaranPemda.SasaranPenetapanPemdaData> penetapanList,
            Integer rootTahun,
            String bulan
    ) {
        String tahunStr = String.valueOf(rootTahun);
        return buildRealisasiLookup(tahunStr, bulan)
                .map(lookup -> {
                    List<SasaranPemdaPenetapanResponse> items = penetapanList.stream()
                            .map(p -> mergePenetapanWithRealisasi(p, lookup.get(p.kodeSasaranPemda())))
                            .filter(response -> !response.indikators().isEmpty())
                            .toList();
                    return new PenetapanSasaranPemdaListResponse(
                            rootTahun, parseInteger(bulan), items);
                });
    }

    private PenetapanSasaranPemdaListResponse buildResponseWithoutBulan(
            List<PenetapanSasaranPemda.SasaranPenetapanPemdaData> penetapanList,
            Integer rootTahun
    ) {
        List<SasaranPemdaPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanSasaranPemdaListResponse(rootTahun, null, items);
    }

    private Mono<Map<String, Map<String, Map<String, Sasaran>>>> buildRealisasiLookup(
            String tahun, String bulan) {
        return sasaranRepository.findAllByTahunAndBulan(tahun, bulan)
                .collectList()
                .map(records -> {
                    Map<String, Map<String, Map<String, Sasaran>>> lookup = new HashMap<>();
                    for (Sasaran r : records) {
                        lookup.computeIfAbsent(r.kodeSasaranPemda(), k -> new HashMap<>())
                                .computeIfAbsent(r.kodeIndikator(), k -> new HashMap<>())
                                .put(r.kodeTarget(), r);
                    }
                    return lookup;
                });
    }

    private SasaranPemdaPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranPemda.SasaranPenetapanPemdaData penetapan,
            Map<String, Map<String, Sasaran>> indikatorLookup
    ) {
        List<SasaranPemdaPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorLookup))
                .filter(java.util.Objects::nonNull)
                .toList();

        return new SasaranPemdaPenetapanResponse(
                penetapan.id(), penetapan.kodeSasaranPemda(), penetapan.sasaranPemda(), indikatorList
        );
    }

    private SasaranPemdaPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanSasaranPemda.IndikatorSasaranPemdaData ind,
            Map<String, Map<String, Sasaran>> indikatorLookup
    ) {
        Map<String, Sasaran> targetMap = indikatorLookup != null
                ? indikatorLookup.get(ind.kodeIndikator())
                : null;

        List<SasaranPemdaPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .map(t -> mergeTarget(t, targetMap))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new SasaranPemdaPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private SasaranPemdaPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanSasaranPemda.TargetSasaranPemdaData t,
            Map<String, Sasaran> targetMap
    ) {
        Sasaran matched = targetMap != null ? targetMap.get(t.kodeTarget()) : null;
        Double realisasiValue = matched != null ? matched.realisasi() : null;
        String faktorPenunjang = matched != null ? matched.faktorPenunjang() : null;
        String faktorPenghambat = matched != null ? matched.faktorPenghambat() : null;
        String buktiPendukung = matched != null ? matched.buktiPendukung() : null;
        String keteranganBuktiPendukung = matched != null ? matched.keteranganBuktiPendukung() : null;
        
        Double capaian = matched != null ? matched.hitungCapaian(t.target()) : null;
        String keteranganCapaian = matched != null ? matched.keteranganCapaian(t.target()) : null;
        
        return new SasaranPemdaPenetapanResponse.TargetPenetapan(
                t.kodeTarget(), t.satuan(), t.target(),
                realisasiValue, capaian, keteranganCapaian,
                faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung
        );
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
