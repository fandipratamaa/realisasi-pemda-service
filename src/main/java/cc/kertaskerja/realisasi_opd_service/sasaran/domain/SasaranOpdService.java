package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.FaktorPenghambatSasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.FaktorPenunjangSasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.PenetapanSasaranOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
public class SasaranOpdService {
    private static final Logger log = LoggerFactory.getLogger(SasaranOpdService.class);
    private final SasaranOpdRepository sasaranOpdRepository;
    private final IndikatorSasaranOpdRepository indikatorSasaranOpdRepository;
    private final TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;
    private final PenetapanSasaranOpdClient penetapanClient;

    public SasaranOpdService(
            SasaranOpdRepository sasaranOpdRepository,
            IndikatorSasaranOpdRepository indikatorSasaranOpdRepository,
            TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository,
            PenetapanSasaranOpdClient penetapanClient
    ) {
        this.sasaranOpdRepository = sasaranOpdRepository;
        this.indikatorSasaranOpdRepository = indikatorSasaranOpdRepository;
        this.targetIndikatorSasaranOpdRepository = targetIndikatorSasaranOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    public Flux<SasaranOpdResponse> getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return sasaranOpdRepository
                .findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
                .flatMap(this::toResponseFromStoredData)
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

    public Mono<SasaranOpd> updateFaktorPenunjang(FaktorPenunjangSasaranOpdRequest req) {
        return findAndUpdateFaktor(req.kodeOpd(), req.kodeSasaranOpd(), req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenunjang(req.faktorPenunjang()));
    }

    public Mono<SasaranOpd> updateFaktorPenghambat(FaktorPenghambatSasaranOpdRequest req) {
        return findAndUpdateFaktor(req.kodeOpd(), req.kodeSasaranOpd(), req.tahun(), req.bulan(),
                existing -> existing.withFaktorPenghambat(req.faktorPenghambat()));
    }

    // ========================================================================
    // Private - Entity to Response
    // ========================================================================

    private Flux<SasaranOpdResponse> toResponseFromStoredData(SasaranOpd sasaran) {
        return indikatorSasaranOpdRepository.findAll()
                .filter(i -> Objects.equals(i.sasaranOpdId(), sasaran.id()))
                .flatMap(indikator -> targetIndikatorSasaranOpdRepository.findAll()
                        .filter(t -> Objects.equals(t.indikatorSasaranId(), indikator.id()))
                        .map(this::toTargetResponseFromEntity)
                        .collectList()
                        .map(targets -> toIndikatorResponseFromEntity(indikator, targets)))
                .collectList()
                .map(indikators -> toSasaranOpdResponseFromEntity(sasaran, indikators))
                .flux();
    }

    private SasaranOpdResponse.TargetResponse toTargetResponseFromEntity(
            cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd t) {
        return new SasaranOpdResponse.TargetResponse(
                t.id(), t.kodeTarget(),
                null, null,
                parseInteger(t.tahun()), parseInteger(t.bulan()),
                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                null, null);
    }

    private SasaranOpdResponse.IndikatorResponse toIndikatorResponseFromEntity(
            cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpd ind,
            List<SasaranOpdResponse.TargetResponse> targets) {
        return new SasaranOpdResponse.IndikatorResponse(
                ind.id(), ind.kodeIndikator(),
                null, null, null, null,
                parseInteger(ind.tahun()), parseInteger(ind.bulan()), targets);
    }

    private SasaranOpdResponse toSasaranOpdResponseFromEntity(
            SasaranOpd sasaran, List<SasaranOpdResponse.IndikatorResponse> indikators) {
        return new SasaranOpdResponse(
                sasaran.id(), sasaran.kodeOpd(), sasaran.kodeSasaranOpd(),
                null,
                parseInteger(sasaran.tahun()), parseInteger(sasaran.bulan()),
                sasaran.faktorPenunjang(), sasaran.faktorPenghambat(),
                indikators);
    }

    // ========================================================================
    // Private - Apply penetapan data ke realisasi response
    // ========================================================================

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
        Map<String, PenetapanSasaranOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikators().stream()
                .collect(Collectors.toMap(PenetapanSasaranOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<SasaranOpdResponse.IndikatorResponse> appliedIndikator = response.indikators().stream()
                .map(ind -> applyPenetapanToIndikator(ind, indikatorPenetapanMap))
                .toList();

        return new SasaranOpdResponse(
                response.id(), response.kodeOpd(), response.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                response.tahun(), response.bulan(),
                response.faktorPenunjang(), response.faktorPenghambat(),
                appliedIndikator
        );
    }

    private SasaranOpdResponse.IndikatorResponse applyPenetapanToIndikator(
            SasaranOpdResponse.IndikatorResponse ind,
            Map<String, PenetapanSasaranOpd.IndikatorPenetapanData> indikatorPenetapanMap
    ) {
        PenetapanSasaranOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
        if (matchedInd == null) {
            return ind;
        }

        Map<String, PenetapanSasaranOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.targets().stream()
                .collect(Collectors.toMap(PenetapanSasaranOpd.TargetPenetapanData::kodeTarget, t -> t));

        List<SasaranOpdResponse.TargetResponse> appliedTargets = ind.targets().stream()
                .map(t -> applyPenetapanToTarget(t, targetPenetapanMap))
                .toList();

        return new SasaranOpdResponse.IndikatorResponse(
                ind.id(), ind.kodeIndikator(),
                matchedInd.indikator(),
                matchedInd.rumusPerhitungan(),
                matchedInd.sumberData(),
                matchedInd.definisiOperasional(),
                ind.tahun(), ind.bulan(),
                appliedTargets
        );
    }

    private SasaranOpdResponse.TargetResponse applyPenetapanToTarget(
            SasaranOpdResponse.TargetResponse t,
            Map<String, PenetapanSasaranOpd.TargetPenetapanData> targetPenetapanMap
    ) {
        PenetapanSasaranOpd.TargetPenetapanData matchedTarget = targetPenetapanMap.get(t.kodeTarget());
        if (matchedTarget == null) {
            return t;
        }
        var capaianResult = SasaranOpd.hitungCapaian(t.realisasi(), matchedTarget.target());
        return new SasaranOpdResponse.TargetResponse(
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
        Mono<Map<String, SasaranOpdResponse>> bulanMap = getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(
                        tahunStr, kodeOpd, bulan)
                .collectMap(SasaranOpdResponse::kodeSasaranOpd);
        return bulanMap
                .map(rmap -> buildResponseWithRealisasi(
                        penetapanList, root, rmap, bulan));
    }

    private PenetapanSasaranOpdListResponse buildResponseWithoutBulan(
            List<PenetapanSasaranOpd.SasaranPenetapanData> penetapanList,
            PenetapanInfo root
    ) {
        List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, null, Set.of()))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanSasaranOpdListResponse(root.kodeOpd(), root.tahun(), null, items);
    }

    private PenetapanSasaranOpdListResponse buildResponseWithRealisasi(
            List<PenetapanSasaranOpd.SasaranPenetapanData> penetapanList,
            PenetapanInfo root,
            Map<String, SasaranOpdResponse> rMap,
            String bulan
    ) {
        List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeSasaranOpd()), Set.of()))
                .filter(response -> !response.indikators().isEmpty())
                .toList();
        return new PenetapanSasaranOpdListResponse(root.kodeOpd(), root.tahun(), parseInteger(bulan), items);
    }

    private SasaranOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranOpd.SasaranPenetapanData penetapan,
            SasaranOpdResponse realisasi,
            Set<String> hiddenTargetKeys
    ) {
        Map<String, SasaranOpdResponse.IndikatorResponse> indikatorMap = buildIndikatorMap(realisasi);
        String faktorPenunjang = realisasi != null ? realisasi.faktorPenunjang() : null;
        String faktorPenghambat = realisasi != null ? realisasi.faktorPenghambat() : null;

        List<SasaranOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> mapIndikatorToPenetapan(ind, indikatorMap,
                        penetapan.kodeSasaranOpd(), hiddenTargetKeys,
                        faktorPenunjang, faktorPenghambat))
                .filter(Objects::nonNull)
                .toList();

        return new SasaranOpdPenetapanResponse(
                penetapan.id(), penetapan.kodeSasaranOpd(), penetapan.sasaranOpd(), indikatorList
        );
    }

    private SasaranOpdPenetapanResponse.IndikatorPenetapan mapIndikatorToPenetapan(
            PenetapanSasaranOpd.IndikatorPenetapanData ind,
            Map<String, SasaranOpdResponse.IndikatorResponse> indikatorMap,
            String kodeSasaranOpd,
            Set<String> hiddenTargetKeys,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        Map<String, SasaranOpdResponse.TargetResponse> targetMap = buildTargetMap(indikatorMap.get(ind.kodeIndikator()));

        List<SasaranOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                .filter(t -> !hiddenTargetKeys.contains(buildTargetKey(kodeSasaranOpd, ind.kodeIndikator(), t.kodeTarget())))
                .map(t -> mergeTarget(t, targetMap, faktorPenunjang, faktorPenghambat))
                .toList();

        if (targetList.isEmpty()) {
            return null;
        }

        return new SasaranOpdPenetapanResponse.IndikatorPenetapan(
                ind.kodeIndikator(), ind.indikator(), ind.rumusPerhitungan(),
                ind.sumberData(), ind.definisiOperasional(), targetList
        );
    }

    private Map<String, SasaranOpdResponse.IndikatorResponse> buildIndikatorMap(SasaranOpdResponse realisasi) {
        if (realisasi == null) {
            return Map.of();
        }
        return realisasi.indikators().stream()
                .collect(Collectors.toMap(SasaranOpdResponse.IndikatorResponse::kodeIndikator, i -> i));
    }

    private Map<String, SasaranOpdResponse.TargetResponse> buildTargetMap(SasaranOpdResponse.IndikatorResponse indikator) {
        if (indikator == null) {
            return Map.of();
        }
        return indikator.targets().stream()
                .collect(Collectors.toMap(SasaranOpdResponse.TargetResponse::kodeTarget, t -> t));
    }

    private SasaranOpdPenetapanResponse.TargetPenetapan mergeTarget(
            PenetapanSasaranOpd.TargetPenetapanData t,
            Map<String, SasaranOpdResponse.TargetResponse> targetMap,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        SasaranOpdResponse.TargetResponse matchedTarget = targetMap.get(t.kodeTarget());
        Double targetPenetapan = t.target();
        Double realisasiValue = matchedTarget != null ? matchedTarget.realisasi() : null;
        var capaianResult = SasaranOpd.hitungCapaian(realisasiValue, targetPenetapan);
        return new SasaranOpdPenetapanResponse.TargetPenetapan(
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

    private String buildTargetKey(String kodeSasaranOpd, String kodeIndikator, String kodeTarget) {
        return kodeSasaranOpd + "|" + kodeIndikator + "|" + kodeTarget;
    }

    // ========================================================================
    // Private - Other helpers
    // ========================================================================

    private Mono<SasaranOpd> findAndUpdateFaktor(
            String kodeOpd, String kodeSasaranOpd, String tahun, String bulan,
            UnaryOperator<SasaranOpd> updater
    ) {
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(kodeOpd, kodeSasaranOpd, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran OPD tidak ditemukan")))
                .flatMap(existing -> sasaranOpdRepository.save(updater.apply(existing)));
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
