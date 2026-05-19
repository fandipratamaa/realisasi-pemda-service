package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), kodeOpd, tahun));
    }

    public Mono<SasaranOpdResponse> submitRealisasiSasaranOpd(SasaranOpdRequest req) {
        return upsertHierarchy(req)
                .flatMap(sasaran -> toResponseFromStoredData(sasaran).next())
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<SasaranOpdResponse> batchSubmitRealisasiSasaranOpd(List<SasaranOpdRequest> sasaranOpdRequests) {
        return Flux.fromIterable(sasaranOpdRequests)
                .flatMap(this::submitRealisasiSasaranOpd);
    }

    public Flux<SasaranOpdPenetapanResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchSasaranOpd(kodeOpd, tahun)
                .flatMapMany(penetapanList -> {
                    if (bulan == null || bulan.isBlank()) {
                        return Flux.fromIterable(penetapanList)
                                .map(p -> mergePenetapanWithRealisasi(p, null));
                    }

                    Mono<Map<String, SasaranOpdResponse>> realisasiMap =
                            getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(String.valueOf(tahun), kodeOpd, bulan)
                                    .collectMap(SasaranOpdResponse::kodeSasaranOpd);

                    return realisasiMap.flatMapMany(rMap ->
                            Flux.fromIterable(penetapanList)
                                    .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeSasaranOpd())))
                    );
                });
    }

    private SasaranOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranOpd.SasaranPenetapanData penetapan,
            SasaranOpdResponse realisasi
    ) {
        Map<String, SasaranOpdResponse.IndikatorResponse> indikatorMap = realisasi != null
                ? realisasi.indikator().stream()
                        .collect(Collectors.toMap(SasaranOpdResponse.IndikatorResponse::kodeIndikator, i -> i))
                : Map.of();

        List<SasaranOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikator().stream()
                .map(ind -> {
                    SasaranOpdResponse.IndikatorResponse matchedInd = indikatorMap.get(ind.kodeIndikator());
                    Map<String, SasaranOpdResponse.TargetResponse> targetMap = matchedInd != null
                            ? matchedInd.target().stream()
                                    .collect(Collectors.toMap(SasaranOpdResponse.TargetResponse::kodeTarget, t -> t))
                            : Map.of();

                    List<SasaranOpdPenetapanResponse.TargetPenetapan> targetList = ind.target().stream()
                            .map(t -> {
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
                                        capaianResult.keteranganCapaian()
                                );
                            })
                            .toList();

                    return new SasaranOpdPenetapanResponse.IndikatorPenetapan(
                            ind.kodeIndikator(),
                            ind.indikator(),
                            ind.rumusPerhitungan(),
                            ind.sumberData(),
                            ind.definisiOperasional(),
                            targetList
                    );
                })
                .toList();

        return new SasaranOpdPenetapanResponse(
                penetapan.kodeOpd(),
                penetapan.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                penetapan.periode(),
                penetapan.tahunAktif(),
                penetapan.versi(),
                indikatorList
        );
    }

    private Mono<SasaranOpdResponse> enrichWithPenetapan(Mono<SasaranOpdResponse> responseMono, String kodeOpd, String tahun) {
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
            return enrichResponse(response, matching);
        }).onErrorResume(e -> {
            log.warn("Gagal terhubung dengan response penetapan untuk kodeOpd={}, tahun={}: {}",
                    kodeOpd, tahun, e.getMessage());
            return responseMono;
        });
    }

    private SasaranOpdResponse enrichResponse(
            SasaranOpdResponse response,
            PenetapanSasaranOpd.SasaranPenetapanData penetapan
    ) {
        Map<String, PenetapanSasaranOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikator().stream()
                .collect(Collectors.toMap(PenetapanSasaranOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<SasaranOpdResponse.IndikatorResponse> enrichedIndikator = response.indikator().stream()
                .map(ind -> {
                    PenetapanSasaranOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
                    if (matchedInd == null) {
                        return ind;
                    }
                    Map<String, PenetapanSasaranOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.target().stream()
                            .collect(Collectors.toMap(PenetapanSasaranOpd.TargetPenetapanData::kodeTarget, t -> t));

                    List<SasaranOpdResponse.TargetResponse> enrichedTargets = ind.target().stream()
                            .map(t -> {
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
                            })
                            .toList();

                    return new SasaranOpdResponse.IndikatorResponse(
                            ind.id(), ind.kodeIndikator(),
                            matchedInd.indikator(),
                            matchedInd.rumusPerhitungan(),
                            matchedInd.sumberData(),
                            matchedInd.definisiOperasional(),
                            ind.tahun(), ind.bulan(),
                            enrichedTargets
                    );
                })
                .toList();

        return new SasaranOpdResponse(
                response.id(), response.kodeOpd(), response.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                response.tahun(), response.bulan(),
                enrichedIndikator
        );
    }

    private Mono<SasaranOpd> upsertHierarchy(SasaranOpdRequest req) {
        return sasaranOpdRepository.findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSasaranOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> sasaranOpdRepository.save(SasaranOpd.of(
                        req.kodeOpd(),
                        req.kodeSasaranOpd(),
                        req.tahun(),
                        req.bulan()
                ))))
                .flatMap(sasaran -> indikatorSasaranOpdRepository.findFirstBySasaranOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                                sasaran.id(), req.kodeIndikatorSasaranOpd(), req.kodeOpd(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorSasaranOpdRepository.save(new IndikatorSasaranOpd(
                                null,
                                sasaran.id(),
                                req.kodeIndikatorSasaranOpd(),
                                req.kodeOpd(),
                                req.tahun(),
                                req.bulan(),
                                null,
                                null,
                                null,
                                null
                        ))))
                        .flatMap(indikator -> targetIndikatorSasaranOpdRepository.findFirstByIndikatorSasaranIdAndKodeTargetAndTahunAndBulan(
                                        indikator.id(), req.kodeTargetSasaranOpd(), req.tahun(), req.bulan())
                                .flatMap(existing -> targetIndikatorSasaranOpdRepository.save(new TargetIndikatorSasaranOpd(
                                        existing.id(),
                                        existing.indikatorSasaranId(),
                                        existing.kodeTarget(),
                                                BigDecimal.valueOf(req.realisasi()),
                                                existing.tahun(),
                                                existing.bulan(),
                                                existing.createdDate(),
                                                null,
                                                existing.createdBy(),
                                                null
                                        )))
                                        .switchIfEmpty(Mono.defer(() -> targetIndikatorSasaranOpdRepository.save(new TargetIndikatorSasaranOpd(
                                                null,
                                                indikator.id(),
                                                req.kodeTargetSasaranOpd(),
                                                BigDecimal.valueOf(req.realisasi()),
                                        req.tahun(),
                                        req.bulan(),
                                        null,
                                        null,
                                        null,
                                        null
                                ))))
                                .thenReturn(sasaran)));
    }

    private Flux<SasaranOpdResponse> toResponseFromStoredData(SasaranOpd sasaran) {
        return indikatorSasaranOpdRepository.findAll()
                .filter(i -> Objects.equals(i.sasaranOpdId(), sasaran.id()))
                .flatMap(indikator -> targetIndikatorSasaranOpdRepository.findAll()
                        .filter(t -> Objects.equals(t.indikatorSasaranId(), indikator.id()))
                        .map(t -> new SasaranOpdResponse.TargetResponse(
                                t.id(),
                                t.kodeTarget(),
                                null,
                                null,
                                parseInteger(t.tahun()),
                                parseInteger(t.bulan()),
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                null,
                                null
                        ))
                        .collectList()
                        .map(targets -> new SasaranOpdResponse.IndikatorResponse(
                                indikator.id(),
                                indikator.kodeIndikator(),
                                null,
                                null,
                                null,
                                null,
                                parseInteger(indikator.tahun()),
                                parseInteger(indikator.bulan()),
                                targets
                        )))
                .collectList()
                .map(indikators -> new SasaranOpdResponse(
                        sasaran.id(),
                        sasaran.kodeOpd(),
                        sasaran.kodeSasaranOpd(),
                        null,
                        parseInteger(sasaran.tahun()),
                        parseInteger(sasaran.bulan()),
                        indikators
                ))
                .flux();
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
