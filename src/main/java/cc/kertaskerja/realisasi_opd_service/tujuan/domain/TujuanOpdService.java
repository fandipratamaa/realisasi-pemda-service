package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.integration.penetapan.PenetapanTujuanOpdClient;
import cc.kertaskerja.integration.penetapan.tujuan_opd.PenetapanTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.PenetapanTujuanOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public Flux<TujuanOpdResponse> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return tujuanOpdRepository
                .findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
                .flatMap(this::toResponseFromStoredData)
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), kodeOpd, tahun));
    }

    public Mono<TujuanOpdResponse> submitRealisasiTujuanOpd(TujuanOpdRequest req) {
        return upsertHierarchy(req)
                .flatMap(tujuan -> toResponseFromStoredData(tujuan).next())
                .flatMap(response -> enrichWithPenetapan(Mono.just(response), req.kodeOpd(), req.tahun()));
    }

    public Flux<TujuanOpdResponse> batchSubmitRealisasiTujuanOpd(List<TujuanOpdRequest> tujuanOpdRequests) {
        return Flux.fromIterable(tujuanOpdRequests)
                .flatMap(this::submitRealisasiTujuanOpd);
    }

    public Mono<PenetapanTujuanOpdListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchTujuanOpd(kodeOpd, tahun)
                .flatMap(penetapanList -> {
                    String rootKodeOpd = penetapanList.isEmpty() ? kodeOpd : penetapanList.getFirst().kodeOpd();
                    Integer effectiveTahun = penetapanList.isEmpty() ? tahun : penetapanList.getFirst().tahunAktif();

                    if (bulan == null || bulan.isBlank()) {
                        List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                                .map(p -> mergePenetapanWithRealisasi(p, null, Set.of()))
                                .filter(response -> !response.indikators().isEmpty())
                                .toList();
                        return Mono.just(new PenetapanTujuanOpdListResponse(rootKodeOpd, effectiveTahun, items));
                    }

                    Mono<Set<String>> hiddenTargetKeys = getHiddenTargetKeysForPreviousMonths(kodeOpd, effectiveTahun, bulan);
                    Mono<Map<String, TujuanOpdResponse>> realisasiMap =
                            getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String.valueOf(effectiveTahun), kodeOpd, bulan)
                                    .collectMap(TujuanOpdResponse::kodeTujuanOpd);

                    return Mono.zip(realisasiMap, hiddenTargetKeys).map(tuple -> {
                                Map<String, TujuanOpdResponse> rMap = tuple.getT1();
                                Set<String> hiddenKeys = tuple.getT2();
                                List<TujuanOpdPenetapanResponse> items = penetapanList.stream()
                                        .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeTujuanOpd()), hiddenKeys))
                                        .filter(response -> !response.indikators().isEmpty())
                                        .toList();
                                return new PenetapanTujuanOpdListResponse(rootKodeOpd, effectiveTahun, items);
                            }
                    );
                });
    }

    private TujuanOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanTujuanOpd.TujuanPenetapanData penetapan,
            TujuanOpdResponse realisasi,
            Set<String> hiddenTargetKeys
    ) {
        Map<String, TujuanOpdResponse.IndikatorResponse> indikatorMap = realisasi != null
                ? realisasi.indikators().stream()
                        .collect(Collectors.toMap(TujuanOpdResponse.IndikatorResponse::kodeIndikator, i -> i))
                : Map.of();

        List<TujuanOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> {
                    TujuanOpdResponse.IndikatorResponse matchedInd = indikatorMap.get(ind.kodeIndikator());
                    Map<String, TujuanOpdResponse.TargetResponse> targetMap = matchedInd != null
                            ? matchedInd.targets().stream()
                                    .collect(Collectors.toMap(TujuanOpdResponse.TargetResponse::kodeTarget, t -> t))
                            : Map.of();

                    List<TujuanOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                            .filter(t -> !hiddenTargetKeys.contains(buildTargetKey(penetapan.kodeTujuanOpd(), ind.kodeIndikator(), t.kodeTarget())))
                            .map(t -> {
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
                                        capaianResult.keteranganCapaian()
                                );
                            })
                            .toList();

                    if (targetList.isEmpty()) {
                        return null;
                    }

                    return new TujuanOpdPenetapanResponse.IndikatorPenetapan(
                            ind.kodeIndikator(),
                            ind.indikator(),
                            ind.rumusPerhitungan(),
                            ind.sumberData(),
                            ind.definisiOperasional(),
                            targetList
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new TujuanOpdPenetapanResponse(
                penetapan.id(),
                penetapan.kodeTujuanOpd(),
                penetapan.tujuanOpd(),
                indikatorList
        );
    }

    // Sembunyikan data penetapan yang sudah di isi realisasinya di bulan tertentu
    private Mono<Set<String>> getHiddenTargetKeysForPreviousMonths(String kodeOpd, int tahun, String bulan) {
        Integer activeMonth = parseInteger(bulan);
        if (activeMonth == null || activeMonth <= 1) {
            return Mono.just(Set.of());
        }

        return tujuanOpdRepository.findAllByTahunAndKodeOpd(String.valueOf(tahun), kodeOpd)
                .filter(tujuan -> {
                    Integer tujuanMonth = parseInteger(tujuan.bulan());
                    return tujuanMonth != null && tujuanMonth < activeMonth;
                })
                .flatMap(this::toResponseFromStoredData)
                .flatMapIterable(response -> response.indikators().stream()
                        .flatMap(indikator -> indikator.targets().stream()
                                .map(target -> buildTargetKey(response.kodeTujuanOpd(), indikator.kodeIndikator(), target.kodeTarget())))
                        .toList())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String buildTargetKey(String kodeTujuanOpd, String kodeIndikator, String kodeTarget) {
        return kodeTujuanOpd + "|" + kodeIndikator + "|" + kodeTarget;
    }

    private Mono<TujuanOpdResponse> enrichWithPenetapan(Mono<TujuanOpdResponse> responseMono, String kodeOpd, String tahun) {
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
            return enrichResponse(response, matching);
        }).onErrorResume(e -> {
            log.warn("Gagal terhubung dengan response penetapan untuk kodeOpd={}, tahun={}: {}",
                    kodeOpd, tahun, e.getMessage());
            return responseMono;
        });
    }

    private TujuanOpdResponse enrichResponse(
            TujuanOpdResponse response,
            PenetapanTujuanOpd.TujuanPenetapanData penetapan
    ) {
        Map<String, PenetapanTujuanOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikators().stream()
                .collect(Collectors.toMap(PenetapanTujuanOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<TujuanOpdResponse.IndikatorResponse> enrichedIndikator = response.indikators().stream()
                .map(ind -> {
                    PenetapanTujuanOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
                    if (matchedInd == null) {
                        return ind;
                    }
                    Map<String, PenetapanTujuanOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.targets().stream()
                            .collect(Collectors.toMap(PenetapanTujuanOpd.TargetPenetapanData::kodeTarget, t -> t));

                    List<TujuanOpdResponse.TargetResponse> enrichedTargets = ind.targets().stream()
                            .map(t -> {
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
                            })
                            .toList();

                    return new TujuanOpdResponse.IndikatorResponse(
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

        return new TujuanOpdResponse(
                response.id(), response.kodeOpd(), response.kodeTujuanOpd(),
                penetapan.tujuanOpd(),
                response.tahun(), response.bulan(),
                enrichedIndikator
        );
    }

    private Mono<TujuanOpd> upsertHierarchy(TujuanOpdRequest req) {
        return tujuanOpdRepository.findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                        req.kodeOpd(), req.kodeTujuanOpd(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> tujuanOpdRepository.save(TujuanOpd.of(
                        req.kodeOpd(),
                        req.kodeTujuanOpd(),
                        req.tahun(),
                        req.bulan()
                ))))
                .flatMap(tujuan -> indikatorTujuanOpdRepository.findFirstByTujuanOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                                        tujuan.id(), req.kodeIndikatorTujuanOpd(), req.kodeOpd(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.defer(() -> indikatorTujuanOpdRepository.save(new IndikatorTujuanOpd(
                                        null,
                                        tujuan.id(),
                                        req.kodeIndikatorTujuanOpd(),
                                        req.kodeOpd(),
                                        req.tahun(),
                                        req.bulan(),
                                        null,
                                        null,
                                        null,
                                        null
                                ))))
                                .flatMap(indikator -> targetIndikatorTujuanOpdRepository.findFirstByIndikatorTujuanIdAndKodeTargetAndTahunAndBulan(
                                                indikator.id(), req.kodeTargetTujuanOpd(), req.tahun(), req.bulan())
                                        .flatMap(existing -> targetIndikatorTujuanOpdRepository.save(new TargetIndikatorTujuanOpd(
                                                existing.id(),
                                                existing.indikatorTujuanId(),
                                                existing.kodeTarget(),
                                                BigDecimal.valueOf(req.realisasi()),
                                                existing.tahun(),
                                                existing.bulan(),
                                                existing.createdDate(),
                                                null,
                                                existing.createdBy(),
                                                null
                                        )))
                                        .switchIfEmpty(Mono.defer(() -> targetIndikatorTujuanOpdRepository.save(new TargetIndikatorTujuanOpd(
                                                null,
                                                indikator.id(),
                                                req.kodeTargetTujuanOpd(),
                                                BigDecimal.valueOf(req.realisasi()),
                                                req.tahun(),
                                                req.bulan(),
                                                null,
                                                null,
                                                null,
                                                null
                                        ))))
                                        .thenReturn(tujuan)));
    }

    private Flux<TujuanOpdResponse> toResponseFromStoredData(TujuanOpd tujuan) {
        return indikatorTujuanOpdRepository.findAll()
                .filter(i -> Objects.equals(i.tujuanOpdId(), tujuan.id()))
                .flatMap(indikator -> targetIndikatorTujuanOpdRepository.findAll()
                        .filter(t -> Objects.equals(t.indikatorTujuanId(), indikator.id()))
                        .map(t -> new TujuanOpdResponse.TargetResponse(
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
                        .map(targets -> new TujuanOpdResponse.IndikatorResponse(
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
                .map(indikators -> new TujuanOpdResponse(
                        tujuan.id(),
                        tujuan.kodeOpd(),
                        tujuan.kodeTujuanOpd(),
                        null,
                        parseInteger(tujuan.tahun()),
                        parseInteger(tujuan.bulan()),
                        indikators
                ))
                .flux();
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
