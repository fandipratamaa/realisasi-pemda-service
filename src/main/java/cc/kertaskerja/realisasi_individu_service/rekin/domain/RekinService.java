package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRekinIndividuClient;
import cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RekinService {
    private static final Logger log = LoggerFactory.getLogger(RekinService.class);
    private final RekinRepository rekinRepository;
    private final IndikatorRekinRepository indikatorRekinRepository;
    private final TargetIndikatorRekinRepository targetIndikatorRekinRepository;
    private final TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;
    private final PenetapanRekinIndividuClient penetapanClient;

    public RekinService(RekinRepository rekinRepository,
                        IndikatorRekinRepository indikatorRekinRepository,
                        TargetIndikatorRekinRepository targetIndikatorRekinRepository,
                        SasaranOpdRepository sasaranOpdRepository,
                        IndikatorSasaranOpdRepository indikatorSasaranOpdRepository,
                        TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository,
                        PenetapanRekinIndividuClient penetapanClient) {
        this.rekinRepository = rekinRepository;
        this.indikatorRekinRepository = indikatorRekinRepository;
        this.targetIndikatorRekinRepository = targetIndikatorRekinRepository;
        this.targetIndikatorSasaranOpdRepository = targetIndikatorSasaranOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    // --- Rekin header ---

    public Flux<Rekin> getRekinByNipAndTahun(String nip, String tahun) {
        return rekinRepository.findAllByNipAndTahun(nip, tahun);
    }

    public Flux<Rekin> getRekinByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return rekinRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    public Flux<Rekin> getRekinByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return rekinRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Flux<Rekin> getRekinByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return rekinRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Mono<RekinWithDetails> createRekin(RekinRequest req) {
        return rekinRepository.findFirstByNipAndTahunAndBulanAndKodePkRekin(
                        req.nip(), req.tahun(), req.bulan(), req.kodePkRekin())
                .flatMap(existing -> rekinRepository.save(buildUpdatedRekin(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Rekin baru = buildUncheckedRekin(
                            req.kodeOpd(), req.nip(), req.kodePkRekin(), req.kodeSasaranOpd(),
                            req.tahun(), req.bulan());
                    return rekinRepository.save(baru);
                }))
                .flatMap(savedRekin ->
                        saveIndikatorAndTarget(savedRekin, req)
                                .flatMap(details ->
                                        syncToSasaranOpd(savedRekin, req)
                                                .then(Mono.just(details)))
                );
    }

    public Mono<TargetIndikatorRekin> updateFaktorPenunjang(FaktorPenunjangRekinRequest req) {
        return rekinRepository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodePkRekin())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(rekin -> indikatorRekinRepository
                        .findFirstByRekinIdAndKodeIndikatorPkRekin(rekin.id(), req.kodeIndikator())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator rekin tidak ditemukan")))
                        .flatMap(indikator -> targetIndikatorRekinRepository
                                .findFirstByIndikatorRekinIdAndKodeTargetPkRekin(indikator.id(), req.kodeTarget())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator rekin tidak ditemukan")))
                                .flatMap(existing -> {
                                    TargetIndikatorRekin updated = new TargetIndikatorRekin(
                                            existing.id(),
                                            existing.indikatorRekinId(),
                                            existing.kodeTargetPkRekin(),
                                            existing.kodeOpd(),
                                            existing.nip(),
                                            existing.tahun(),
                                            existing.bulan(),
                                            existing.target(),
                                            existing.realisasi(),
                                            existing.jenisRealisasi(),
                                            req.faktorPenunjang(),
                                            existing.faktorPenghambat(),
                                            existing.createdBy(),
                                            existing.lastModifiedBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate()
                                    );
                                    return targetIndikatorRekinRepository.save(updated);
                                })
                        )
                );
    }

    public Mono<TargetIndikatorRekin> updateFaktorPenghambat(FaktorPenghambatRekinRequest req) {
        return rekinRepository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodePkRekin())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(rekin -> indikatorRekinRepository
                        .findFirstByRekinIdAndKodeIndikatorPkRekin(rekin.id(), req.kodeIndikator())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator rekin tidak ditemukan")))
                        .flatMap(indikator -> targetIndikatorRekinRepository
                                .findFirstByIndikatorRekinIdAndKodeTargetPkRekin(indikator.id(), req.kodeTarget())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator rekin tidak ditemukan")))
                                .flatMap(existing -> {
                                    TargetIndikatorRekin updated = new TargetIndikatorRekin(
                                            existing.id(),
                                            existing.indikatorRekinId(),
                                            existing.kodeTargetPkRekin(),
                                            existing.kodeOpd(),
                                            existing.nip(),
                                            existing.tahun(),
                                            existing.bulan(),
                                            existing.target(),
                                            existing.realisasi(),
                                            existing.jenisRealisasi(),
                                            existing.faktorPenunjang(),
                                            req.faktorPenghambat(),
                                            existing.createdBy(),
                                            existing.lastModifiedBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate()
                                    );
                                    return targetIndikatorRekinRepository.save(updated);
                                })
                        )
                );
    }

    private Mono<RekinWithDetails> saveIndikatorAndTarget(Rekin rekin, RekinRequest req) {
        return findOrCreateIndikator(rekin, req)
                .flatMap(indikator -> upsertTarget(indikator.id(), req))
                .then(Mono.defer(() -> enrichWithDetails(rekin)));
    }

    private Mono<IndikatorRekin> findOrCreateIndikator(Rekin rekin, RekinRequest req) {
        return indikatorRekinRepository
                .findFirstByRekinIdAndKodeIndikatorPkRekin(rekin.id(), req.kodeIndikatorPKrekin())
                .switchIfEmpty(Mono.defer(() -> {
                    IndikatorRekin baru = IndikatorRekin.of(
                            rekin.id(), req.kodeIndikatorPKrekin(), "Realisasi Indikator " + req.kodeIndikatorPKrekin(),
                            rekin.kodeOpd(), rekin.nip(), rekin.tahun(), rekin.bulan());
                    return indikatorRekinRepository.save(baru);
                }));
    }

    private Mono<Void> upsertTarget(Long indikatorId, RekinRequest req) {
        return targetIndikatorRekinRepository
                .findFirstByIndikatorRekinIdAndKodeTargetPkRekin(indikatorId, req.kodeTargetPKrekin())
                .flatMap(existing -> {
                    TargetIndikatorRekin updated = new TargetIndikatorRekin(
                            existing.id(),
                            existing.indikatorRekinId(),
                            existing.kodeTargetPkRekin(),
                            req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                            req.target(), req.realisasi(), req.jenisRealisasi(),
                            existing.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return targetIndikatorRekinRepository.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    TargetIndikatorRekin baru = TargetIndikatorRekin.of(
                            indikatorId, req.kodeTargetPKrekin(),
                            req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                            req.target(), req.realisasi(), req.jenisRealisasi(),
                            "", "");
                    return targetIndikatorRekinRepository.save(baru);
                }))
                .then();
    }

    public static Rekin buildUncheckedRekin(
            String kodeOpd,
            String nip,
            String kodePkRekin,
            String kodeSasaranOpd,
            String tahun,
            String bulan) {
        return Rekin.of(kodeOpd, nip, kodePkRekin, kodeSasaranOpd, 0, "", "Realisasi Rekin " + kodePkRekin,
                tahun, bulan, RekinStatus.UNCHECKED);
    }

    private static Rekin buildUpdatedRekin(Rekin existing, RekinRequest req) {
        return new Rekin(
                existing.id(),
                req.kodeOpd(),
                req.nip(),
                req.kodePkRekin(),
                req.kodeSasaranOpd(),
                existing.rekin(),
                req.tahun(),
                req.bulan(),
                RekinStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate()
        );
    }

    // --- Sync ke Sasaran OPD ---

    private Mono<Void> syncToSasaranOpd(Rekin rekin, RekinRequest req) {
        return targetIndikatorSasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorSasaranOpdAndKodeTargetSasaranOpdAndTahunAndBulan(
                        rekin.kodeOpd(), req.kodeSasaranOpd(), req.kodeIndikatorPKrekin(),
                        req.kodeTargetPKrekin(), rekin.tahun(), rekin.bulan())
                .flatMap(existing -> {
                    TargetIndikatorSasaranOpd updated = new TargetIndikatorSasaranOpd(
                            existing.id(), existing.indikatorSasaranId(),
                            existing.kodeTarget(), req.realisasi(),
                            existing.tahun(), existing.bulan(),
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdDate(), existing.lastModifiedDate(),
                            existing.createdBy(), existing.lastModifiedBy());
                    return targetIndikatorSasaranOpdRepository.save(updated);
                })
                .then();
    }

    // --- Indikator ---

    public Flux<IndikatorRekin> getIndikatorByRekinId(Long rekinId) {
        return indikatorRekinRepository.findAllByRekinId(rekinId);
    }

    // --- Target ---

    public Flux<TargetIndikatorRekin> getTargetByIndikatorRekinId(Long indikatorRekinId) {
        return targetIndikatorRekinRepository.findAllByIndikatorRekinId(indikatorRekinId);
    }

    // --- Combined query (for GET responses) ---

    public Flux<RekinWithDetails> getRekinWithDetailsByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return rekinRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan)
                .flatMap(this::enrichWithDetails);
    }

    public Flux<RekinWithDetails> getRekinWithDetailsByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return rekinRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .flatMap(this::enrichWithDetails);
    }

    private Mono<RekinWithDetails> enrichWithDetails(Rekin rekin) {
        return indikatorRekinRepository.findAllByRekinId(rekin.id())
                .collectList()
                .flatMap(indicators -> {
                    if (indicators.isEmpty()) {
                        return Mono.just(new RekinWithDetails(rekin, indicators, Collections.emptyList()));
                    }
                    return targetIndikatorRekinRepository
                            .findAllByIndikatorRekinIdIn(
                                    indicators.stream().map(IndikatorRekin::id).toList())
                            .collectList()
                            .map(targets -> new RekinWithDetails(rekin, indicators, targets));
                });
    }

    // --- Penetapan Integration ---

    public Mono<PenetapanRekinIndividuResponse> getPenetapanByNip(String nip, String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRekinIndividu(nip, kodeOpd, tahun)
                .flatMap(data -> {
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(toResponseWithoutBulan(data));
                    }
                    return buildResponseWithBulan(data, nip, kodeOpd, tahun, bulan);
                });
    }

    private PenetapanRekinIndividuResponse toResponseWithoutBulan(PenetapanRekinIndividu.RekinIndividuData data) {
        List<PenetapanRekinIndividuResponse.RekinPenetapanResponse> rekins = data.rekins().stream()
                .map(this::mapRekinToResponse)
                .toList();
        return new PenetapanRekinIndividuResponse(
                data.pegawaiId(), data.nama(), data.kodeOpd(), data.tahunAktif(), null, rekins
        );
    }

    private PenetapanRekinIndividuResponse.RekinPenetapanResponse mapRekinToResponse(
            PenetapanRekinIndividu.RekinData rekin) {
        List<PenetapanRekinIndividuResponse.IndikatorPenetapanResponse> indikators = rekin.indikatorPk().stream()
                .map(this::mapIndikatorToResponse)
                .toList();
        return new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                rekin.id(), rekin.kodePk(), rekin.rekin(), rekin.versi(), indikators
        );
    }

    private PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mapIndikatorToResponse(
            PenetapanRekinIndividu.IndikatorRekinData indikator) {
        List<PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                        t.id(), t.kodeTargetPk(), t.tahun(), t.target(), t.satuan(),
                        null, null, null, null, null
                ))
                .toList();
        return new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                indikator.id(), indikator.kodeIndikatorPk(), indikator.namaIndikatorPk(), targets
        );
    }

    private Mono<PenetapanRekinIndividuResponse> buildResponseWithBulan(
            PenetapanRekinIndividu.RekinIndividuData data,
            String nip, String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);
        return rekinRepository.findAllByNipAndTahunAndBulan(nip, tahunStr, bulan)
                .filter(r -> r.kodeOpd().equals(kodeOpd))
                .flatMap(this::enrichWithDetails)
                .collectList()
                .map(localList -> {
                    Map<String, TargetIndikatorRekin> localTargetMap = buildLocalTargetMap(localList);
                    List<PenetapanRekinIndividuResponse.RekinPenetapanResponse> rekins = data.rekins().stream()
                            .map(r -> mergeRekinWithRealisasi(r, localTargetMap))
                            .toList();
                    return new PenetapanRekinIndividuResponse(
                            data.pegawaiId(), data.nama(), data.kodeOpd(), data.tahunAktif(),
                            parseInteger(bulan), rekins
                    );
                });
    }

    private Map<String, TargetIndikatorRekin> buildLocalTargetMap(List<RekinWithDetails> localList) {
        return localList.stream()
                .flatMap(rwd -> rwd.indikators().stream()
                        .flatMap(ind -> rwd.targets().stream()
                                .filter(t -> t.indikatorRekinId().equals(ind.id()))
                                .map(t -> Map.entry(buildTargetKey(rwd.rekin().kodePkRekin(), ind.kodeIndikatorPkRekin(), t.kodeTargetPkRekin()), t))
                        )
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String buildTargetKey(String kodePkRekin, String kodeIndikatorPkRekin, String kodeTargetPkRekin) {
        return kodePkRekin + "|" + kodeIndikatorPkRekin + "|" + kodeTargetPkRekin;
    }

    private PenetapanRekinIndividuResponse.RekinPenetapanResponse mergeRekinWithRealisasi(
            PenetapanRekinIndividu.RekinData rekin,
            Map<String, TargetIndikatorRekin> localTargetMap
    ) {
        List<PenetapanRekinIndividuResponse.IndikatorPenetapanResponse> indikators = rekin.indikatorPk().stream()
                .map(ind -> mergeIndikatorWithRealisasi(rekin.kodePk(), ind, localTargetMap))
                .toList();
        return new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                rekin.id(), rekin.kodePk(), rekin.rekin(),
                rekin.versi(), indikators
        );
    }

    private PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mergeIndikatorWithRealisasi(
            String kodePk,
            PenetapanRekinIndividu.IndikatorRekinData indikator,
            Map<String, TargetIndikatorRekin> localTargetMap
    ) {
        List<PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> mergeTargetWithRealisasi(kodePk, indikator.kodeIndikatorPk(), t, localTargetMap))
                .toList();
        return new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                indikator.id(), indikator.kodeIndikatorPk(), indikator.namaIndikatorPk(), targets
        );
    }

    private PenetapanRekinIndividuResponse.TargetPenetapanResponse mergeTargetWithRealisasi(
            String kodePk, String kodeIndikatorPk,
            PenetapanRekinIndividu.TargetRekinData target,
            Map<String, TargetIndikatorRekin> localTargetMap
    ) {
        String key = buildTargetKey(kodePk, kodeIndikatorPk, target.kodeTargetPk());
        TargetIndikatorRekin local = localTargetMap.get(key);
        Double realisasiValue = local != null && local.realisasi() != null
                ? local.realisasi().doubleValue() : null;
        Rekin.CapaianResult capaianResult = Rekin.hitungCapaian(realisasiValue, target.target());
        String faktorPenunjang = local != null ? local.faktorPenunjang() : null;
        String faktorPenghambat = local != null ? local.faktorPenghambat() : null;
        return new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                target.id(), target.kodeTargetPk(), target.tahun(), target.target(), target.satuan(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat
        );
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
