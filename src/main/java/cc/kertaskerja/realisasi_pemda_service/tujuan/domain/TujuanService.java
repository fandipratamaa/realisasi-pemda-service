package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TujuanService {
    private final TujuanRepository tujuanRepository;

    public TujuanService(TujuanRepository tujuanRepository) {
        this.tujuanRepository = tujuanRepository;
    }

    public Flux<Tujuan> getAllRealisasiTujuan() {
        return tujuanRepository.findAll();
    }

    public Flux<Tujuan> getRealisasiTujuanByTahun(String tahun) {
        return tujuanRepository.findAllByTahun(tahun);
    }

    public Flux<Tujuan> getRealisasiTujuanByTahunAndTujuanId(String tahun, String tujuanId) {
        return tujuanRepository.findAllByTahunAndTujuanId(tahun, tujuanId);
    }

    public Flux<Tujuan> getRealisasiTujuanByTujuanId(String tujuanId) {
        return tujuanRepository.findAllByTujuanId(tujuanId);
    }

    public Mono<Tujuan> getRealisasiTujuanById(Long id) {
        return tujuanRepository.findById(id);
    }

public Mono<Tujuan> submitRealisasiTujuan(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiTujuan(tujuanId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi))
                .flatMap(tujuanRepository::save);
    }

    public Flux<Tujuan> batchSubmitRealisasiTujuan(List<TujuanRequest> tujuans) {
        return Flux.fromIterable(tujuans)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return tujuanRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    Tujuan updated = new Tujuan(
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
                                            TujuanStatus.UNCHECKED,
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.version()
                                    );
                                    return tujuanRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    Tujuan baru = buildUncheckedRealisasiTujuan(
                                            req.tujuanId(),
                                            req.indikatorId(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi()
                                    );
                                    return tujuanRepository.save(baru);
                                }));
                    }
                    else {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.tujuanId(),
                                req.indikatorId(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.bulan(),
                                req.jenisRealisasi()
                        );
                        return tujuanRepository.save(baru);
                    }
                }
        );
    }

    public Flux<Tujuan> getRealisasiTujuanByIndikatorId(String indikatorId) {
        return tujuanRepository.findAllByIndikatorId(indikatorId);
    }

    public static Tujuan buildUncheckedRealisasiTujuan(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi) {
        return Tujuan.of(tujuanId,
                "Realisasi Tujuan " + tujuanId,
                indikatorId,
                "Realisasi Indikator " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi,
                TujuanStatus.UNCHECKED);
    }

    public Flux<Tujuan> getRealisasiTujuanByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return tujuanRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Flux<Tujuan> getRealisasiTujuanByTahunAndBulan(String tahun, String bulan) {
        return tujuanRepository.findAllByTahunAndBulan(tahun, bulan);
    }
}
