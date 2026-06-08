package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.integration.penetapan.PenetapanTujuanOpdClient;
import cc.kertaskerja.integration.penetapan.tujuan_opd.PenetapanTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.FaktorPenghambatTujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.FaktorPenunjangTujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.PenetapanTujuanOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
public class TujuanOpdService {
    private static final Logger log = LoggerFactory.getLogger(TujuanOpdService.class);
    private final TujuanOpdRepository tujuanOpdRepository;
    private final IndikatorTujuanOpdRepository indikatorTujuanOpdRepository;
    private final TargetIndikatorTujuanOpdRepository targetIndikatorTujuanOpdRepository;
    private final PenetapanTujuanOpdClient penetapanClient;

    public TujuanOpdService(
            TujuanOpdRepository tujuanOpdRepository,
            IndikatorTujuanOpdRepository indikatorTujuanOpdRepository,
            TargetIndikatorTujuanOpdRepository targetIndikatorTujuanOpdRepository,
            PenetapanTujuanOpdClient penetapanClient
    ) {
        this.tujuanOpdRepository = tujuanOpdRepository;
        this.indikatorTujuanOpdRepository = indikatorTujuanOpdRepository;
        this.targetIndikatorTujuanOpdRepository = targetIndikatorTujuanOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    // ========================================================================
    // Public API - Query
    // ========================================================================

    public Flux<TujuanOpdResponse> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return tujuanOpdRepository
                .findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
                .flatMap(this::toResponseFromStoredData)
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

    // ========================================================================
    // Public API - Write
    // ========================================================================

    public Mono<TujuanOpdResponse> submitRealisasiTujuanOpd(TujuanOpdRequest req) {
        return upsertHierarchy(req)
                .flatMap(tujuan -> toResponseFromStoredData(tujuan).next())
                .flatMap(response -> applyPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<TujuanOpdResponse> batchSubmitRealisasiTujuanOpd(List<TujuanOpdRequest> tujuanOpdRequests) {
        return Flux.fromIterable(tujuanOpdRequests)
                .flatMap(this::submitRealisasiTujuanOpd);
    }

    // ========================================================================
    // Public API - Update
    // ========================================================================

    public Mono<TujuanOpd> updateFaktorPenunjang(FaktorPenunjangTujuanOpdRequest req) {
        return findAndUpdateFaktor(req.kodeOpd(), req.kodeTujuanOpd(), req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenunjang(req.faktorPenunjang()));
    }

    public Mono<TujuanOpd> updateFaktorPenghambat(FaktorPenghambatTujuanOpdRequest req) {
        return findAndUpdateFaktor(req.kodeOpd(), req.kodeTujuanOpd(), req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenghambat(req.faktorPenghambat()));
    }

    // ========================================================================
    // Private - Upsert chain
    // ========================================================================

    private Mono<TujuanOpd> upsertHierarchy(TujuanOpdRequest req) {
        return upsertTujuan(req)
                .flatMap(tujuan -> upsertIndikator(tujuan.id(), req)
                        .flatMap(indikator -> upsertTarget(indikator.id(), req)
                                .thenReturn(tujuan)));
    }

    private Mono<TujuanOpd> upsertTujuan(TujuanOpdRequest req) {
        return tujuanOpdRepository
                .findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                        req.kodeOpd(), req.kodeTujuanOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> tujuanOpdRepository.save(
                        TujuanOpd.of(req.kodeOpd(), req.kodeTujuanOpd(), req.tahun(), req.bulan()))));
    }

    private Mono<IndikatorTujuanOpd> upsertIndikator(Long tujuanId, TujuanOpdRequest req) {
        return indikatorTujuanOpdRepository
                .findFirstByTujuanOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                        tujuanId, req.kodeIndikatorTujuanOpd(), req.kodeOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> indikatorTujuanOpdRepository.save(
                        new IndikatorTujuanOpd(
                                null, tujuanId, req.kodeIndikatorTujuanOpd(),
                                req.kodeOpd(), req.tahun(), req.bulan(),
                                null, null, null, null))));
    }

    private Mono<TargetIndikatorTujuanOpd> upsertTarget(Long indikatorId, TujuanOpdRequest req) {
        return targetIndikatorTujuanOpdRepository
                .findFirstByIndikatorTujuanIdAndKodeTargetAndTahunAndBulan(
                        indikatorId, req.kodeTargetTujuanOpd(), req.tahun(), req.bulan())
                .flatMap(existing -> targetIndikatorTujuanOpdRepository.save(
                        new TargetIndikatorTujuanOpd(
                                existing.id(), existing.indikatorTujuanId(),
                                existing.kodeTarget(),
                                BigDecimal.valueOf(req.realisasi()),
                                existing.tahun(), existing.bulan(),
                                existing.createdDate(), null,
                                existing.createdBy(), null)))
                .switchIfEmpty(Mono.defer(() -> targetIndikatorTujuanOpdRepository.save(
                        new TargetIndikatorTujuanOpd(
                                null, indikatorId, req.kodeTargetTujuanOpd(),
                                BigDecimal.valueOf(req.realisasi()),
                                req.tahun(), req.bulan(),
                                null, null, null, null))));
    }

    // ========================================================================
    // Private - Entity to Response
    // ========================================================================

    private Flux<TujuanOpdResponse> toResponseFromStoredData(TujuanOpd tujuan) {
        return indikatorTujuanOpdRepository.findAllByTujuanOpdId(tujuan.id())
                .flatMap(indikator -> targetIndikatorTujuanOpdRepository.findAllByIndikatorTujuanId(indikator.id())
                        .map(this::toTargetResponseFromEntity)
                        .collectList()
                        .map(targets -> toIndikatorResponseFromEntity(indikator, targets)))
                .collectList()
                .map(indikators -> toTujuanOpdResponseFromEntity(tujuan, indikators))
                .flux();
    }

    private TujuanOpdResponse.TargetResponse toTargetResponseFromEntity(TargetIndikatorTujuanOpd t) {
        return new TujuanOpdResponse.TargetResponse(
                t.id(), t.kodeTarget(),
                null, null,
                parseInteger(t.tahun()), parseInteger(t.bulan()),
                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                null, null);
    }

    private TujuanOpdResponse.IndikatorResponse toIndikatorResponseFromEntity(
            IndikatorTujuanOpd ind, List<TujuanOpdResponse.TargetResponse> targets) {
        return new TujuanOpdResponse.IndikatorResponse(
                ind.id(), ind.kodeIndikator(),
                null, null, null, null,
                parseInteger(ind.tahun()), parseInteger(ind.bulan()), targets);
    }

    private TujuanOpdResponse toTujuanOpdResponseFromEntity(
            TujuanOpd tujuan, List<TujuanOpdResponse.IndikatorResponse> indikators) {
        return new TujuanOpdResponse(
                tujuan.id(), tujuan.kodeOpd(), tujuan.kodeTujuanOpd(),
                null,
                parseInteger(tujuan.tahun()), parseInteger(tujuan.bulan()),
                tujuan.faktorPenunjang(), tujuan.faktorPenghambat(),
                indikators);
    }

    // ========================================================================
    // Private - Apply penetapan data ke realisasi response
    // ========================================================================

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
        Map<String, PenetapanTujuanOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikators().stream()
                .collect(Collectors.toMap(PenetapanTujuanOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<TujuanOpdResponse.IndikatorResponse> appliedIndikator = response.indikators().stream()
                .map(ind -> applyPenetapanToIndikator(ind, indikatorPenetapanMap))
                .toList();

        return new TujuanOpdResponse(
                response.id(), response.kodeOpd(), response.kodeTujuanOpd(),
                penetapan.tujuanOpd(),
                response.tahun(), response.bulan(),
                response.faktorPenunjang(), response.faktorPenghambat(),
                appliedIndikator
        );
    }

    private TujuanOpdResponse.IndikatorResponse applyPenetapanToIndikator(
            TujuanOpdResponse.IndikatorResponse ind,
            Map<String, PenetapanTujuanOpd.IndikatorPenetapanData> indikatorPenetapanMap
    ) {
        PenetapanTujuanOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
        if (matchedInd == null) {
            return ind;
        }

        Map<String, PenetapanTujuanOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.targets().stream()
                .collect(Collectors.toMap(PenetapanTujuanOpd.TargetPenetapanData::kodeTarget, t -> t));

        List<TujuanOpdResponse.TargetResponse> appliedTargets = ind.targets().stream()
                .map(t -> applyPenetapanToTarget(t, targetPenetapanMap))
                .toList();

        return new TujuanOpdResponse.IndikatorResponse(
                ind.id(), ind.kodeIndikator(),
                matchedInd.indikator(),
                matchedInd.rumusPerhitungan(),
                matchedInd.sumberData(),
                matchedInd.definisiOperasional(),
                ind.tahun(), ind.bulan(),
                appliedTargets
        );
    }

    private TujuanOpdResponse.TargetResponse applyPenetapanToTarget(
            TujuanOpdResponse.TargetResponse t,
            Map<String, PenetapanTujuanOpd.TargetPenetapanData> targetPenetapanMap
    ) {
        PenetapanTujuanOpd.TargetPenetapanData matchedTarget = targetPenetapanMap.get(t.kodeTarget());
        if (matchedTarget == null) {
            return t;
        }
        var capaianResult = TujuanOpd.hitungCapaian(t.realisasi(), matchedTarget.target());
        return new TujuanOpdResponse.TargetResponse(
                t.id(), t.kodeTarget(),
                matchedTarget.target(),
                matchedTarget.satuan(),
                t.tahun(), t.bulan(), t.realisasi(),
                capaianResult.capaian(),
                capaianResult.keteranganCapaian()
        );
    }

    // ========================================================================
    // Private - Merge penetapan data dengan realisasi (untuk endpoint getPenetapanWithRealisasi)
    // ========================================================================

    private record PenetapanInfo(String kodeOpd, Integer tahun) {}

    private PenetapanInfo resolveRootInfo(List<PenetapanTujuanOpd.TujuanPenetapanData> list, String kodeOpd, int tahun) {
        if (list.isEmpty()) {
            return new PenetapanInfo(kodeOpd, tahun);
        }
        var first = list.getFirst();
        return new PenetapanInfo(first.kodeOpd(), first.tahunAktif());
    }

    private Mono<PenetapanTujuanOpdListResponse> buildResponseWithBulan(
            List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList,
            PenetapanInfo root,
            String kodeOpd,
            String bulan
    ) {
        String tahunStr = String.valueOf(root.tahun());
        Mono<Map<String, TujuanOpdResponse>> bulanMap = getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(
                        tahunStr, kodeOpd, bulan)
                .collectMap(TujuanOpdResponse::kodeTujuanOpd);
        return bulanMap
                .map(rmap -> buildResponseWithRealisasi(
                        penetapanList, root, rmap, bulan));
    }

    private PenetapanTujuanOpdListResponse buildResponseWithoutBulan(
            List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList,
            PenetapanInfo root
    ) {
        List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null, Set.of()))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanTujuanOpdListResponse(root.kodeOpd(), root.tahun(), null, items);
    }

    private PenetapanTujuanOpdListResponse buildResponseWithRealisasi(
            List<PenetapanTujuanOpd.TujuanPenetapanData> penetapanList,
            PenetapanInfo root,
            Map<String, TujuanOpdResponse> rMap,
            String bulan
    ) {
        List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeTujuanOpd()), Set.of()))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanTujuanOpdListResponse(root.kodeOpd(), root.tahun(), parseInteger(bulan), items);
    }

    private TujuanOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanTujuanOpd.TujuanPenetapanData penetapan,
            TujuanOpdResponse realisasi,
            Set<String> hiddenTargetKeys
    ) {
        Map<String, TujuanOpdResponse.IndikatorResponse> indikatorMap = buildIndikatorMap(realisasi);
        String faktorPenunjang = realisasi != null ? realisasi.faktorPenunjang() : null;
        String faktorPenghambat = realisasi != null ? realisasi.faktorPenghambat() : null;

        List<TujuanOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorMap,
                        penetapan.kodeTujuanOpd(), hiddenTargetKeys,
                        faktorPenunjang, faktorPenghambat))
                .filter(Objects::nonNull)
                .toList();

        return new TujuanOpdPenetapanResponse(
                penetapan.id(), penetapan.kodeTujuanOpd(), penetapan.tujuanOpd(), indikatorList
        );
    }

    private TujuanOpdPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanTujuanOpd.IndikatorPenetapanData ind,
            Map<String, TujuanOpdResponse.IndikatorResponse> indikatorMap,
            String kodeTujuanOpd,
            Set<String> hiddenTargetKeys,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        Map<String, TujuanOpdResponse.TargetResponse> targetMap = buildTargetMap(indikatorMap.get(ind.kodeIndikator()));

        List<TujuanOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .filter(t -> !hiddenTargetKeys.contains(buildTargetKey(kodeTujuanOpd, ind.kodeIndikator(), t.kodeTarget())))
                .map(t -> mergeTarget(t, targetMap, faktorPenunjang, faktorPenghambat))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new TujuanOpdPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private Map<String, TujuanOpdResponse.IndikatorResponse> buildIndikatorMap(TujuanOpdResponse realisasi) {
        if (realisasi == null) {
            return Map.of();
        }
        return realisasi.indikators().stream()
                .collect(Collectors.toMap(TujuanOpdResponse.IndikatorResponse::kodeIndikator, i -> i));
    }

    private Map<String, TujuanOpdResponse.TargetResponse> buildTargetMap(TujuanOpdResponse.IndikatorResponse indikator) {
        if (indikator == null) {
            return Map.of();
        }
        return indikator.targets().stream()
                .collect(Collectors.toMap(TujuanOpdResponse.TargetResponse::kodeTarget, t -> t));
    }

    private TujuanOpdPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanTujuanOpd.TargetPenetapanData t,
            Map<String, TujuanOpdResponse.TargetResponse> targetMap,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        TujuanOpdResponse.TargetResponse matchedTarget = targetMap.get(t.kodeTarget());
        Double targetPenetapan = t.target();
        Double realisasiValue = matchedTarget != null ? matchedTarget.realisasi() : null;
        var capaianResult = TujuanOpd.hitungCapaian(realisasiValue, targetPenetapan);
        return new TujuanOpdPenetapanResponse.TargetPenetapan(
                t.kodeTarget(),
                t.satuan(),
                targetPenetapan,
                realisasiValue,
                capaianResult.capaian(),
                capaianResult.keteranganCapaian(),
                faktorPenunjang,
                faktorPenghambat
        );
    }

    private String buildTargetKey(String kodeTujuanOpd, String kodeIndikator, String kodeTarget) {
        return kodeTujuanOpd + "|" + kodeIndikator + "|" + kodeTarget;
    }

    // ========================================================================
    // Private - Method untuk cek ada tidak tujuan opd saat insert realisasi
    // ========================================================================

    private Mono<TujuanOpd> findAndUpdateFaktor(
            String kodeOpd, String kodeTujuanOpd, String tahun, String bulan,
            UnaryOperator<TujuanOpd> updater
    ) {
        return tujuanOpdRepository
                .findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(kodeOpd, kodeTujuanOpd, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan OPD tidak ditemukan")))
                .flatMap(existing -> tujuanOpdRepository.save(updater.apply(existing)));
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
