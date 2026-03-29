package cc.kertaskerja.realisasi_opd_service.renja_target.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja_target.web.RenjaTargetRequest;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RenjaTargetService {
    private final RenjaTargetRepository renjaTargetRepository;

    public RenjaTargetService(RenjaTargetRepository renjaTargetRepository) {
        this.renjaTargetRepository = renjaTargetRepository;
    }

    public Flux<RenjaTarget> getAllRealisasiRenjaTarget() {
        return renjaTargetRepository.findAll();
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByTahunAndKodeOpd(String tahun, String kodeOpd) {
        return renjaTargetRepository.findAllByTahunAndKodeOpd(tahun, kodeOpd);
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByKodeOpd(String kodeOpd) {
        return renjaTargetRepository.findAllByKodeOpd(kodeOpd);
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByRenjaId(String renjaTargetId) {
        return renjaTargetRepository.findAllByRenjaTargetId(renjaTargetId);
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByIndikatorId(String indikatorId) {
        return renjaTargetRepository.findAllByIndikatorId(indikatorId);
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByPeriodeRpjmd(String tahunAwal, String tahunAkhir,
            String kodeOpd) {
        return renjaTargetRepository.findAllByTahunBetweenAndKodeOpd(tahunAwal, tahunAkhir, kodeOpd);
    }

    public Flux<RenjaTarget> getRealisasiRenjaTargetByTahunAndRenjaTargetIdAndKodeOpd(String tahun,
            String renjaTargetId, String kodeOpd) {
        return renjaTargetRepository.findAllByTahunAndRenjaTargetIdAndKodeOpd(tahun, renjaTargetId, kodeOpd);
    }

    public Mono<RenjaTarget> getRealisasiRenjaTargetById(Long id) {
        return renjaTargetRepository.findById(id);
    }

    public Mono<RenjaTarget> submitRealisasiRenjaTarget(String renjaTargetId, String renjaTarget,
            JenisRenja jenisRenjaTarget,
            String indikatorId, String indikator,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, JenisRealisasi jenisRealisasi,
            String kodeOpd) {
        return Mono.just(buildUncheckedRealisasiRenjaTarget(
                renjaTargetId, renjaTarget, jenisRenjaTarget, indikatorId, indikator, targetId, target,
                realisasi, satuan, tahun, jenisRealisasi, kodeOpd))
                .flatMap(renjaTargetRepository::save);
    }

    public static RenjaTarget buildUncheckedRealisasiRenjaTarget(String renjaTargetId, String renjaTarget,
            JenisRenja jenisRenjaTarget,
            String indikatorId, String indikator,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, JenisRealisasi jenisRealisasi,
            String kodeOpd) {
        return RenjaTarget.of(
                renjaTargetId,
                renjaTarget,
                jenisRenjaTarget,
                indikatorId,
                indikator,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                jenisRealisasi,
                kodeOpd,
                RenjaTargetStatus.UNCHECKED);
    }

    public Flux<RenjaTarget> batchSubmitRealisasiRenjaTarget(@Valid List<RenjaTargetRequest> renjaTargetRequests) {
        return Flux.fromIterable(renjaTargetRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renjaTargetRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    RenjaTarget updated = new RenjaTarget(
                                            existing.id(),
                                            existing.renjaTargetId(),
                                            existing.renjaTarget(),
                                            existing.jenisRenjaTarget(),
                                            existing.indikatorId(),
                                            existing.indikator(),
                                            existing.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.jenisRealisasi(),
                                            existing.kodeOpd(),
                                            RenjaTargetStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy(),
                                            existing.version());
                                    return renjaTargetRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    RenjaTarget baru = buildUncheckedRealisasiRenjaTarget(
                                            req.renjaTargetId(),
                                            req.renjaTarget(),
                                            req.jenisRenjaTarget(),
                                            req.indikatorId(),
                                            req.indikator(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.jenisRealisasi(),
                                            req.kodeOpd());
                                    return renjaTargetRepository.save(baru);
                                }));
                    } else {
                        RenjaTarget baru = buildUncheckedRealisasiRenjaTarget(
                                req.renjaTargetId(),
                                req.renjaTarget(),
                                req.jenisRenjaTarget(),
                                req.indikatorId(),
                                req.indikator(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.jenisRealisasi(),
                                req.kodeOpd());
                        return renjaTargetRepository.save(baru);
                    }
                });
    }
}
