package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service

public class RekinService {
    private final RekinRepository rekinRepository;
    private final IndikatorRekinRepository indikatorRekinRepository;
    private final TargetIndikatorRekinRepository targetIndikatorRekinRepository;
    private final SasaranOpdRepository sasaranOpdRepository;
    private final IndikatorSasaranOpdRepository indikatorSasaranOpdRepository;
    private final TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;

    public RekinService(RekinRepository rekinRepository,
                        IndikatorRekinRepository indikatorRekinRepository,
                        TargetIndikatorRekinRepository targetIndikatorRekinRepository,
                        SasaranOpdRepository sasaranOpdRepository,
                        IndikatorSasaranOpdRepository indikatorSasaranOpdRepository,
                        TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository) {
        this.rekinRepository = rekinRepository;
        this.indikatorRekinRepository = indikatorRekinRepository;
        this.targetIndikatorRekinRepository = targetIndikatorRekinRepository;
        this.sasaranOpdRepository = sasaranOpdRepository;
        this.indikatorSasaranOpdRepository = indikatorSasaranOpdRepository;
        this.targetIndikatorSasaranOpdRepository = targetIndikatorSasaranOpdRepository;
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
        Mono<Rekin> rekinMono;
        if (req.id() != null) {
            rekinMono = rekinRepository.findById(req.id())
                    .flatMap(existing -> rekinRepository.save(buildUpdatedRekin(existing, req)))
                    .switchIfEmpty(Mono.defer(() ->
                            rekinRepository.findFirstByNipAndTahunAndBulanAndKodeRekin(
                                            req.nip(), req.tahun(), req.bulan(), req.kodeRekin())
                                    .flatMap(existing -> rekinRepository.save(buildUpdatedRekin(existing, req)))
                                    .switchIfEmpty(Mono.defer(() -> {
                                        Rekin baru = buildUncheckedRekin(
                                                req.kodeOpd(), req.nip(), req.kodeRekin(), req.kodeSasaranOpd(),
                                                req.tahun(), req.bulan());
                                        return rekinRepository.save(baru);
                                    }))
                    ));
        } else {
            rekinMono = rekinRepository.findFirstByNipAndTahunAndBulanAndKodeRekin(
                            req.nip(), req.tahun(), req.bulan(), req.kodeRekin())
                    .flatMap(existing -> rekinRepository.save(buildUpdatedRekin(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Rekin baru = buildUncheckedRekin(
                                req.kodeOpd(), req.nip(), req.kodeRekin(), req.kodeSasaranOpd(),
                                req.tahun(), req.bulan());
                        return rekinRepository.save(baru);
                    }));
        }

        return rekinMono
                .flatMap(savedRekin ->
                        deleteExistingIndikatorsAndTargets(savedRekin.id())
                                .then(saveIndikatorAndTarget(savedRekin, req))
                                .flatMap(details ->
                                        syncToSasaranOpd(savedRekin, req)
                                                .then(Mono.just(details)))
                );
    }

    public Mono<TargetIndikatorRekin> updateFaktorPenunjang(FaktorPenunjangRekinRequest req) {
        return rekinRepository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodeRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodeRekin())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(rekin -> indikatorRekinRepository
                        .findFirstByRekinIdAndKodeIndikator(rekin.id(), req.kodeIndikator())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator rekin tidak ditemukan")))
                        .flatMap(indikator -> targetIndikatorRekinRepository
                                .findFirstByIndikatorRekinIdAndKodeTarget(indikator.id(), req.kodeTarget())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator rekin tidak ditemukan")))
                                .flatMap(existing -> {
                                    TargetIndikatorRekin updated = new TargetIndikatorRekin(
                                            existing.id(),
                                            existing.indikatorRekinId(),
                                            existing.kodeTarget(),
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
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodeRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodeRekin())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(rekin -> indikatorRekinRepository
                        .findFirstByRekinIdAndKodeIndikator(rekin.id(), req.kodeIndikator())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator rekin tidak ditemukan")))
                        .flatMap(indikator -> targetIndikatorRekinRepository
                                .findFirstByIndikatorRekinIdAndKodeTarget(indikator.id(), req.kodeTarget())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator rekin tidak ditemukan")))
                                .flatMap(existing -> {
                                    TargetIndikatorRekin updated = new TargetIndikatorRekin(
                                            existing.id(),
                                            existing.indikatorRekinId(),
                                            existing.kodeTarget(),
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

    private Mono<Void> deleteExistingIndikatorsAndTargets(Long rekinId) {
        return indikatorRekinRepository.findAllByRekinId(rekinId)
                .collectList()
                .flatMap(indikators -> {
                    if (indikators.isEmpty()) return Mono.empty();
                    List<Long> indikatorIds = indikators.stream().map(IndikatorRekin::id).toList();
                    return targetIndikatorRekinRepository
                            .findAllByIndikatorRekinIdIn(indikatorIds)
                            .collectList()
                            .flatMap(targets -> {
                                if (targets.isEmpty()) {
                                    return indikatorRekinRepository.deleteAll(indikators);
                                }
                                return targetIndikatorRekinRepository.deleteAll(targets)
                                        .then(indikatorRekinRepository.deleteAll(indikators));
                            });
                });
    }

    private Mono<RekinWithDetails> saveIndikatorAndTarget(Rekin rekin, RekinRequest req) {
        IndikatorRekin indikator = IndikatorRekin.of(
                rekin.id(), req.kodeIndikator(), "Realisasi Indikator " + req.kodeIndikator(),
                rekin.kodeOpd(), rekin.nip(), rekin.tahun(), rekin.bulan()
        );
        return indikatorRekinRepository.save(indikator)
                .flatMap(savedInd -> {
                    TargetIndikatorRekin target = TargetIndikatorRekin.of(
                            savedInd.id(), req.kodeTarget(),
                            rekin.kodeOpd(), rekin.nip(), rekin.tahun(), rekin.bulan(),
                            req.target(), req.realisasi(), req.jenisRealisasi(),
                            "", ""
                    );
                    return targetIndikatorRekinRepository.save(target);
                })
                .then(Mono.defer(() -> enrichWithDetails(rekin)));
    }

    public static Rekin buildUncheckedRekin(
            String kodeOpd,
            String nip,
            String kodeRekin,
            String kodeSasaranOpd,
            String tahun,
            String bulan) {
        return Rekin.of(kodeOpd, nip, kodeRekin, kodeSasaranOpd, "Realisasi Rekin " + kodeRekin,
                tahun, bulan, RekinStatus.UNCHECKED);
    }

    private static Rekin buildUpdatedRekin(Rekin existing, RekinRequest req) {
        return new Rekin(
                existing.id(),
                req.kodeOpd(),
                req.nip(),
                req.kodeRekin(),
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
                        rekin.kodeOpd(), req.kodeSasaranOpd(), req.kodeIndikator(),
                        req.kodeTarget(), rekin.tahun(), rekin.bulan())
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
}
