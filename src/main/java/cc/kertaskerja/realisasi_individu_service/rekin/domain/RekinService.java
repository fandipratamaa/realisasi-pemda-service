package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RekinService {
    private final RekinRepository rekinRepository;

    public RekinService(RekinRepository rekinRepository) {
        this.rekinRepository = rekinRepository;
    }

    public Flux<Rekin> getRealisasiRekinByNipAndTahun(String nip, String tahun) {
        return rekinRepository.findAllByNipAndTahun(nip, tahun);
    }

    public Flux<Rekin> getRealisasiRekinByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return rekinRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    public Flux<Rekin> getRealisasiRekinByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return rekinRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Flux<Rekin> getRealisasiRekinByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return rekinRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Mono<Rekin> submitRealisasiRekin(RekinRequest req) {
        if (req.targetRealisasiId() != null) {
            return rekinRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> rekinRepository
                            .findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(
                                    req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.targetId())
                            .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> {
                                Rekin baru = buildUncheckedRealisasiRekin(
                                        req.rekinId(), req.indikatorId(),
                                        req.nip(), req.namaPegawai(),
                                        req.targetId(), req.target(), req.realisasi(), req.satuan(),
                                        req.tahun(), req.bulan(), req.kodeOpd(), req.jenisRealisasi());
                                return rekinRepository.save(baru);
                            }))));
        }

        return rekinRepository.findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(
                        req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.targetId())
                .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Rekin baru = buildUncheckedRealisasiRekin(
                            req.rekinId(), req.indikatorId(),
                            req.nip(), req.namaPegawai(),
                            req.targetId(), req.target(), req.realisasi(), req.satuan(),
                            req.tahun(), req.bulan(), req.kodeOpd(), req.jenisRealisasi());
                    return rekinRepository.save(baru);
                }));
    }

    public static Rekin buildUncheckedRealisasiRekin(String rekinId,
            String indikatorId,
            String nip, String namaPegawai,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, String bulan, String kodeOpd, JenisRealisasi jenisRealisasi) {
        return Rekin.of(
                rekinId,
                "",
                indikatorId,
                "",
                nip,
                namaPegawai,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                kodeOpd,
                "",
                "",
                jenisRealisasi,
                RekinStatus.UNCHECKED);
    }

    public Flux<Rekin> batchSubmitRealisasiRekin(@Valid List<RekinRequest> rekinRequests) {
        return Flux.fromIterable(rekinRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return rekinRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> rekinRepository
                                        .findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(
                                                req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.targetId())
                                        .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                                        .switchIfEmpty(Mono.defer(() -> {
                                            Rekin baru = buildUncheckedRealisasiRekin(
                                                    req.rekinId(),
                                                    req.indikatorId(),
                                                    req.nip(),
                                                    req.namaPegawai(),
                                                    req.targetId(),
                                                    req.target(),
                                                    req.realisasi(),
                                                    req.satuan(),
                                                    req.tahun(),
                                                    req.bulan(),
                                                    req.kodeOpd(),
                                                    req.jenisRealisasi());
                                            return rekinRepository.save(baru);
                                        }))));
                    }

                    return rekinRepository.findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(
                                    req.nip(),
                                    req.tahun(),
                                    req.bulan(),
                                    req.rekinId(),
                                    req.targetId())
                            .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> {
                                Rekin baru = buildUncheckedRealisasiRekin(
                                        req.rekinId(),
                                        req.indikatorId(),
                                        req.nip(),
                                        req.namaPegawai(),
                                        req.targetId(),
                                        req.target(),
                                        req.realisasi(),
                                        req.satuan(),
                                        req.tahun(),
                                        req.bulan(),
                                        req.kodeOpd(),
                                        req.jenisRealisasi());
                                return rekinRepository.save(baru);
                            }));
                });
    }

    private static Rekin buildUpdatedRealisasiRekin(Rekin existing, RekinRequest req) {
        return new Rekin(
                existing.id(),
                existing.rekinId(),
                existing.rekin(),
                existing.indikatorId(),
                existing.indikator(),
                existing.nip(),
                req.namaPegawai(),
                existing.targetId(),
                existing.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                req.kodeOpd(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                RekinStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version());
    }

    public Mono<Rekin> updateFaktorPenunjang(FaktorPenunjangRekinRequest req) {
        return rekinRepository
                .findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.targetId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(existing -> {
                    Rekin updated = new Rekin(
                            existing.id(),
                            existing.rekinId(),
                            existing.rekin(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.nip(),
                            existing.namaPegawai(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.kodeOpd(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            RekinStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.lastModifiedBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.version()
                    );
                    return rekinRepository.save(updated);
                });
    }

    public Mono<Rekin> updateFaktorPenghambat(FaktorPenghambatRekinRequest req) {
        return rekinRepository
                .findFirstByNipAndTahunAndBulanAndRekinIdAndTargetId(req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.targetId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")))
                .flatMap(existing -> {
                    Rekin updated = new Rekin(
                            existing.id(),
                            existing.rekinId(),
                            existing.rekin(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.nip(),
                            existing.namaPegawai(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.kodeOpd(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            RekinStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.lastModifiedBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.version()
                    );
                    return rekinRepository.save(updated);
                });
    }
}
