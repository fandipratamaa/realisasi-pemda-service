package cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renja_target_individu.web.RenjaTargetIndividuRequest;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RenjaTargetIndividuService {
    private final RenjaTargetIndividuRepository renjaTargetIndividuRepository;

    public RenjaTargetIndividuService(RenjaTargetIndividuRepository renjaTargetIndividuRepository) {
        this.renjaTargetIndividuRepository = renjaTargetIndividuRepository;
    }

    public Flux<RenjaTargetIndividu> getAllRealisasiRenjaTargetIndividu() {
        return renjaTargetIndividuRepository.findAll();
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndNip(String tahun, String nip) {
        return renjaTargetIndividuRepository.findAllByTahunAndNip(tahun, nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunNipAndBulan(String tahun, String nip, String bulan) {
        return renjaTargetIndividuRepository.findAllByTahunAndNipAndBulan(tahun, nip, bulan);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndBulanAndKodeOpd(String tahun, String bulan, String kodeOpd) {
        return renjaTargetIndividuRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndNipAndBulanAndKodeOpd(String tahun, String nip, String bulan, String kodeOpd) {
        return renjaTargetIndividuRepository.findAllByTahunAndNipAndBulanAndKodeOpd(tahun, nip, bulan, kodeOpd);
    }

    public Mono<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunNipJenisRenjaKodeRenja(String tahun, String nip, JenisRenja jenisRenja, String kodeRenja) {
        return renjaTargetIndividuRepository.findFirstByNipAndTahunAndKodeRenjaAndJenisRenja(nip, tahun, kodeRenja, jenisRenja);
    }

    public Mono<Void> deleteRealisasiRenjaTargetIndividuByFilters(String tahun, String nip, JenisRenja jenisRenja, String kodeRenja) {
        return renjaTargetIndividuRepository.deleteByTahunAndNipAndJenisRenjaAndKodeRenja(tahun, nip, jenisRenja, kodeRenja);
    }

    public Mono<RenjaTargetIndividu> submitRealisasiRenjaTargetIndividu(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String namaPegawai,
            String kodeOpd,
            String idIndikator,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiRenjaTargetIndividu(
                        kodeRenja,
                        jenisRenja,
                        nip,
                        namaPegawai,
                        kodeOpd,
                        idIndikator,
                        indikator,
                        targetId,
                        target,
                        realisasi,
                        satuan,
                        tahun,
                        bulan,
                        jenisRealisasi))
                .flatMap(renjaTargetIndividuRepository::save);
    }

public static RenjaTargetIndividu buildUncheckedRealisasiRenjaTargetIndividu(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String namaPegawai,
            String kodeOpd,
            String idIndikator,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi) {
        return RenjaTargetIndividu.of(
                kodeRenja,
                jenisRenja,
                nip,
                namaPegawai,
                kodeOpd,
                idIndikator,
                indikator,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
                RenjaTargetIndividuStatus.UNCHECKED
        );
    }

    public Flux<RenjaTargetIndividu> batchSubmitRealisasiRenjaTargetIndividu(@Valid List<RenjaTargetIndividuRequest> renjaTargetIndividuRequests) {
        return Flux.fromIterable(renjaTargetIndividuRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renjaTargetIndividuRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository
                                        .findFirstByTargetId(req.targetId())
                                        .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                                        .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository.save(buildUncheckedRealisasiRenjaTargetIndividu(
                                                req.kodeRenja(),
                                                req.jenisRenja(),
                                                req.nip(),
                                                req.namaPegawai(),
                                                req.kodeOpd(),
                                                req.idIndikator(),
                                                req.indikator(),
                                                req.targetId(),
                                                req.target(),
                                                req.realisasi(),
                                                req.satuan(),
                                                req.tahun(),
                                                req.bulan(),
                                                req.jenisRealisasi()
                                        ))))));
                    }

                    return renjaTargetIndividuRepository
                            .findFirstByNipAndTahunAndKodeRenjaAndJenisRenja(
                                    req.nip(),
                                    req.tahun(),
                                    req.kodeRenja(),
                                    req.jenisRenja())
                            .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository
                                    .findFirstByTargetId(req.targetId())
                                    .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                                    .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository.save(buildUncheckedRealisasiRenjaTargetIndividu(
                                            req.kodeRenja(),
                                            req.jenisRenja(),
                                            req.nip(),
                                            req.namaPegawai(),
                                            req.kodeOpd(),
                                            req.idIndikator(),
                                            req.indikator(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi()
                                    ))))));
                });
    }

    private static RenjaTargetIndividu buildUpdatedRealisasiRenjaTargetIndividu(RenjaTargetIndividu existing, RenjaTargetIndividuRequest req) {
        return new RenjaTargetIndividu(
                existing.id(),
                existing.kodeRenja(),
                existing.jenisRenja(),
                existing.nip(),
                req.namaPegawai(),
                req.kodeOpd(),
                existing.idIndikator(),
                existing.indikator(),
                existing.targetId(),
                req.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan() != null ? req.bulan() : existing.bulan(),
                req.jenisRealisasi(),
                RenjaTargetIndividuStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version()
        );
    }
}
