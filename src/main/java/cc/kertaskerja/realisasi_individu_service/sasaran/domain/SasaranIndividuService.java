package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator.IndikatorSasaranIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.target.TargetIndikatorSasaranIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator.IndikatorSasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.target.TargetIndikatorSasaranIndividu;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.PenetapanSasaranIndividuListResponse;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.SasaranIndividuPenetapanResponse;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.SasaranIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.SasaranIndividuSubmitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SasaranIndividuService {
    private static final Logger log = LoggerFactory.getLogger(SasaranIndividuService.class);
    private final SasaranIndividuRepository sasaranIndividuRepository;
    private final IndikatorSasaranIndividuRepository indikatorSasaranIndividuRepository;
    private final TargetIndikatorSasaranIndividuRepository targetIndikatorIndividuOpdRepository;
    private final SasaranOpdRepository sasaranOpdRepository;
    private final IndikatorSasaranOpdRepository indikatorSasaranOpdRepository;
    private final TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;
    private final PenetapanSasaranOpdClient penetapanClient;

    public SasaranIndividuService(
            SasaranIndividuRepository sasaranIndividuRepository,
            IndikatorSasaranIndividuRepository indikatorSasaranIndividuRepository,
            TargetIndikatorSasaranIndividuRepository targetIndikatorIndividuOpdRepository,
            SasaranOpdRepository sasaranOpdRepository,
            IndikatorSasaranOpdRepository indikatorSasaranOpdRepository,
            TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository,
            PenetapanSasaranOpdClient penetapanClient
    ) {
        this.sasaranIndividuRepository = sasaranIndividuRepository;
        this.indikatorSasaranIndividuRepository = indikatorSasaranIndividuRepository;
        this.targetIndikatorIndividuOpdRepository = targetIndikatorIndividuOpdRepository;
        this.sasaranOpdRepository = sasaranOpdRepository;
        this.indikatorSasaranOpdRepository = indikatorSasaranOpdRepository;
        this.targetIndikatorSasaranOpdRepository = targetIndikatorSasaranOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    public Flux<SasaranIndividuResponse> getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return sasaranIndividuRepository
                .findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
                .flatMap(this::toResponseFromStoredData)
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), kodeOpd, tahun));
    }

    @Transactional
    public Mono<SasaranIndividuResponse> submitRealisasiSasaranIndividu(SasaranIndividuSubmitRequest req) {
        return upsertHierarchy(req)
                .flatMap(sasaran -> upsertOpdHierarchy(req).thenReturn(sasaran))
                .flatMap(sasaran -> toResponseFromStoredData(sasaran).next())
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<SasaranIndividuResponse> batchSubmitRealisasiSasaranIndividu(List<SasaranIndividuSubmitRequest> requests) {
        return Flux.fromIterable(requests)
                .flatMap(this::submitRealisasiSasaranIndividu);
    }

    public Mono<PenetapanSasaranIndividuListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchSasaranOpd(kodeOpd, tahun)
                .flatMap(penetapanList -> {
                    String rootKodeOpd = penetapanList.isEmpty() ? kodeOpd : penetapanList.getFirst().kodeOpd();
                    Integer effectiveTahun = penetapanList.isEmpty() ? tahun : penetapanList.getFirst().tahunAktif();

                    if (bulan == null || bulan.isBlank()) {
                        List<SasaranIndividuPenetapanResponse> items = penetapanList.stream()
                                .map(p -> mergePenetapanWithRealisasi(p, null))
                                .toList();
                        return Mono.just(new PenetapanSasaranIndividuListResponse(rootKodeOpd, effectiveTahun, items));
                    }

                    Mono<Map<String, SasaranIndividuResponse>> realisasiMap =
                            getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(String.valueOf(effectiveTahun), kodeOpd, bulan)
                                    .collectMap(SasaranIndividuResponse::kodeSasaranOpd);

                    return realisasiMap.map(rMap -> {
                        List<SasaranIndividuPenetapanResponse> items = penetapanList.stream()
                                .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeSasaranOpd())))
                                .toList();
                        return new PenetapanSasaranIndividuListResponse(rootKodeOpd, effectiveTahun, items);
                    });
                });
    }

    private SasaranIndividuPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranOpd.SasaranPenetapanData penetapan,
            SasaranIndividuResponse realisasi
    ) {
        Map<String, SasaranIndividuResponse.IndikatorResponse> indikatorMap = realisasi != null
                ? realisasi.indikators().stream()
                .collect(Collectors.toMap(SasaranIndividuResponse.IndikatorResponse::kodeIndikator, i -> i))
                : Map.of();

        List<SasaranIndividuPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> {
                    SasaranIndividuResponse.IndikatorResponse matchedInd = indikatorMap.get(ind.kodeIndikator());
                    Map<String, SasaranIndividuResponse.TargetResponse> targetMap = matchedInd != null
                            ? matchedInd.targets().stream()
                            .collect(Collectors.toMap(SasaranIndividuResponse.TargetResponse::kodeTarget, t -> t))
                            : Map.of();

                    List<SasaranIndividuPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                            .map(t -> {
                                SasaranIndividuResponse.TargetResponse matchedTarget = targetMap.get(t.kodeTarget());
                                Double targetPenetapan = t.target();
                                Double realisasiValue = matchedTarget != null ? matchedTarget.realisasi() : null;
                                var capaianResult = SasaranIndividu.hitungCapaian(realisasiValue, targetPenetapan);
                                return new SasaranIndividuPenetapanResponse.TargetPenetapan(
                                        t.kodeTarget(),
                                        t.satuan(),
                                        targetPenetapan,
                                        realisasiValue,
                                        capaianResult.capaian(),
                                        capaianResult.keteranganCapaian()
                                );
                            })
                            .toList();

                    return new SasaranIndividuPenetapanResponse.IndikatorPenetapan(
                            ind.kodeIndikator(),
                            ind.indikator(),
                            ind.rumusPerhitungan(),
                            ind.sumberData(),
                            ind.definisiOperasional(),
                            targetList
                    );
                })
                .toList();

        return new SasaranIndividuPenetapanResponse(
                penetapan.id(),
                penetapan.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                indikatorList
        );
    }

    private Mono<SasaranIndividuResponse> enrichWithPenetapan(Mono<SasaranIndividuResponse> responseMono, String kodeOpd, String tahun) {
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

    private SasaranIndividuResponse enrichResponse(
            SasaranIndividuResponse response,
            PenetapanSasaranOpd.SasaranPenetapanData penetapan
    ) {
        Map<String, PenetapanSasaranOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikators().stream()
                .collect(Collectors.toMap(PenetapanSasaranOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<SasaranIndividuResponse.IndikatorResponse> enrichedIndikator = response.indikators().stream()
                .map(ind -> {
                    PenetapanSasaranOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
                    if (matchedInd == null) {
                        return ind;
                    }
                    Map<String, PenetapanSasaranOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.targets().stream()
                            .collect(Collectors.toMap(PenetapanSasaranOpd.TargetPenetapanData::kodeTarget, t -> t));

                    List<SasaranIndividuResponse.TargetResponse> enrichedTargets = ind.targets().stream()
                            .map(t -> {
                                PenetapanSasaranOpd.TargetPenetapanData matchedTarget = targetPenetapanMap.get(t.kodeTarget());
                                if (matchedTarget == null) {
                                    return t;
                                }
                                var capaianResult = SasaranIndividu.hitungCapaian(t.realisasi(), matchedTarget.target());
                                return new SasaranIndividuResponse.TargetResponse(
                                        t.id(), t.kodeTarget(),
                                        matchedTarget.target(),
                                        matchedTarget.satuan(),
                                        t.tahun(), t.bulan(), t.realisasi(),
                                        capaianResult.capaian(),
                                        capaianResult.keteranganCapaian()
                                );
                            })
                            .toList();

                    return new SasaranIndividuResponse.IndikatorResponse(
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

        return new SasaranIndividuResponse(
                response.id(), response.kodeOpd(), response.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                response.tahun(), response.bulan(),
                enrichedIndikator
        );
    }

    private Mono<SasaranIndividu> upsertHierarchy(SasaranIndividuSubmitRequest req) {
        return sasaranIndividuRepository.findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSasaranOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> sasaranIndividuRepository.save(SasaranIndividu.of(
                        req.kodeOpd(),
                        req.kodeSasaranOpd(),
                        req.tahun(),
                        req.bulan()
                ))))
                .flatMap(sasaran -> indikatorSasaranIndividuRepository.findFirstBySasaranOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                                        sasaran.id(), req.kodeIndikator(), req.kodeOpd(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.defer(() -> indikatorSasaranIndividuRepository.save(new IndikatorSasaranIndividu(
                                        null,
                                        sasaran.id(),
                                        req.kodeIndikator(),
                                        req.kodeOpd(),
                                        req.tahun(),
                                        req.bulan(),
                                        null,
                                        null,
                                        null,
                                        null
                                ))))
                                .flatMap(indikator -> targetIndikatorIndividuOpdRepository.findFirstByIndikatorSasaranIdAndKodeTargetAndTahunAndBulan(
                                                indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                        .flatMap(existing -> targetIndikatorIndividuOpdRepository.save(new TargetIndikatorSasaranIndividu(
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
                                        .switchIfEmpty(Mono.defer(() -> targetIndikatorIndividuOpdRepository.save(new TargetIndikatorSasaranIndividu(
                                                null,
                                                indikator.id(),
                                                req.kodeTarget(),
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

    private Mono<Void> upsertOpdHierarchy(SasaranIndividuSubmitRequest req) {
        return sasaranOpdRepository.findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSasaranOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> sasaranOpdRepository.save(SasaranOpd.of(
                        req.kodeOpd(),
                        req.kodeSasaranOpd(),
                        req.tahun(),
                        req.bulan()
                ))))
                .flatMap(sasaranOpd -> indikatorSasaranOpdRepository.findFirstBySasaranOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                                        sasaranOpd.id(), req.kodeIndikator(), req.kodeOpd(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.defer(() -> indikatorSasaranOpdRepository.save(new IndikatorSasaranOpd(
                                        null,
                                        sasaranOpd.id(),
                                        req.kodeIndikator(),
                                        req.kodeOpd(),
                                        req.tahun(),
                                        req.bulan(),
                                        null,
                                        null,
                                        null,
                                        null
                                ))))
                                .flatMap(indikator -> targetIndikatorSasaranOpdRepository.findFirstByIndikatorSasaranIdAndKodeTargetAndTahunAndBulan(
                                                indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
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
                                                req.kodeTarget(),
                                                BigDecimal.valueOf(req.realisasi()),
                                                req.tahun(),
                                                req.bulan(),
                                                null,
                                                null,
                                                null,
                                                null
                                        ))))
                                        .then()));
    }

    private Flux<SasaranIndividuResponse> toResponseFromStoredData(SasaranIndividu sasaran) {
        return indikatorSasaranIndividuRepository.findAll()
                .filter(i -> Objects.equals(i.sasaranOpdId(), sasaran.id()))
                .flatMap(indikator -> targetIndikatorIndividuOpdRepository.findAll()
                        .filter(t -> Objects.equals(t.indikatorSasaranId(), indikator.id()))
                        .map(t -> new SasaranIndividuResponse.TargetResponse(
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
                        .map(targets -> new SasaranIndividuResponse.IndikatorResponse(
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
                .map(indikators -> new SasaranIndividuResponse(
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
