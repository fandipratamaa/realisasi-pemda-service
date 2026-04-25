package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RenaksiService {
    private final RenaksiRepository renaksiRepository;

    public RenaksiService(RenaksiRepository renaksiRepository) {
        this.renaksiRepository = renaksiRepository;
    }

    public Flux<Renaksi> getAllRealisasiRenaksi() {
        return renaksiRepository.findAll();
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

    public Mono<Renaksi> submitRealisasiRenaksi(
            String renaksiId,
            String renaksi,
            String nip,
            String rekinId,
            String rekin,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String bulan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd) {
        return Mono.just(buildUncheckedRealisasiRenaksi(
                        renaksiId,
                        renaksi,
                        nip,
                        rekinId,
                        rekin,
                        targetId,
                        target,
                        realisasi,
                        satuan,
                        bulan,
                        tahun,
                        jenisRealisasi,
                        kodeOpd))
                .flatMap(renaksiRepository::save);
    }

    public Mono<Void> deleteRealisasiRenaksi(Long id) {
        return renaksiRepository.deleteById(id);
    }

    public static Renaksi buildUncheckedRealisasiRenaksi(
            String renaksiId,
            String renaksi,
            String nip,
            String rekinId,
            String rekin,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String bulan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd) {
        return Renaksi.of(
                renaksiId,
                renaksi,
                nip,
                rekinId,
                rekin,
                targetId,
                target,
                realisasi,
                satuan,
                bulan,
                tahun,
                jenisRealisasi,
                kodeOpd,
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
                                        req.rekinId(),
                                        req.rekin(),
                                        req.targetId(),
                                        req.target(),
                                        req.realisasi(),
                                        req.satuan(),
                                        req.bulan(),
                                        req.tahun(),
                                        req.jenisRealisasi(),
                                        req.kodeOpd()
                                ))));
                    }

                    return renaksiRepository.findFirstByNipAndBulanAndRekinIdAndRenaksiId(
                                    req.nip(),
                                    req.bulan(),
                                    req.rekinId(),
                                    req.renaksiId())
                            .flatMap(existing -> renaksiRepository.save(buildUpdatedRealisasiRenaksi(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renaksiRepository.save(buildUncheckedRealisasiRenaksi(
                                    req.renaksiId(),
                                    req.renaksi(),
                                    req.nip(),
                                    req.rekinId(),
                                    req.rekin(),
                                    req.targetId(),
                                    req.target(),
                                    req.realisasi(),
                                    req.satuan(),
                                    req.bulan(),
                                    req.tahun(),
                                    req.jenisRealisasi(),
                                    req.kodeOpd()
                            ))));
                });
    }

    private static Renaksi buildUpdatedRealisasiRenaksi(Renaksi existing, RenaksiRequest req) {
        return new Renaksi(
                existing.id(),
                existing.renaksiId(),
                existing.renaksi(),
                existing.nip(),
                existing.rekinId(),
                existing.rekin(),
                existing.targetId(),
                existing.target(),
                req.realisasi(),
                req.satuan(),
                req.bulan(),
                req.tahun(),
                req.jenisRealisasi(),
                req.kodeOpd(),
                RenaksiStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version());
    }
}
