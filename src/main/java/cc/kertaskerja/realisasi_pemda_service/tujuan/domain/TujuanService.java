package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

    public Mono<Tujuan> submitRealisasiTujuan(TujuanRequest req) {
        if (req.targetRealisasiId() != null) {
            return tujuanRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.tujuanId(), req.indikatorId(), req.targetId(),
                                req.target(), req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(), req.visiMisi(),
                                req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi());
                        return tujuanRepository.save(baru);
                    }));
        }
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                        req.tujuanId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Tujuan baru = buildUncheckedRealisasiTujuan(
                            req.tujuanId(), req.indikatorId(), req.targetId(),
                            req.target(), req.realisasi(), req.satuan(),
                            req.tahun(), req.bulan(), req.visiMisi(),
                            req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi());
                    return tujuanRepository.save(baru);
                }));
    }

    private static Tujuan buildUpdatedRealisasiTujuan(Tujuan existing, TujuanRequest req) {
        return new Tujuan(
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
                req.visiMisi(),
                req.rumusPerhitungan(),
                req.sumberData(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                TujuanStatus.UNCHECKED,
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy(),
                existing.version()
        );
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
                                            req.visiMisi(),
                                            req.rumusPerhitungan(),
                                            req.sumberData(),
                                            existing.faktorPenunjang(),
                                            existing.faktorPenghambat(),
                                            req.jenisRealisasi(),
                                            TujuanStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy(),
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
                                            req.visiMisi(),
                                            req.rumusPerhitungan(),
                                        req.sumberData(),
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
                                req.visiMisi(),
                                req.rumusPerhitungan(),
                                req.sumberData(),
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

    public static Tujuan buildUncheckedRealisasiTujuan(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, String visiMisi, String rumusPerhitungan, String sumberData, JenisRealisasi jenisRealisasi) {
        return Tujuan.of(tujuanId,
                "Realisasi Tujuan " + tujuanId,
                indikatorId,
                "Realisasi Indikator " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan, visiMisi, rumusPerhitungan, sumberData,
                "",
                "",
                jenisRealisasi,
                TujuanStatus.UNCHECKED);
    }

    public Flux<Tujuan> getRealisasiTujuanByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return tujuanRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Flux<Tujuan> getRealisasiTujuanByTahunAndBulan(String tahun, String bulan) {
        return tujuanRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Mono<Tujuan> updateFaktorPenunjang(String tujuanId, String indikatorId, String targetId, String tahun, String bulan, String faktorPenunjang) {
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(tujuanId, indikatorId, targetId, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.tujuanId(),
                            existing.tujuan(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.visiMisi(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            faktorPenunjang,
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy(),
                            existing.version()
                    );
                    return tujuanRepository.save(updated);
                });
    }

    public Mono<Tujuan> updateFaktorPenghambat(String tujuanId, String indikatorId, String targetId, String tahun, String bulan, String faktorPenghambat) {
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(tujuanId, indikatorId, targetId, tahun, bulan)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.tujuanId(),
                            existing.tujuan(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.visiMisi(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            existing.faktorPenunjang(),
                            faktorPenghambat,
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy(),
                            existing.version()
                    );
                    return tujuanRepository.save(updated);
                });
    }
}
