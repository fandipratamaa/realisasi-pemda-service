package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TujuanOpdService {
    private final TujuanOpdRepository tujuanOpdRepository;

    public TujuanOpdService(TujuanOpdRepository tujuanOpdRepository) {
        this.tujuanOpdRepository = tujuanOpdRepository;
    }

    public Flux<TujuanOpd> getAllRealisasiTujuanOpd() {
        return tujuanOpdRepository.findAll();
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndKodeOpd(String tahun, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunAndKodeOpd(tahun, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByKodeOpd(String kodeOpd) {
        return tujuanOpdRepository.findAllByKodeOpd(kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTujuanId(String tujuanId) {
        return tujuanOpdRepository.findAllByTujuanId(tujuanId);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByIndikatorId(String indikatorId) {
        return tujuanOpdRepository.findAllByIndikatorId(indikatorId);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByPeriodeRpjmd(String tahunAwal, String tahunAkhir, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunBetweenAndKodeOpd(tahunAwal, tahunAkhir, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndTujuanIdAndKodeOpd(String tahun, String tujuanId, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunAndTujuanIdAndKodeOpd(tahun, tujuanId, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan);
    }

    public Mono<TujuanOpd> getRealisasiTujuanOpdById(Long id) {
        return tujuanOpdRepository.findById(id);
    }

    public Mono<TujuanOpd> submitRealisasiTujuanOpd(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd) {
        return Mono.just(buildUncheckedRealisasiTujuanOpd(tujuanId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd))
                .flatMap(tujuanOpdRepository::save);
    }

    public static TujuanOpd buildUncheckedRealisasiTujuanOpd(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd) {
        return TujuanOpd.of(
                tujuanId,
                "Realisasi Tujuan Opd " + tujuanId,
                indikatorId,
                "Realisasi Indikator Opd " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan,
                jenisRealisasi, kodeOpd,
                TujuanOpdStatus.UNCHECKED
        );
    }

    public Flux<TujuanOpd> batchSubmitRealisasiTujuanOpd(@Valid List<TujuanOpdRequest> tujuanOpdRequests) {
        return Flux.fromIterable(tujuanOpdRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return tujuanOpdRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    TujuanOpd updated = new TujuanOpd(
                                            existing.id(),
                                            existing.tujuanId(),
                                            existing.tujuan(),
                                            existing.indikatorId(),
                                            existing.indikator(),
                                            existing.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi(),
                                            existing.kodeOpd(),
                                            TujuanOpdStatus.UNCHECKED,
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.version()
                                    );
                                    return tujuanOpdRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    TujuanOpd baru = buildUncheckedRealisasiTujuanOpd(
                                            req.tujuanId(),
                                            req.indikatorId(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi(),
                                            req.kodeOpd()
                                    );
                                    return tujuanOpdRepository.save(baru);
                                }));
                    }
                    else {
                        TujuanOpd baru = buildUncheckedRealisasiTujuanOpd(
                                req.tujuanId(),
                                req.indikatorId(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.bulan(),
                                req.jenisRealisasi(),
                                req.kodeOpd()
                        );
                        return tujuanOpdRepository.save(baru);
                    }
                });
    }
}
