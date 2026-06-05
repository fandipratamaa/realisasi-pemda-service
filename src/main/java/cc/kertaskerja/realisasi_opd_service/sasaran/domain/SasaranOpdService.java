package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.integration.penetapan.sasaran_opd.PenetapanSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public Mono<PenetapanSasaranOpdListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchSasaranOpd(kodeOpd, tahun)
                .flatMap(penetapanList -> {
                    String rootKodeOpd = penetapanList.isEmpty() ? kodeOpd : penetapanList.getFirst().kodeOpd();
                    Integer effectiveTahun = penetapanList.isEmpty() ? tahun : penetapanList.getFirst().tahunAktif();

                    if (bulan == null || bulan.isBlank()) {
                        List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                                .map(p -> mergePenetapanWithRealisasi(p, null, Set.of()))
                                .filter(response -> !response.indikators().isEmpty())
                                .toList();
                        return Mono.just(new PenetapanSasaranOpdListResponse(rootKodeOpd, effectiveTahun, null, items));
                    }

                    Mono<Set<String>> hiddenTargetKeys = getHiddenTargetKeysForPreviousMonths(kodeOpd, effectiveTahun, bulan);
                    Mono<Map<String, SasaranOpdResponse>> realisasiMap =
                            getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(String.valueOf(effectiveTahun), kodeOpd, bulan)
                                    .collectMap(SasaranOpdResponse::kodeSasaranOpd);

                    return Mono.zip(realisasiMap, hiddenTargetKeys).map(tuple -> {
                        Map<String, SasaranOpdResponse> rMap = tuple.getT1();
                        Set<String> hiddenKeys = tuple.getT2();
                        List<SasaranOpdPenetapanResponse> items = penetapanList.stream()
                                .map(p -> mergePenetapanWithRealisasi(p, rMap.get(p.kodeSasaranOpd()), hiddenKeys))
                                .filter(response -> !response.indikators().isEmpty())
                                .toList();
                        return new PenetapanSasaranOpdListResponse(rootKodeOpd, effectiveTahun, parseInteger(bulan), items);
                    });
                });
    }

    private SasaranOpdPenetapanResponse mergePenetapanWithRealisasi(
            PenetapanSasaranOpd.SasaranPenetapanData penetapan,
            SasaranOpdResponse realisasi,
            Set<String> hiddenTargetKeys
    ) {
        Map<String, SasaranOpdResponse.IndikatorResponse> indikatorMap = realisasi != null
                ? realisasi.indikators().stream()
                        .collect(Collectors.toMap(SasaranOpdResponse.IndikatorResponse::kodeIndikator, i -> i))
                : Map.of();

        List<SasaranOpdPenetapanResponse.IndikatorPenetapan> indikatorList = penetapan.indikators().stream()
                .map(ind -> {
                    SasaranOpdResponse.IndikatorResponse matchedInd = indikatorMap.get(ind.kodeIndikator());
                    Map<String, SasaranOpdResponse.TargetResponse> targetMap = matchedInd != null
                            ? matchedInd.targets().stream()
                                    .collect(Collectors.toMap(SasaranOpdResponse.TargetResponse::kodeTarget, t -> t))
                            : Map.of();

                    List<SasaranOpdPenetapanResponse.TargetPenetapan> targetList = ind.targets().stream()
                            .filter(t -> !hiddenTargetKeys.contains(buildTargetKey(penetapan.kodeSasaranOpd(), ind.kodeIndikator(), t.kodeTarget())))
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

                    if (targetList.isEmpty()) {
                        return null;
                    }

                    return new SasaranOpdPenetapanResponse.IndikatorPenetapan(
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

        return new SasaranOpdPenetapanResponse(
                penetapan.id(),
                penetapan.kodeSasaranOpd(),
                penetapan.sasaranOpd(),
                indikatorList
        );
    }

    private Mono<Set<String>> getHiddenTargetKeysForPreviousMonths(String kodeOpd, int tahun, String bulan) {
        Integer activeMonth = parseInteger(bulan);
        if (activeMonth == null) {
            return Mono.just(Set.of());
        }

        return sasaranOpdRepository.findAllByTahunAndKodeOpd(String.valueOf(tahun), kodeOpd)
                .filter(sasaran -> {
                    Integer sasaranMonth = parseInteger(sasaran.bulan());
                    return sasaranMonth != null && !sasaranMonth.equals(activeMonth);
                })
                .flatMap(this::toResponseFromStoredData)
                .flatMapIterable(response -> response.indikators().stream()
                        .flatMap(indikator -> indikator.targets().stream()
                                .map(target -> buildTargetKey(response.kodeSasaranOpd(), indikator.kodeIndikator(), target.kodeTarget())))
                        .toList())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String buildTargetKey(String kodeSasaranOpd, String kodeIndikator, String kodeTarget) {
        return kodeSasaranOpd + "|" + kodeIndikator + "|" + kodeTarget;
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
        Map<String, PenetapanSasaranOpd.IndikatorPenetapanData> indikatorPenetapanMap = penetapan.indikators().stream()
                .collect(Collectors.toMap(PenetapanSasaranOpd.IndikatorPenetapanData::kodeIndikator, i -> i));

        List<SasaranOpdResponse.IndikatorResponse> enrichedIndikator = response.indikators().stream()
                .map(ind -> {
                    PenetapanSasaranOpd.IndikatorPenetapanData matchedInd = indikatorPenetapanMap.get(ind.kodeIndikator());
                    if (matchedInd == null) {
                        return ind;
                    }
                    Map<String, PenetapanSasaranOpd.TargetPenetapanData> targetPenetapanMap = matchedInd.targets().stream()
                            .collect(Collectors.toMap(PenetapanSasaranOpd.TargetPenetapanData::kodeTarget, t -> t));

                    List<SasaranOpdResponse.TargetResponse> enrichedTargets = ind.targets().stream()
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
                response.faktorPenunjang(), response.faktorPenghambat(),
                enrichedIndikator
        );
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
                        sasaran.faktorPenunjang(),
                        sasaran.faktorPenghambat(),
                        indikators
                ))
                .flux();
    }

    public Mono<SasaranOpd> updateFaktorPenunjang(String kodeOpd, String kodeSasaranOpd, String tahun, String bulan, String faktorPenunjang) {
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(kodeOpd, kodeSasaranOpd, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran OPD tidak ditemukan")))
                .flatMap(existing -> {
                    SasaranOpd updated = new SasaranOpd(
                            existing.id(),
                            existing.kodeOpd(),
                            existing.kodeSasaranOpd(),
                            existing.tahun(),
                            existing.bulan(),
                            faktorPenunjang,
                            existing.faktorPenghambat(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranOpdRepository.save(updated);
                });
    }

    public Mono<SasaranOpd> updateFaktorPenghambat(String kodeOpd, String kodeSasaranOpd, String tahun, String bulan, String faktorPenghambat) {
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(kodeOpd, kodeSasaranOpd, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran OPD tidak ditemukan")))
                .flatMap(existing -> {
                    SasaranOpd updated = new SasaranOpd(
                            existing.id(),
                            existing.kodeOpd(),
                            existing.kodeSasaranOpd(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.faktorPenunjang(),
                            faktorPenghambat,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranOpdRepository.save(updated);
                });
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
