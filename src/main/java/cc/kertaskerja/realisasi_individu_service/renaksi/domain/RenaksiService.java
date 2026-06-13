package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.indikator.IndikatorRenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.indikator.IndikatorRenaksiIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.target.TargetIndikatorRenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.target.TargetIndikatorRenaksiIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenghambatRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenunjangRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiIndividuRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class RenaksiService {
    private final SasaranIndividuRepository sasaranIndividuRepository;
    private final RenaksiIndividuRepository renaksiIndividuRepository;
    private final IndikatorRenaksiIndividuRepository indikatorRenaksiIndividuRepository;
    private final TargetIndikatorRenaksiIndividuRepository targetIndikatorRenaksiRepository;

    public RenaksiService(SasaranIndividuRepository sasaranIndividuRepository,
                          RenaksiIndividuRepository renaksiIndividuRepository,
                          IndikatorRenaksiIndividuRepository indikatorRenaksiIndividuRepository,
                          TargetIndikatorRenaksiIndividuRepository targetIndikatorRenaksiRepository) {
        this.sasaranIndividuRepository = sasaranIndividuRepository;
        this.renaksiIndividuRepository = renaksiIndividuRepository;
        this.indikatorRenaksiIndividuRepository = indikatorRenaksiIndividuRepository;
        this.targetIndikatorRenaksiRepository = targetIndikatorRenaksiRepository;
    }

    // --- Sasaran header ---

    public Flux<SasaranIndividu> getSasaranByNipAndTahun(String nip, String tahun) {
        return sasaranIndividuRepository.findAllByNipAndTahun(nip, tahun);
    }

    public Flux<SasaranIndividu> getSasaranByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    public Flux<SasaranIndividu> getSasaranByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Mono<SasaranWithDetails> createSasaran(RenaksiIndividuRequest req) {
        Mono<SasaranIndividu> sasaranMono;
        if (req.id() != null) {
            sasaranMono = sasaranIndividuRepository.findById(req.id())
                    .flatMap(existing -> sasaranIndividuRepository.save(buildUpdatedSasaran(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        SasaranIndividu baru = buildUncheckedSasaran(
                                req.kodeOpd(), req.nip(), req.kodeSasaran(),
                                req.tahun(), req.bulan());
                        return sasaranIndividuRepository.save(baru);
                    }));
        } else {
            sasaranMono = sasaranIndividuRepository.findFirstByNipAndTahunAndBulanAndKodeSasaran(
                            req.nip(), req.tahun(), req.bulan(), req.kodeSasaran())
                    .flatMap(existing -> sasaranIndividuRepository.save(buildUpdatedSasaran(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        SasaranIndividu baru = buildUncheckedSasaran(
                                req.kodeOpd(), req.nip(), req.kodeSasaran(),
                                req.tahun(), req.bulan());
                        return sasaranIndividuRepository.save(baru);
                    }));
        }

        return sasaranMono
                .flatMap(savedSasaran ->
                        deleteExistingRenaksiAndDescendants(savedSasaran.id())
                                .then(saveRenaksiAndTarget(savedSasaran, req))
                );
    }

    public Mono<TargetIndikatorRenaksiIndividu> updateFaktorPenunjang(FaktorPenunjangRenaksiRequest req) {
        return sasaranIndividuRepository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodeSasaran(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodeSasaran())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(sasaran -> renaksiIndividuRepository
                        .findFirstBySasaranIdAndKodeRenaksi(sasaran.id(), req.kodeRenaksi())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Renaksi tidak ditemukan")))
                        .flatMap(renaksi -> indikatorRenaksiIndividuRepository
                                .findFirstByRenaksiIdAndKodeIndikator(renaksi.id(), req.kodeIndikator())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator renaksi tidak ditemukan")))
                                .flatMap(indikator -> targetIndikatorRenaksiRepository
                                        .findFirstByIndikatorRenaksiIdAndKodeTarget(indikator.id(), req.kodeTarget())
                                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator renaksi tidak ditemukan")))
                                        .flatMap(existing -> {
                                            TargetIndikatorRenaksiIndividu updated = new TargetIndikatorRenaksiIndividu(
                                                    existing.id(),
                                                    existing.indikatorRenaksiId(),
                                                    existing.kodeTarget(),
                                                    existing.kodeOpd(),
                                                    existing.nip(),
                                                    existing.tahun(),
                                                    existing.bulan(),
                                                    existing.paguAnggaran(),
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
                                            return targetIndikatorRenaksiRepository.save(updated);
                                        })
                                )
                        )
                );
    }

    public Mono<TargetIndikatorRenaksiIndividu> updateFaktorPenghambat(FaktorPenghambatRenaksiRequest req) {
        return sasaranIndividuRepository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodeSasaran(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(), req.kodeSasaran())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(sasaran -> renaksiIndividuRepository
                        .findFirstBySasaranIdAndKodeRenaksi(sasaran.id(), req.kodeRenaksi())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Renaksi tidak ditemukan")))
                        .flatMap(renaksi -> indikatorRenaksiIndividuRepository
                                .findFirstByRenaksiIdAndKodeIndikator(renaksi.id(), req.kodeIndikator())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator renaksi tidak ditemukan")))
                                .flatMap(indikator -> targetIndikatorRenaksiRepository
                                        .findFirstByIndikatorRenaksiIdAndKodeTarget(indikator.id(), req.kodeTarget())
                                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target indikator renaksi tidak ditemukan")))
                                        .flatMap(existing -> {
                                            TargetIndikatorRenaksiIndividu updated = new TargetIndikatorRenaksiIndividu(
                                                    existing.id(),
                                                    existing.indikatorRenaksiId(),
                                                    existing.kodeTarget(),
                                                    existing.kodeOpd(),
                                                    existing.nip(),
                                                    existing.tahun(),
                                                    existing.bulan(),
                                                    existing.paguAnggaran(),
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
                                            return targetIndikatorRenaksiRepository.save(updated);
                                        })
                                )
                        )
                );
    }

    private Mono<Void> deleteExistingRenaksiAndDescendants(Long sasaranId) {
        return renaksiIndividuRepository.findAllBySasaranId(sasaranId)
                .collectList()
                .flatMap(renaksis -> {
                    if (renaksis.isEmpty()) return Mono.empty();
                    List<Long> renaksiIds = renaksis.stream().map(RenaksiIndividu::id).toList();
                    return indikatorRenaksiIndividuRepository.findAllByRenaksiIdIn(renaksiIds)
                            .collectList()
                            .flatMap(indikators -> {
                                if (indikators.isEmpty()) {
                                    return renaksiIndividuRepository.deleteAll(renaksis);
                                }
                                List<Long> indikatorIds = indikators.stream().map(IndikatorRenaksiIndividu::id).toList();
                                return targetIndikatorRenaksiRepository.findAllByIndikatorRenaksiIdIn(indikatorIds)
                                        .collectList()
                                        .flatMap(targets -> {
                                            if (targets.isEmpty()) {
                                                return indikatorRenaksiIndividuRepository.deleteAll(indikators)
                                                        .then(renaksiIndividuRepository.deleteAll(renaksis));
                                            }
                                            return targetIndikatorRenaksiRepository.deleteAll(targets)
                                                    .then(indikatorRenaksiIndividuRepository.deleteAll(indikators))
                                                    .then(renaksiIndividuRepository.deleteAll(renaksis));
                                        });
                            });
                });
    }

    private Mono<SasaranWithDetails> saveRenaksiAndTarget(SasaranIndividu sasaran, RenaksiIndividuRequest req) {
        RenaksiIndividu renaksi = RenaksiIndividu.of(
                sasaran.id(), sasaran.kodeOpd(), sasaran.nip(), req.kodeRenaksi(),
                "Realisasi Renaksi " + req.kodeRenaksi(),
                sasaran.tahun(), sasaran.bulan(), RenaksiStatus.UNCHECKED
        );
        return renaksiIndividuRepository.save(renaksi)
                .flatMap(savedRenaksi -> {
                    IndikatorRenaksiIndividu indikator = IndikatorRenaksiIndividu.of(
                            savedRenaksi.id(), req.kodeIndikator(), "Realisasi Indikator " + req.kodeIndikator(),
                            sasaran.kodeOpd(), sasaran.nip(), sasaran.tahun(), sasaran.bulan()
                    );
                    return indikatorRenaksiIndividuRepository.save(indikator)
                            .flatMap(savedIndikator -> {
                                BigDecimal realisasiVal = req.realisasi() != null ? req.realisasi() : BigDecimal.ZERO;
                                BigDecimal paguVal = req.paguAnggaran() != null ? req.paguAnggaran() : BigDecimal.ZERO;
                                TargetIndikatorRenaksiIndividu target = TargetIndikatorRenaksiIndividu.of(
                                        savedIndikator.id(), req.kodeTarget(),
                                        sasaran.kodeOpd(), sasaran.nip(), sasaran.tahun(), sasaran.bulan(),
                                        paguVal, req.target(), realisasiVal, req.jenisRealisasi(),
                                        "", ""
                                );
                                return targetIndikatorRenaksiRepository.save(target);
                            });
                })
                .then(Mono.defer(() -> enrichWithDetails(sasaran)));
    }

    public static SasaranIndividu buildUncheckedSasaran(
            String kodeOpd,
            String nip,
            String kodeSasaran,
            String tahun,
            String bulan) {
        return SasaranIndividu.of(kodeOpd, nip, kodeSasaran, "Realisasi Sasaran " + kodeSasaran,
                tahun, bulan, RenaksiStatus.UNCHECKED);
    }

    private static SasaranIndividu buildUpdatedSasaran(SasaranIndividu existing, RenaksiIndividuRequest req) {
        return new SasaranIndividu(
                existing.id(),
                req.kodeOpd(),
                req.nip(),
                req.kodeSasaran(),
                existing.sasaran(),
                req.tahun(),
                req.bulan(),
                RenaksiStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version()
        );
    }

    // --- Renaksi ---

    public Flux<RenaksiIndividu> getRenaksiBySasaranId(Long sasaranId) {
        return renaksiIndividuRepository.findAllBySasaranId(sasaranId);
    }

    // --- Indikator ---

    public Flux<IndikatorRenaksiIndividu> getIndikatorByRenaksiId(Long renaksiId) {
        return indikatorRenaksiIndividuRepository.findAllByRenaksiId(renaksiId);
    }

    // --- Target ---

    public Flux<TargetIndikatorRenaksiIndividu> getTargetByIndikatorRenaksiId(Long indikatorRenaksiId) {
        return targetIndikatorRenaksiRepository.findAllByIndikatorRenaksiId(indikatorRenaksiId);
    }

    // --- Combined query (for GET responses) ---

    public Flux<SasaranWithDetails> getSasaranWithDetailsByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan)
                .flatMap(this::enrichWithDetails);
    }

    public Flux<SasaranWithDetails> getSasaranWithDetailsByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .flatMap(this::enrichWithDetails);
    }

    private Mono<SasaranWithDetails> enrichWithDetails(SasaranIndividu sasaran) {
        return renaksiIndividuRepository.findAllBySasaranId(sasaran.id())
                .collectList()
                .flatMap(renaksis -> {
                    if (renaksis.isEmpty()) {
                        return Mono.just(new SasaranWithDetails(sasaran, renaksis, Collections.emptyList(), Collections.emptyList()));
                    }
                    List<Long> renaksiIds = renaksis.stream().map(RenaksiIndividu::id).toList();
                    return indikatorRenaksiIndividuRepository.findAllByRenaksiIdIn(renaksiIds)
                            .collectList()
                            .flatMap(indikators -> {
                                if (indikators.isEmpty()) {
                                    return Mono.just(new SasaranWithDetails(sasaran, renaksis, indikators, Collections.emptyList()));
                                }
                                List<Long> indikatorIds = indikators.stream().map(IndikatorRenaksiIndividu::id).toList();
                                return targetIndikatorRenaksiRepository.findAllByIndikatorRenaksiIdIn(indikatorIds)
                                        .collectList()
                                        .map(targets -> new SasaranWithDetails(sasaran, renaksis, indikators, targets));
                            });
                });
    }
}
