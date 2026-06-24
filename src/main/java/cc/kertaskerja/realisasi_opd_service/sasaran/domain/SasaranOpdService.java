package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.FaktorPenghambatSasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.FaktorPenunjangSasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.PenetapanSasaranOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdResponse;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.LaporanRealisasiSasaranOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
public class SasaranOpdService {
    private static final Logger log = LoggerFactory.getLogger(SasaranOpdService.class);
    private final SasaranOpdRepository sasaranOpdRepository;
    private final PenetapanSasaranOpdClient penetapanClient;

    public SasaranOpdService(
            SasaranOpdRepository sasaranOpdRepository,
            PenetapanSasaranOpdClient penetapanClient
    ) {
        this.sasaranOpdRepository = sasaranOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    public Flux<SasaranOpdResponse> getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return sasaranOpdRepository
                .findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), kodeOpd, tahun));
    }

    public Mono<PenetapanSasaranOpdListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchSasaranOpd(kodeOpd, tahun)
                .flatMap(penetapanList -> {
                    var root = resolveRootInfo(penetapanList, kodeOpd, tahun);
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(buildResponseWithoutBulan(penetapanList, root));
                    }
                    return buildResponseWithBulan(penetapanList, root, kodeOpd, bulan);
                });
    }

    public Mono<SasaranOpdResponse> submitRealisasiSasaranOpd(SasaranOpdRequest req) {
        return upsert(req)
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<SasaranOpdResponse> batchSubmitRealisasiSasaranOpd(List<SasaranOpdRequest> sasaranOpdRequests) {
        return Flux.fromIterable(sasaranOpdRequests)
                .flatMap(this::submitRealisasiSasaranOpd);
    }

    public Mono<SasaranOpd> updateFaktorPenunjang(FaktorPenunjangSasaranOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.kodeSasaranOpd(), req.kodeIndikator(), req.kodeTarget(),
                req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenunjang(req.faktorPenunjang()));
    }

    public Mono<SasaranOpd> updateFaktorPenghambat(FaktorPenghambatSasaranOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.kodeSasaranOpd(), req.kodeIndikator(), req.kodeTarget(),
                req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenghambat(req.faktorPenghambat()));
    }

    public Mono<LaporanRealisasiSasaranOpdResponse> getLaporanRealisasi(String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return sasaranOpdRepository.findAllByKodeOpdAndTahun(kodeOpd, tahun)
                .collectList()
                .map(list -> {
                    Map<String, Double> listData = switch (jenisLaporan) {
                        case BULANAN -> {
                            if (bulan == null || bulan.isBlank()) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                            }
                            double total = list.stream()
                                    .filter(t -> bulan.equals(t.bulan()))
                                    .filter(t -> t.realisasi() != null)
                                    .mapToDouble(t -> t.realisasi().doubleValue())
                                    .sum();
                            yield Map.of(bulan, total);
                        }
                        case TRIWULAN -> {
                            Map<String, Double> triwulanMap = new HashMap<>();
                            for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                            for (SasaranOpd t : list) {
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
                            for (SasaranOpd t : list) {
                                if (t.realisasi() == null) continue;
                                bulanMap.merge(t.bulan(), t.realisasi().doubleValue(), Double::sum);
                            }
                            yield bulanMap;
                        }
                    };
                    return new LaporanRealisasiSasaranOpdResponse(tahun, kodeOpd, jenisLaporan, listData);
                });
    }

    private Mono<SasaranOpd> upsert(SasaranOpdRequest req) {
        JenisRealisasi jenisRealisasi = req.jenisRealisasi() != null
                ? req.jenisRealisasi()
                : JenisRealisasi.NAIK;
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSasaranOpd(), req.kodeIndikator(),
                        req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> sasaranOpdRepository.save(
                        new SasaranOpd(
                                existing.id(), req.kodeOpd(), req.tahun(), req.bulan(),
                                req.kodeSasaranOpd(), req.kodeIndikator(), req.kodeTarget(),
                                BigDecimal.valueOf(req.realisasi()),
                                jenisRealisasi,
                                existing.faktorPenunjang(), existing.faktorPenghambat(),
                                existing.createdBy(), existing.createdDate(), null, null)))
                .switchIfEmpty(Mono.defer(() -> sasaranOpdRepository.save(
                        SasaranOpd.of(
                                req.kodeOpd(), req.tahun(), req.bulan(),
                                req.kodeSasaranOpd(), req.kodeIndikator(), req.kodeTarget(),
                                BigDecimal.valueOf(req.realisasi()),
                                jenisRealisasi))));
    }

    private SasaranOpdResponse toResponse(SasaranOpd entity) {
        return new SasaranOpdResponse(
                entity.id(), entity.kodeOpd(),
                parseInteger(entity.tahun()), parseInteger(entity.bulan()),
                entity.kodeSasaranOpd(), entity.kodeIndikator(), entity.kodeTarget(),
                entity.realisasi() != null ? entity.realisasi().doubleValue() : null,
                entity.faktorPenunjang(), entity.faktorPenghambat(),
                null, null, null, null, null, null, null, null, null,
                entity.jenisRealisasi(),
                entity.createdBy(), entity.lastModifiedBy());
    }

    private Mono<SasaranOpdResponse> applyPenetapan(Mono<SasaranOpdResponse> responseMono, String kodeOpd, String tahun) {
        Mono<List<PenetapanSasaranOpd.SasaranPenetapanData>> penetapanData =
                penetapanClient.fetchSasaranOpd(kodeOpd, Integer.parseInt(tahun));

        return responseMono.zipWith(penetapanData.defaultIfEmpty(List.of()), (response, penetapanList) -> {
            PenetapanSasaranOpd.SasaranPenetapanData matching = penetapanList.stream()
                    .filter(p -> p.kodeSasaranOpd().equals(response.kodeSasaranOpd()))
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

    private SasaranOpdResponse applyPenetapanToResponse(
            SasaranOpdResponse response,
            PenetapanSasaranOpd.SasaranPenetapanData penetapan
    ) {
        var matchedIndikator = penetapan.indikators().stream()
                .filter(i -> i.kodeIndikator().equals(response.kodeIndikator()))
                .findFirst();

        if (matchedIndikator.isEmpty()) {
            return new SasaranOpdResponse(
                    response.id(), response.kodeOpd(), response.tahun(), response.bulan(),
                    response.kodeSasaranOpd(), response.kodeIndikator(), response.kodeTarget(),
                    response.realisasi(), response.faktorPenunjang(), response.faktorPenghambat(),
                    penetapan.sasaranOpd(), null, null, null, null, null, null, null, null,
                    response.jenisRealisasi(),
                    response.createdBy(), response.lastModifiedBy());
        }

        var matchedTarget = matchedIndikator.get().targets().stream()
                .filter(t -> t.kodeTarget().equals(response.kodeTarget()))
                .findFirst();

        Double target = matchedTarget.map(PenetapanSasaranOpd.TargetPenetapanData::target).orElse(null);
        String satuan = matchedTarget.map(PenetapanSasaranOpd.TargetPenetapanData::satuan).orElse(null);
        var capaianResult = SasaranOpd.hitungCapaian(response.realisasi(), target);

        return new SasaranOpdResponse(
                response.id(), response.kodeOpd(), response.tahun(), response.bulan(),
                response.kodeSasaranOpd(), response.kodeIndikator(), response.kodeTarget(),
                response.realisasi(), response.faktorPenunjang(), response.faktorPenghambat(),
                penetapan.sasaranOpd(),
                matchedIndikator.get().indikator(),
                matchedIndikator.get().rumusPerhitungan(),
                matchedIndikator.get().sumberData(),
                matchedIndikator.get().definisiOperasional(),
                target, satuan,
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                response.jenisRealisasi(),
                response.createdBy(), response.lastModifiedBy()
        );
    }

    private Mono<Map<String, Map<String, Map<String, SasaranOpd>>>> buildRealisasiLookup(
            String kodeOpd, String tahun, String bulan) {
        return sasaranOpdRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .collectList()
                .map(records -> {
                    Map<String, Map<String, Map<String, SasaranOpd>>> lookup = new HashMap<>();
                    for (SasaranOpd r : records) {
                        lookup.computeIfAbsent(r.kodeSasaranOpd(), k -> new HashMap<>())
                                .computeIfAbsent(r.kodeIndikator(), k -> new HashMap<>())
                                .put(r.kodeTarget(), r);
                    }
                    return lookup;
                });
    }

    private record PenetapanInfo(String kodeOpd, Integer tahun) {}

    private PenetapanInfo resolveRootInfo(List<PenetapanSasaranOpd.SasaranPenetapanData> list, String kodeOpd, int tahun) {
        if (list.isEmpty()) {
            return new PenetapanInfo(kodeOpd, tahun);
        }
        var first = list.getFirst();
        return new PenetapanInfo(first.kodeOpd(), first.tahunAktif());
    }

    private Mono<PenetapanSasaranOpdListResponse> buildResponseWithBulan(
            List<PenetapanSasaranOpd.SasaranPenetapanData> penetapanList,
            PenetapanInfo root,
            String kodeOpd,
            String bulan
    ) {
        String tahunStr = String.valueOf(root.tahun());
        return buildRealisasiLookup(kodeOpd, tahunStr, bulan)
                .map(lookup -> {
                    List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                            .map(p -> mergePenetapanWithRealisasi(p, lookup.get(p.kodeSasaranOpd())))
                            .filter(response -> !response.indikators().isEmpty())
                            .toList();
                    return new PenetapanSasaranOpdListResponse(
                            root.kodeOpd(), root.tahun(), parseInteger(bulan), items);
                });
    }

    private PenetapanSasaranOpdListResponse buildResponseWithoutBulan(
            List<PenetapanSasaranOpd.SasaranPenetapanData> penetapanList,
            PenetapanInfo root
    ) {
        List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanSasaranOpdListResponse(root.kodeOpd(), root.tahun(), null, items);
    }

    private SasaranOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranOpd.SasaranPenetapanData penetapan,
            Map<String, Map<String, SasaranOpd>> indikatorLookup
    ) {
        List<SasaranOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorLookup))
                .filter(Objects::nonNull)
                .toList();

        return new SasaranOpdPenetapanResponse(
                penetapan.id(), penetapan.kodeSasaranOpd(), penetapan.sasaranOpd(), indikatorList
        );
    }

    private SasaranOpdPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanSasaranOpd.IndikatorPenetapanData ind,
            Map<String, Map<String, SasaranOpd>> indikatorLookup
    ) {
        Map<String, SasaranOpd> targetMap = indikatorLookup != null
                ? indikatorLookup.get(ind.kodeIndikator())
                : null;

        List<SasaranOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .map(t -> mergeTarget(t, targetMap))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new SasaranOpdPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private SasaranOpdPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanSasaranOpd.TargetPenetapanData t,
            Map<String, SasaranOpd> targetMap
    ) {
        SasaranOpd matched = targetMap != null ? targetMap.get(t.kodeTarget()) : null;
        Double realisasiValue = matched != null && matched.realisasi() != null
                ? matched.realisasi().doubleValue()
                : null;
        String faktorPenunjang = matched != null ? matched.faktorPenunjang() : null;
        String faktorPenghambat = matched != null ? matched.faktorPenghambat() : null;
        var capaianResult = SasaranOpd.hitungCapaian(realisasiValue, t.target());
        return new SasaranOpdPenetapanResponse.TargetPenetapan(
                t.kodeTarget(), t.satuan(), t.target(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat
        );
    }

    private Mono<SasaranOpd> findAndUpdateFaktor(
            String kodeOpd, String kodeSasaranOpd, String kodeIndikator, String kodeTarget,
            String tahun, String bulan,
            UnaryOperator<SasaranOpd> updater
    ) {
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        kodeOpd, kodeSasaranOpd, kodeIndikator, kodeTarget, tahun, bulan)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Data sasaran OPD tidak ditemukan")))
                .flatMap(existing -> sasaranOpdRepository.save(updater.apply(existing)));
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
