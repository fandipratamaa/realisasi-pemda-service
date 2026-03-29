package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.web.RenjaPaguRequest;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class RenjaPaguService {
    private final RenjaPaguRepository renjaPaguRepository;

    public RenjaPaguService(RenjaPaguRepository renjaPaguRepository) {
        this.renjaPaguRepository = renjaPaguRepository;
    }

    public Flux<RenjaPagu> getAllRealisasiRenjaPagu() {
        return renjaPaguRepository.findAll();
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByTahunAndKodeOpd(String tahun, String kodeOpd) {
        return renjaPaguRepository.findAllByTahunAndKodeOpd(tahun, kodeOpd);
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByKodeOpd(String kodeOpd) {
        return renjaPaguRepository.findAllByKodeOpd(kodeOpd);
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByRenjaPaguId(String renjaPaguId) {
        return renjaPaguRepository.findAllByRenjaPaguId(renjaPaguId);
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByPeriodeRpjmd(String tahunAwal, String tahunAkhir, String kodeOpd) {
        return renjaPaguRepository.findAllByTahunBetweenAndKodeOpd(tahunAwal, tahunAkhir, kodeOpd);
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByTahunAndRenjaPaguIdAndKodeOpd(String tahun, String renjaPaguId, String kodeOpd) {
        return renjaPaguRepository.findAllByTahunAndRenjaPaguIdAndKodeOpd(tahun, renjaPaguId, kodeOpd);
    }

    public Mono<RenjaPagu> getRealisasiRenjaPaguById(Long id) {
        return renjaPaguRepository.findById(id);
    }

    public Mono<RenjaPagu> submitRealisasiRenjaPagu(String renjaPaguId, String renjaPagu, JenisRenja jenisRenja, Integer pagu, Integer realisasi, String satuan, String tahun, JenisRealisasi jenisRealisasi, String kodeOpd) {
        return Mono.just(buildUncheckedRealisasiRenjaPagu(renjaPaguId, renjaPagu, jenisRenja, pagu, realisasi, satuan, tahun, jenisRealisasi, kodeOpd))
                .flatMap(renjaPaguRepository::save);
    }

    public static RenjaPagu buildUncheckedRealisasiRenjaPagu(String renjaPaguId, String renjaPagu, JenisRenja jenisRenjaPagu, Integer pagu, Integer realisasi, String satuan, String tahun, JenisRealisasi jenisRealisasi, String kodeOpd) {
        return RenjaPagu.of(
                renjaPaguId,
                "Renja Pagu " + renjaPaguId,
                jenisRenjaPagu, pagu, realisasi, satuan, tahun,
                jenisRealisasi, kodeOpd,
                RenjaPaguStatus.UNCHECKED
        );
    }

    public Flux<RenjaPagu> batchSubmitRealisasiRenjaPagu(@Valid List<RenjaPaguRequest> renjaPaguRequests) {
        return Flux.fromIterable(renjaPaguRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renjaPaguRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    RenjaPagu updated = new RenjaPagu(
                                            existing.id(),
                                            existing.renjaPaguId(),
                                            existing.renjaPagu(),
                                            existing.jenisRenjaPagu(),
                                            req.pagu(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.jenisRealisasi(),
                                            existing.kodeOpd(),
                                            RenjaPaguStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy(),
                                            existing.version()
                                    );
                                    return renjaPaguRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    RenjaPagu baru = buildUncheckedRealisasiRenjaPagu(
                                            req.renjaPaguId(),
                                            req.renjaPagu(),
                                            req.jenisRenjaPagu(),
                                            req.pagu(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.jenisRealisasi(),
                                            req.kodeOpd()
                                    );
                                    return renjaPaguRepository.save(baru);
                                }));
                    }
                    else {
                        RenjaPagu baru = buildUncheckedRealisasiRenjaPagu(
                                req.renjaPaguId(),
                                req.renjaPagu(),
                                req.jenisRenjaPagu(),
                                req.pagu(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.jenisRealisasi(),
                                req.kodeOpd()
                        );
                        return renjaPaguRepository.save(baru);
                    }
                });
    }
}
