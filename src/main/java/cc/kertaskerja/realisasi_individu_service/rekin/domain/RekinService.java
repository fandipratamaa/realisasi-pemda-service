package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RekinService {
    private final RekinRepository rekinRepository;

    public RekinService(RekinRepository rekinRepository) {
        this.rekinRepository = rekinRepository;
    }

    public Flux<Rekin> getAllRealisasiRekin() {
        return rekinRepository.findAll();
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

    public Flux<Rekin> getRealisasiRekinByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan) {
        return rekinRepository.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    public Mono<Rekin> getRealisasiRekinByNipIdSasaranTahunRekinId(String nip, String idSasaran, String tahun, String rekinId) {
        return rekinRepository.findFirstByNipAndIdSasaranAndTahunAndRekinId(nip, idSasaran, tahun, rekinId);
    }

    public Flux<Rekin> getRealisasiRekinByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return rekinRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Mono<Rekin> submitRealisasiRekin(String rekinId, String rekin,
            String indikatorId, String indikator,
            String nip, String idSasaran, String sasaran,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, String bulan, String kodeOpd, JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiRekin(
                rekinId, rekin, indikatorId, indikator, nip, idSasaran, sasaran, targetId, target,
                realisasi, satuan, tahun, bulan, kodeOpd, jenisRealisasi))
                .flatMap(rekinRepository::save);
    }

    public Mono<Void> deleteRealisasiRekin(Long id) {
        return rekinRepository.deleteById(id);
    }

public static Rekin buildUncheckedRealisasiRekin(String rekinId, String rekin,
            String indikatorId, String indikator,
            String nip, String idSasaran, String sasaran,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, String bulan, String kodeOpd, JenisRealisasi jenisRealisasi) {
        return Rekin.of(
                rekinId,
                rekin,
                indikatorId,
                indikator,
                nip,
                idSasaran,
                sasaran,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                kodeOpd,
                jenisRealisasi,
                RekinStatus.UNCHECKED);
    }

    public Flux<Rekin> batchSubmitRealisasiRekin(@Valid List<RekinRequest> rekinRequests) {
        return Flux.fromIterable(rekinRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return rekinRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> {
                                    Rekin baru = buildUncheckedRealisasiRekin(
                                            req.rekinId(),
                                            req.rekin(),
                                            req.indikatorId(),
                                            req.indikator(),
                                            req.nip(),
                                            req.idSasaran(),
                                            req.sasaran(),
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
                    }

                    return rekinRepository.findFirstByNipAndIdSasaranAndTahunAndRekinId(
                                    req.nip(),
                                    req.idSasaran(),
                                    req.tahun(),
                                    req.rekinId())
                            .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> {
                                Rekin baru = buildUncheckedRealisasiRekin(
                                        req.rekinId(),
                                        req.rekin(),
                                        req.indikatorId(),
                                        req.indikator(),
                                        req.nip(),
                                        req.idSasaran(),
                                        req.sasaran(),
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
                existing.idSasaran(),
                existing.sasaran(),
                existing.targetId(),
                existing.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                req.kodeOpd(),
                req.jenisRealisasi(),
                RekinStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version());
    }
}
