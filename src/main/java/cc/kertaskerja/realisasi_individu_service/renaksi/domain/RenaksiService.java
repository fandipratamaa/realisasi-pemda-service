package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RenaksiService {
    private final RenaksiRepository renaksiRepository;

    public RenaksiService(RenaksiRepository renaksiRepository) {
        this.renaksiRepository = renaksiRepository;
    }

    public Mono<Renaksi> getRealisasiRenaksiByNipBulanRekin(String nip, String bulan, String rekinId, String renaksiId) {
        return renaksiRepository.findFirstByNipAndBulanAndRekinIdAndRenaksiId(nip, bulan, rekinId, renaksiId);
    }

    public Flux<Renaksi> getRealisasiRenaksiByNipAndBulan(String nip, String bulan) {
        return renaksiRepository.findAllByNipAndBulan(nip, bulan);
    }

    public Flux<Renaksi> getRealisasiRenaksiByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return renaksiRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    public Flux<Renaksi> getRealisasiRenaksiByKodeOpdAndBulan(String kodeOpd, String bulan) {
        return renaksiRepository.findAllByKodeOpdAndBulan(kodeOpd, bulan);
    }

    public Flux<Renaksi> getRealisasiRenaksiByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return renaksiRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Mono<Renaksi> submitRealisasiRenaksi(RenaksiRequest req) {
        if (req.targetRealisasiId() != null) {
            return renaksiRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> renaksiRepository.save(buildUpdatedRealisasiRenaksi(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Renaksi baru = buildUncheckedRealisasiRenaksi(
                                req.renaksiId(), req.renaksi(),
                                req.nip(), req.namaPegawai(),
                                req.rekinId(), req.rekin(),
                                req.targetId(), req.target(),
                                req.realisasi(), req.anggaran(), req.satuan(),
                                req.bulan(), req.tahun(),
                                req.jenisRealisasi(), req.kodeOpd());
                        return renaksiRepository.save(baru);
                    }));
        }

        return renaksiRepository.findFirstByNipAndTahunAndBulanAndRekinIdAndRenaksiIdAndTargetId(
                        req.nip(), req.tahun(), req.bulan(), req.rekinId(), req.renaksiId(), req.targetId())
                .flatMap(existing -> renaksiRepository.save(buildUpdatedRealisasiRenaksi(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Renaksi baru = buildUncheckedRealisasiRenaksi(
                            req.renaksiId(), req.renaksi(),
                            req.nip(), req.namaPegawai(),
                            req.rekinId(), req.rekin(),
                            req.targetId(), req.target(),
                            req.realisasi(), req.anggaran(), req.satuan(),
                            req.bulan(), req.tahun(),
                            req.jenisRealisasi(), req.kodeOpd());
                    return renaksiRepository.save(baru);
                }));
    }

    public static Renaksi buildUncheckedRealisasiRenaksi(
            String renaksiId,
            String renaksi,
            String nip,
            String namaPegawai,
            String rekinId,
            String rekin,
            String targetId,
            String target,
            Integer realisasi,
            BigDecimal anggaran,
            String satuan,
            String bulan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd) {
        return Renaksi.of(
                renaksiId,
                renaksi,
                nip,
                namaPegawai,
                rekinId,
                rekin,
                targetId,
                target,
                realisasi,
                anggaran,
                satuan,
                bulan,
                tahun,
                jenisRealisasi,
                kodeOpd,
                "",
                "",
                RenaksiStatus.UNCHECKED);
    }

    public Flux<Renaksi> batchSubmitRealisasiRenaksi(@Valid List<RenaksiRequest> renaksiRequests) {
        return Flux.fromIterable(renaksiRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renaksiRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> renaksiRepository.save(buildUpdatedRealisasiRenaksi(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> renaksiRepository.save(buildUncheckedRealisasiRenaksi(
                                        req.renaksiId(),
                                        req.renaksi(),
                                        req.nip(),
                                        req.namaPegawai(),
                                        req.rekinId(),
                                        req.rekin(),
                                        req.targetId(),
                                        req.target(),
                                        req.realisasi(),
                                        req.anggaran(),
                                        req.satuan(),
                                        req.bulan(),
                                        req.tahun(),
                                        req.jenisRealisasi(),
                                        req.kodeOpd()
                                ))));
                    }

                    return renaksiRepository.findFirstByNipAndTahunAndBulanAndRekinIdAndRenaksiIdAndTargetId(
                                    req.nip(),
                                    req.tahun(),
                                    req.bulan(),
                                    req.rekinId(),
                                    req.renaksiId(),
                                    req.targetId())
                            .flatMap(existing -> renaksiRepository.save(buildUpdatedRealisasiRenaksi(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renaksiRepository.save(buildUncheckedRealisasiRenaksi(
                                    req.renaksiId(),
                                    req.renaksi(),
                                    req.nip(),
                                    req.namaPegawai(),
                                    req.rekinId(),
                                    req.rekin(),
                                    req.targetId(),
                                    req.target(),
                                    req.realisasi(),
                                    req.anggaran(),
                                    req.satuan(),
                                    req.bulan(),
                                    req.tahun(),
                                    req.jenisRealisasi(),
                                    req.kodeOpd()
                            ))));
                });
    }

    public Mono<Renaksi> updateFaktorPenunjang(String nip, String tahun, String bulan, String rekinId, String renaksiId, String targetId, String faktorPenunjang) {
        return renaksiRepository
                .findFirstByNipAndTahunAndBulanAndRekinIdAndRenaksiIdAndTargetId(nip, tahun, bulan, rekinId, renaksiId, targetId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Renaksi tidak ditemukan")))
                .flatMap(existing -> {
                    Renaksi updated = new Renaksi(
                            existing.id(),
                            existing.renaksiId(),
                            existing.renaksi(),
                            existing.nip(),
                            existing.namaPegawai(),
                            existing.rekinId(),
                            existing.rekin(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.anggaran(),
                            existing.satuan(),
                            existing.bulan(),
                            existing.tahun(),
                            existing.jenisRealisasi(),
                            existing.kodeOpd(),
                            faktorPenunjang,
                            existing.faktorPenghambat(),
                            existing.status(),
                            existing.createdBy(),
                            existing.lastModifiedBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.version()
                    );
                    return renaksiRepository.save(updated);
                });
    }

    public Mono<Renaksi> updateFaktorPenghambat(String nip, String tahun, String bulan, String rekinId, String renaksiId, String targetId, String faktorPenghambat) {
        return renaksiRepository
                .findFirstByNipAndTahunAndBulanAndRekinIdAndRenaksiIdAndTargetId(nip, tahun, bulan, rekinId, renaksiId, targetId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Renaksi tidak ditemukan")))
                .flatMap(existing -> {
                    Renaksi updated = new Renaksi(
                            existing.id(),
                            existing.renaksiId(),
                            existing.renaksi(),
                            existing.nip(),
                            existing.namaPegawai(),
                            existing.rekinId(),
                            existing.rekin(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.anggaran(),
                            existing.satuan(),
                            existing.bulan(),
                            existing.tahun(),
                            existing.jenisRealisasi(),
                            existing.kodeOpd(),
                            existing.faktorPenunjang(),
                            faktorPenghambat,
                            existing.status(),
                            existing.createdBy(),
                            existing.lastModifiedBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.version()
                    );
                    return renaksiRepository.save(updated);
                });
    }

    private static Renaksi buildUpdatedRealisasiRenaksi(Renaksi existing, RenaksiRequest req) {
        return new Renaksi(
                existing.id(),
                existing.renaksiId(),
                existing.renaksi(),
                existing.nip(),
                req.namaPegawai(),
                existing.rekinId(),
                existing.rekin(),
                existing.targetId(),
                existing.target(),
                req.realisasi(),
                req.anggaran(),
                req.satuan(),
                req.bulan(),
                req.tahun(),
                req.jenisRealisasi(),
                req.kodeOpd(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                RenaksiStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version());
    }
}
