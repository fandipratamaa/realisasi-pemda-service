package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenghambatRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenunjangRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.LaporanRealisasiTujuanResponse;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanResponse;
import cc.kertaskerja.integration.penetapan.PenetapanTujuanPemdaClient;
import cc.kertaskerja.integration.penetapan.tujuan_pemda.PenetapanTujuanPemda;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.PenetapanTujuanPemdaListResponse;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanPemdaPenetapanResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TujuanService {
    private static final Logger log = LoggerFactory.getLogger(TujuanService.class);
    private final TujuanRepository tujuanRepository;
    private final UploadClient uploadClient;
    private final PenetapanTujuanPemdaClient penetapanClient;

    public TujuanService(TujuanRepository tujuanRepository, UploadClient uploadClient, PenetapanTujuanPemdaClient penetapanClient) {
        this.tujuanRepository = tujuanRepository;
        this.uploadClient = uploadClient;
        this.penetapanClient = penetapanClient;
    }

    public Mono<TujuanResponse> submitRealisasiTujuan(TujuanRequest req) {
        String bukti = req.buktiPendukung() != null ? req.buktiPendukung() : "";
        
        Mono<Tujuan> savedMono;
        if (req.targetRealisasiId() != null) {
            savedMono = tujuanRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req, bukti)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.kodeTujuanPemda(), req.kodeIndikator(), req.kodeTarget(),
                                req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(),
                                req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                        return tujuanRepository.save(baru);
                    }));
        } else {
            savedMono = tujuanRepository
                    .findFirstByKodeTujuanPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                            req.kodeTujuanPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                    .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req, bukti)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.kodeTujuanPemda(), req.kodeIndikator(), req.kodeTarget(),
                                req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(),
                                req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                        return tujuanRepository.save(baru);
                    }));
        }
        
        return savedMono.map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req.tahun()));
    }

    private TujuanResponse toResponse(Tujuan entity) {
        return new TujuanResponse(
                entity.id(), null, null, entity.kodeTujuanPemda(), entity.kodeIndikator(), entity.kodeTarget(),
                entity.realisasi(), entity.satuan(), entity.tahun(), entity.bulan(),
                entity.faktorPenunjang(), entity.faktorPenghambat(),
                null, null, null, null, null, null, null, null,
                entity.jenisRealisasi(), entity.createdBy(), entity.lastModifiedBy(),
                entity.buktiPendukung(), entity.keteranganBuktiPendukung()
        );
    }

    private Mono<TujuanResponse> applyPenetapan(Mono<TujuanResponse> responseMono, String tahun) {
        Mono<List<PenetapanTujuanPemda.TujuanPenetapanPemdaData>> penetapanData =
                penetapanClient.fetchTujuanPemda(Integer.parseInt(tahun));

        return responseMono.zipWith(penetapanData.defaultIfEmpty(List.of()), (response, penetapanList) -> {
            PenetapanTujuanPemda.TujuanPenetapanPemdaData matching = penetapanList.stream()
                    .filter(p -> p.kodeTujuanPemda().equals(response.kodeTujuanPemda()))
                    .findFirst()
                    .orElse(null);
            if (matching == null) {
                return response;
            }
            return applyPenetapanToResponse(response, matching);
        }).onErrorResume(e -> {
            log.warn("Gagal terhubung dengan response penetapan untuk tahun={}: {}", tahun, e.getMessage());
            return responseMono;
        });
    }

    private TujuanResponse applyPenetapanToResponse(
            TujuanResponse response,
            PenetapanTujuanPemda.TujuanPenetapanPemdaData penetapan
    ) {
        var matchedIndikator = penetapan.indikators().stream()
                .filter(i -> i.kodeIndikator().equals(response.kodeIndikator()))
                .findFirst();

        if (matchedIndikator.isEmpty()) {
            return new TujuanResponse(
                    response.id(), penetapan.visi(), penetapan.misi(), response.kodeTujuanPemda(), response.kodeIndikator(), response.kodeTarget(),
                    response.realisasi(), response.satuan(), response.tahun(), response.bulan(),
                    response.faktorPenunjang(), response.faktorPenghambat(),
                    penetapan.tujuanPemda(), null, null, null, null, null, null, null,
                    response.jenisRealisasi(), response.createdBy(), response.lastModifiedBy(),
                    response.buktiPendukung(), response.keteranganBuktiPendukung()
            );
        }

        var matchedTarget = matchedIndikator.get().targets().stream()
                .filter(t -> t.kodeTarget().equals(response.kodeTarget()))
                .findFirst();

        Double target = matchedTarget.map(PenetapanTujuanPemda.TargetPenetapanPemdaData::target).orElse(null);

        Double capaian = null;
        String keteranganCapaian = null;
        if (response.realisasi() != null && target != null && target != 0 && response.realisasi() != 0) {
            cc.kertaskerja.capaian.domain.Capaian capaianObj = new cc.kertaskerja.capaian.domain.Capaian(
                    response.realisasi(), String.valueOf(target), response.jenisRealisasi());
            capaian = capaianObj.hasilCapaian();
            if (capaian > 100) {
                keteranganCapaian = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", capaian) + ")";
                capaian = 100.0;
            }
        }

        return new TujuanResponse(
                response.id(), penetapan.visi(), penetapan.misi(), response.kodeTujuanPemda(), response.kodeIndikator(), response.kodeTarget(),
                response.realisasi(), response.satuan(), response.tahun(), response.bulan(),
                response.faktorPenunjang(), response.faktorPenghambat(),
                penetapan.tujuanPemda(),
                matchedIndikator.get().indikator(),
                matchedIndikator.get().rumusPerhitungan(),
                matchedIndikator.get().sumberData(),
                matchedIndikator.get().definisiOperasional(),
                target, capaian, keteranganCapaian,
                response.jenisRealisasi(), response.createdBy(), response.lastModifiedBy(),
                response.buktiPendukung(), response.keteranganBuktiPendukung()
        );
    }

    private static Tujuan buildUpdatedRealisasiTujuan(Tujuan existing, TujuanRequest req, String buktiPendukung) {
        return new Tujuan(
                existing.id(),
                existing.kodeTujuanPemda(),
                existing.kodeIndikator(),
                existing.kodeTarget(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                TujuanStatus.UNCHECKED,
                buktiPendukung != null && !buktiPendukung.isBlank() ? buktiPendukung : existing.buktiPendukung(),
                req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
    }

    public static Tujuan buildUncheckedRealisasiTujuan(String kodeTujuanPemda, String kodeIndikator, String kodeTarget, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String buktiPendukung, String keteranganBuktiPendukung) {
        return Tujuan.of(kodeTujuanPemda,
                kodeIndikator,
                kodeTarget, realisasi, satuan, tahun, bulan,
                "",
                "",
                jenisRealisasi,
                TujuanStatus.UNCHECKED,
                buktiPendukung,
                keteranganBuktiPendukung);
    }

    public Flux<Tujuan> getRealisasiTujuanByTahunAndBulan(String tahun, String bulan) {
        return tujuanRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Flux<LaporanRealisasiTujuanResponse> getLaporanRealisasi(String tahun, JenisLaporan jenisLaporan, String bulan) {
        Mono<List<PenetapanTujuanPemda.TujuanPenetapanPemdaData>> penetapanMono = penetapanClient.fetchTujuanPemda(Integer.parseInt(tahun))
                .onErrorReturn(List.of());

        return Mono.zip(tujuanRepository.findAllByTahun(tahun).collectList(), penetapanMono)
                .flatMapMany(tuple -> {
                    List<Tujuan> list = tuple.getT1();
                    List<PenetapanTujuanPemda.TujuanPenetapanPemdaData> penetapanList = tuple.getT2();

                    Map<String, List<Tujuan>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        Tujuan first = groupList.get(0);
                        
                        String indikatorName = first.kodeIndikator();
                        String targetName = first.kodeTarget();

                        for (var p : penetapanList) {
                            for (var ind : p.indikators()) {
                                if (ind.kodeIndikator().equals(first.kodeIndikator())) {
                                    indikatorName = ind.indikator();
                                    for (var tgt : ind.targets()) {
                                        if (tgt.kodeTarget().equals(first.kodeTarget())) {
                                            targetName = tgt.target() != null ? String.valueOf(tgt.target()) : first.kodeTarget();
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(t -> bulan.equals(t.bulan()))
                                        .filter(t -> t.realisasi() != null)
                                        .mapToDouble(Tujuan::realisasi)
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (Tujuan t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (Tujuan t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    String key = t.bulan();
                                    bulanMap.merge(key, t.realisasi(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        return new LaporanRealisasiTujuanResponse(tahun, indikatorName, targetName, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Mono<TujuanResponse> updateFaktorPenunjang(FaktorPenunjangRequest req) {
        return tujuanRepository
                .findFirstByKodeTujuanPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(req.kodeTujuanPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.kodeTujuanPemda(),
                            existing.kodeIndikator(),
                            existing.kodeTarget(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.keteranganBuktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                })
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req.tahun()));
    }

    public Mono<TujuanResponse> updateFaktorPenghambat(FaktorPenghambatRequest req) {
        return tujuanRepository
                .findFirstByKodeTujuanPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(req.kodeTujuanPemda(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.kodeTujuanPemda(),
                            existing.kodeIndikator(),
                            existing.kodeTarget(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.keteranganBuktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                })
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req.tahun()));
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }

    public Mono<String> syncPenetapanTujuanPemda(int tahun) {
        return penetapanClient.syncTujuanPemda(tahun);
    }

    public Mono<PenetapanTujuanPemdaListResponse> getPenetapanWithRealisasi(int tahun, String bulan) {
        return penetapanClient.fetchTujuanPemda(tahun)
                .flatMap(penetapanList -> {
                    var rootTahun = resolveRootTahun(penetapanList, tahun);
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(buildResponseWithoutBulan(penetapanList, rootTahun));
                    }
                    return buildResponseWithBulan(penetapanList, rootTahun, bulan);
                });
    }

    private Integer resolveRootTahun(List<PenetapanTujuanPemda.TujuanPenetapanPemdaData> list, int tahun) {
        if (list.isEmpty()) {
            return tahun;
        }
        var first = list.getFirst();
        return first.tahunAktif() != null ? first.tahunAktif() : tahun;
    }

    private Mono<PenetapanTujuanPemdaListResponse> buildResponseWithBulan(
            List<PenetapanTujuanPemda.TujuanPenetapanPemdaData> penetapanList,
            Integer rootTahun,
            String bulan
    ) {
        String tahunStr = String.valueOf(rootTahun);
        return buildRealisasiLookup(tahunStr, bulan)
                .map(lookup -> {
                    List<TujuanPemdaPenetapanResponse> items = penetapanList.stream()
                            .map(p -> mergePenetapanWithRealisasi(p, lookup.get(p.kodeTujuanPemda())))
                            .filter(response -> !response.indikators().isEmpty())
                            .toList();
                    return new PenetapanTujuanPemdaListResponse(
                            rootTahun, parseInteger(bulan), items);
                });
    }

    private PenetapanTujuanPemdaListResponse buildResponseWithoutBulan(
            List<PenetapanTujuanPemda.TujuanPenetapanPemdaData> penetapanList,
            Integer rootTahun
    ) {
        List<TujuanPemdaPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanTujuanPemdaListResponse(rootTahun, null, items);
    }

    private Mono<Map<String, Map<String, Map<String, Tujuan>>>> buildRealisasiLookup(
            String tahun, String bulan) {
        return tujuanRepository.findAllByTahunAndBulan(tahun, bulan)
                .collectList()
                .map(records -> {
                    Map<String, Map<String, Map<String, Tujuan>>> lookup = new HashMap<>();
                    for (Tujuan r : records) {
                        lookup.computeIfAbsent(r.kodeTujuanPemda(), k -> new HashMap<>())
                                .computeIfAbsent(r.kodeIndikator(), k -> new HashMap<>())
                                .put(r.kodeTarget(), r);
                    }
                    return lookup;
                });
    }

    private TujuanPemdaPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanTujuanPemda.TujuanPenetapanPemdaData penetapan,
            Map<String, Map<String, Tujuan>> indikatorLookup
    ) {
        List<TujuanPemdaPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorLookup))
                .filter(java.util.Objects::nonNull)
                .toList();

        return new TujuanPemdaPenetapanResponse(
                penetapan.id(), penetapan.visi(), penetapan.misi(), penetapan.kodeTujuanPemda(), penetapan.tujuanPemda(), indikatorList
        );
    }

    private TujuanPemdaPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanTujuanPemda.IndikatorPenetapanPemdaData ind,
            Map<String, Map<String, Tujuan>> indikatorLookup
    ) {
        Map<String, Tujuan> targetMap = indikatorLookup != null
                ? indikatorLookup.get(ind.kodeIndikator())
                : null;

        List<TujuanPemdaPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .map(t -> mergeTarget(t, targetMap))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new TujuanPemdaPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private TujuanPemdaPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanTujuanPemda.TargetPenetapanPemdaData t,
            Map<String, Tujuan> targetMap
    ) {
        Tujuan matched = targetMap != null ? targetMap.get(t.kodeTarget()) : null;
        Double realisasiValue = matched != null ? matched.realisasi() : null;
        String faktorPenunjang = matched != null ? matched.faktorPenunjang() : null;
        String faktorPenghambat = matched != null ? matched.faktorPenghambat() : null;
        String buktiPendukung = matched != null ? matched.buktiPendukung() : null;
        String keteranganBuktiPendukung = matched != null ? matched.keteranganBuktiPendukung() : null;
        
        Double capaian = matched != null ? matched.hitungCapaian(t.target()) : null;
        String keteranganCapaian = matched != null ? matched.keteranganCapaian(t.target()) : null;
        
        return new TujuanPemdaPenetapanResponse.TargetPenetapan(
                t.kodeTarget(), t.satuan(), t.target(),
                realisasiValue, capaian, keteranganCapaian,
                faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung
        );
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
