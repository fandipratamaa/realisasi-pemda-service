package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.integration.penetapan.PenetapanTujuanOpdClient;
import cc.kertaskerja.integration.penetapan.tujuan_opd.PenetapanTujuanOpd;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.PenetapanTujuanOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.faktor_penghambat.FaktorPenghambatTujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.faktor_penunjang.FaktorPenunjangTujuanOpdRequest;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.LaporanRealisasiTujuanOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

@Service
public class TujuanOpdService {
    private static final Logger log = LoggerFactory.getLogger(TujuanOpdService.class);
    private final TujuanOpdRepository tujuanOpdRepository;
    private final PenetapanTujuanOpdClient penetapanClient;
    private final UploadClient uploadClient;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public TujuanOpdService(
            TujuanOpdRepository tujuanOpdRepository,
            PenetapanTujuanOpdClient penetapanClient,
            UploadClient uploadClient
    ) {
        this.tujuanOpdRepository = tujuanOpdRepository;
        this.penetapanClient = penetapanClient;
        this.uploadClient = uploadClient;
    }

    public Flux<TujuanOpdResponse> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return tujuanOpdRepository
                .findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), kodeOpd, tahun));
    }

    public Mono<PenetapanTujuanOpdListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchTujuanOpd(kodeOpd, tahun)
                .flatMap(penetapanList -> {
                    var root = resolveRootInfo(penetapanList, kodeOpd, tahun);
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(buildResponseWithoutBulan(penetapanList, root));
                    }
                    return buildResponseWithBulan(penetapanList, root, kodeOpd, bulan);
                });
    }

    public Mono<TujuanOpdResponse> submitRealisasiTujuanOpd(TujuanOpdRequest req) {
        return upsert(req)
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<TujuanOpdResponse> batchSubmitRealisasiTujuanOpd(List<TujuanOpdRequest> tujuanOpdRequests) {
        return Flux.fromIterable(tujuanOpdRequests)
                .flatMap(this::submitRealisasiTujuanOpd);
    }

    public Mono<TujuanOpd> updateFaktorPenunjang(FaktorPenunjangTujuanOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.kodeTujuanOpd(), req.kodeIndikator(), req.kodeTarget(),
                req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenunjang(req.faktorPenunjang()));
    }

    public Mono<TujuanOpd> updateFaktorPenghambat(FaktorPenghambatTujuanOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.kodeTujuanOpd(), req.kodeIndikator(), req.kodeTarget(),
                req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenghambat(req.faktorPenghambat()));
    }

    public Flux<LaporanRealisasiTujuanOpdResponse> getLaporanRealisasi(String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        Mono<List<PenetapanTujuanOpd.TujuanPenetapanData>> penetapanMono = penetapanClient.fetchTujuanOpd(kodeOpd, Integer.parseInt(tahun))
                .onErrorReturn(List.of());

        return Mono.zip(tujuanOpdRepository.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList(), penetapanMono)
                .flatMapMany(tuple -> {
                    List<TujuanOpd> list = tuple.getT1();
                    List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList = tuple.getT2();

                    Map<String, List<TujuanOpd>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));

                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        TujuanOpd first = groupList.get(0);
                        
                        String indikatorName = first.kodeIndikator();
                        String targetName = first.kodeTarget();

                        for (var p : penetapanList) {
                            for (var ind : p.indikators()) {
                                if (ind.kodeIndikator().equals(first.kodeIndikator())) {
                                    indikatorName = ind.indikator();
                                    for (var tgt : ind.targets()) {
                                        if (tgt.kodeTarget().equals(first.kodeTarget())) {
                                            // As target is Double in Penetapan, convert to string
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
                                        .mapToDouble(t -> t.realisasi().doubleValue())
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (TujuanOpd t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi().doubleValue(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (TujuanOpd t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    String key = t.bulan();
                                    bulanMap.merge(key, t.realisasi().doubleValue(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };

                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        
                        return new LaporanRealisasiTujuanOpdResponse(tahun, kodeOpd, indikatorName, targetName, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    private Mono<TujuanOpd> upsert(TujuanOpdRequest req) {
        JenisRealisasi jenisRealisasi = req.jenisRealisasi() != null
                ? req.jenisRealisasi()
                : JenisRealisasi.NAIK;
        String bukti = req.buktiPendukung() != null ? req.buktiPendukung() : "";
        return tujuanOpdRepository
                .findFirstByKodeOpdAndKodeTujuanOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeTujuanOpd(), req.kodeIndikator(),
                        req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> tujuanOpdRepository.save(
                        new TujuanOpd(
                                existing.id(), req.kodeOpd(), req.tahun(), req.bulan(),
                                req.kodeTujuanOpd(), req.kodeIndikator(), req.kodeTarget(),
                                BigDecimal.valueOf(req.realisasi()),
                                jenisRealisasi,
                                existing.faktorPenunjang(), existing.faktorPenghambat(),
                                bukti != null && !bukti.isBlank() ? bukti : existing.buktiPendukung(),
                                req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                                existing.createdBy(), existing.createdDate(), null, null)))
                .switchIfEmpty(Mono.defer(() -> tujuanOpdRepository.save(
                        TujuanOpd.of(
                                req.kodeOpd(), req.tahun(), req.bulan(),
                                req.kodeTujuanOpd(), req.kodeIndikator(), req.kodeTarget(),
                                BigDecimal.valueOf(req.realisasi()),
                                jenisRealisasi, bukti, req.keteranganBuktiPendukung()))));
    }

    private TujuanOpdResponse toResponse(TujuanOpd entity) {
        return new TujuanOpdResponse(
                entity.id(), entity.kodeOpd(),
                parseInteger(entity.tahun()), parseInteger(entity.bulan()),
                entity.kodeTujuanOpd(), entity.kodeIndikator(), entity.kodeTarget(),
                entity.realisasi() != null ? entity.realisasi().doubleValue() : null,
                entity.faktorPenunjang(), entity.faktorPenghambat(),
                null, null, null, null, null, null, null, null, null,
                entity.jenisRealisasi(),
                entity.createdBy(), entity.lastModifiedBy(), entity.buktiPendukung(),
                entity.keteranganBuktiPendukung());
    }

    private Mono<TujuanOpdResponse> applyPenetapan(Mono<TujuanOpdResponse> responseMono, String kodeOpd, String tahun) {
        Mono<List<PenetapanTujuanOpd.TujuanPenetapanData>> penetapanData =
                penetapanClient.fetchTujuanOpd(kodeOpd, Integer.parseInt(tahun));

        return responseMono.zipWith(penetapanData.defaultIfEmpty(List.of()), (response, penetapanList) -> {
            PenetapanTujuanOpd.TujuanPenetapanData matching = penetapanList.stream()
                    .filter(p -> p.kodeTujuanOpd().equals(response.kodeTujuanOpd()))
                    .findFirst()
                    .orElse(null);
            if (matching == null) {
                return response;
            }
            return applyPenetapanToResponse(response, matching);
        }).onErrorResume(e -> {
            log.warn("Gagal terhubung dengan response penetapan untuk kodeOpd={}, tahun={}: {}",
                    kodeOpd, tahun, e.getMessage());
            return responseMono;
        });
    }

    private TujuanOpdResponse applyPenetapanToResponse(
            TujuanOpdResponse response,
            PenetapanTujuanOpd.TujuanPenetapanData penetapan
    ) {
        var matchedIndikator = penetapan.indikators().stream()
                .filter(i -> i.kodeIndikator().equals(response.kodeIndikator()))
                .findFirst();

        if (matchedIndikator.isEmpty()) {
            return new TujuanOpdResponse(
                    response.id(), response.kodeOpd(), response.tahun(), response.bulan(),
                    response.kodeTujuanOpd(), response.kodeIndikator(), response.kodeTarget(),
                    response.realisasi(), response.faktorPenunjang(), response.faktorPenghambat(),
                    penetapan.tujuanOpd(), null, null, null, null, null, null, null, null,
                    response.jenisRealisasi(),
                    response.createdBy(), response.lastModifiedBy(), response.buktiPendukung(),
                    response.keteranganBuktiPendukung());
        }

        var matchedTarget = matchedIndikator.get().targets().stream()
                .filter(t -> t.kodeTarget().equals(response.kodeTarget()))
                .findFirst();

        Double target = matchedTarget.map(PenetapanTujuanOpd.TargetPenetapanData::target).orElse(null);
        String satuan = matchedTarget.map(PenetapanTujuanOpd.TargetPenetapanData::satuan).orElse(null);
        var capaianResult = TujuanOpd.hitungCapaian(response.realisasi(), target);

        return new TujuanOpdResponse(
                response.id(), response.kodeOpd(), response.tahun(), response.bulan(),
                response.kodeTujuanOpd(), response.kodeIndikator(), response.kodeTarget(),
                response.realisasi(), response.faktorPenunjang(), response.faktorPenghambat(),
                penetapan.tujuanOpd(),
                matchedIndikator.get().indikator(),
                matchedIndikator.get().rumusPerhitungan(),
                matchedIndikator.get().sumberData(),
                matchedIndikator.get().definisiOperasional(),
                target, satuan,
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                response.jenisRealisasi(),
                response.createdBy(), response.lastModifiedBy(), response.buktiPendukung(),
                response.keteranganBuktiPendukung()
        );
    }

    private Mono<Map<String, Map<String, Map<String, TujuanOpd>>>> buildRealisasiLookup(
            String kodeOpd, String tahun, String bulan) {
        return tujuanOpdRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .collectList()
                .map(records -> {
                    Map<String, Map<String, Map<String, TujuanOpd>>> lookup = new HashMap<>();
                    for (TujuanOpd r : records) {
                        lookup.computeIfAbsent(r.kodeTujuanOpd(), k -> new HashMap<>())
                                .computeIfAbsent(r.kodeIndikator(), k -> new HashMap<>())
                                .put(r.kodeTarget(), r);
                    }
                    return lookup;
                });
    }

    private record PenetapanInfo(String kodeOpd, Integer tahun) {}

    private PenetapanInfo resolveRootInfo(List<PenetapanTujuanOpd.TujuanPenetapanData> list, String kodeOpd, int tahun) {
        if (list.isEmpty()) {
            return new PenetapanInfo(kodeOpd, tahun);
        }
        var first = list.getFirst();
        return new PenetapanInfo(
                first.kodeOpd() != null ? first.kodeOpd() : kodeOpd,
                first.tahunAktif() != null ? first.tahunAktif() : tahun
        );
    }

    private Mono<PenetapanTujuanOpdListResponse> buildResponseWithBulan(
            List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList,
            PenetapanInfo root,
            String kodeOpd,
            String bulan
    ) {
        String tahunStr = String.valueOf(root.tahun());
        return buildRealisasiLookup(kodeOpd, tahunStr, bulan)
                .map(lookup -> {
                    List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                            .map(p -> mergePenetapanWithRealisasi(p, lookup.get(p.kodeTujuanOpd())))
                            .filter(response -> !response.indikators().isEmpty())
                            .toList();
                    return new PenetapanTujuanOpdListResponse(
                            root.kodeOpd(), root.tahun(), parseInteger(bulan), items);
                });
    }

    private PenetapanTujuanOpdListResponse buildResponseWithoutBulan(
            List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList,
            PenetapanInfo root
    ) {
        List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanTujuanOpdListResponse(root.kodeOpd(), root.tahun(), null, items);
    }

    private TujuanOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanTujuanOpd.TujuanPenetapanData penetapan,
            Map<String, Map<String, TujuanOpd>> indikatorLookup
    ) {
        List<TujuanOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorLookup))
                .filter(Objects::nonNull)
                .toList();

        return new TujuanOpdPenetapanResponse(
                penetapan.id(), penetapan.kodeTujuanOpd(), penetapan.tujuanOpd(), indikatorList
        );
    }

    private TujuanOpdPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanTujuanOpd.IndikatorPenetapanData ind,
            Map<String, Map<String, TujuanOpd>> indikatorLookup
    ) {
        Map<String, TujuanOpd> targetMap = indikatorLookup != null
                ? indikatorLookup.get(ind.kodeIndikator())
                : null;

        List<TujuanOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .map(t -> mergeTarget(t, targetMap))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new TujuanOpdPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private TujuanOpdPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanTujuanOpd.TargetPenetapanData t,
            Map<String, TujuanOpd> targetMap
    ) {
        TujuanOpd matched = targetMap != null ? targetMap.get(t.kodeTarget()) : null;
        Double realisasiValue = matched != null && matched.realisasi() != null
                ? matched.realisasi().doubleValue()
                : null;
        String faktorPenunjang = matched != null ? matched.faktorPenunjang() : null;
        String faktorPenghambat = matched != null ? matched.faktorPenghambat() : null;
        String buktiPendukung = matched != null ? matched.buktiPendukung() : null;
        var capaianResult = TujuanOpd.hitungCapaian(realisasiValue, t.target());
        return new TujuanOpdPenetapanResponse.TargetPenetapan(
                t.kodeTarget(), t.satuan(), t.target(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat, buktiPendukung
        );
    }

    private Mono<TujuanOpd> findAndUpdateFaktor(
            String kodeOpd, String kodeTujuanOpd, String kodeIndikator, String kodeTarget,
            String tahun, String bulan,
            UnaryOperator<TujuanOpd> updater
    ) {
        return tujuanOpdRepository
                .findFirstByKodeOpdAndKodeTujuanOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        kodeOpd, kodeTujuanOpd, kodeIndikator, kodeTarget, tahun, bulan)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Data tujuan OPD tidak ditemukan")))
                .flatMap(existing -> tujuanOpdRepository.save(updater.apply(existing)));
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }
}
