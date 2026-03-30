package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.capaian.domain.Capaian;
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

    public Mono<Rekin> getRealisasiRekinById(Long id) {
        return rekinRepository.findById(id);
    }

    public Flux<Rekin> getRealisasiRekinByRekinId(String rekinId) {
        return rekinRepository.findAllByRekinId(rekinId);
    }

    public Flux<Rekin> getRealisasiRekinByTahun(String tahun) {
        return rekinRepository.findAllByTahun(tahun);
    }

    public Flux<Rekin> getRealisasiRekinByIdSasaran(String idSasaran) {
        return rekinRepository.findAllByIdSasaran(idSasaran);
    }

    public Flux<Rekin> getRealisasiRekinByNip(String nip) {
        return rekinRepository.findAllByNip(nip);
    }

    public Flux<Rekin> getRealisasiRekinByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return rekinRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Mono<Rekin> submitRealisasiRekin(String rekinId, String rekin,
            String nip, String idSasaran, String sasaran,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiRekin(
                rekinId, rekin, nip, idSasaran, sasaran, targetId, target,
                realisasi, satuan, tahun, jenisRealisasi))
                .flatMap(rekinRepository::save);
    }

    public Mono<Void> deleteRealisasiRekin(Long id) {
        return rekinRepository.deleteById(id);
    }

    public static Rekin buildUncheckedRealisasiRekin(String rekinId, String rekin,
            String nip, String idSasaran, String sasaran,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, JenisRealisasi jenisRealisasi) {
        String keteranganCapaian = buildKeteranganCapaian(realisasi, target, jenisRealisasi);
        return Rekin.of(
                rekinId,
                rekin,
                nip,
                idSasaran,
                sasaran,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                jenisRealisasi,
                RekinStatus.UNCHECKED,
                keteranganCapaian);
    }

    private static String buildKeteranganCapaian(Integer realisasi, String target, JenisRealisasi jenisRealisasi) {
        if (realisasi == null) {
            return null;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian() > 100 ? "Peringatan: nilai capaian melebihi 100 %" : null;
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
                                            req.nip(),
                                            req.idSasaran(),
                                            req.sasaran(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.jenisRealisasi());
                                    return rekinRepository.save(baru);
                                }));
                    }

                    return rekinRepository.findFirstByNipAndIdSasaranAndTahun(
                                    req.nip(),
                                    req.idSasaran(),
                                    req.tahun())
                            .flatMap(existing -> rekinRepository.save(buildUpdatedRealisasiRekin(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> {
                                Rekin baru = buildUncheckedRealisasiRekin(
                                        req.rekinId(),
                                        req.rekin(),
                                        req.nip(),
                                        req.idSasaran(),
                                        req.sasaran(),
                                        req.targetId(),
                                        req.target(),
                                        req.realisasi(),
                                        req.satuan(),
                                        req.tahun(),
                                        req.jenisRealisasi());
                                return rekinRepository.save(baru);
                            }));
                });
    }

    private static Rekin buildUpdatedRealisasiRekin(Rekin existing, RekinRequest req) {
        String keteranganCapaian = buildKeteranganCapaian(req.realisasi(), existing.target(), req.jenisRealisasi());
        return new Rekin(
                existing.id(),
                existing.rekinId(),
                existing.rekin(),
                existing.nip(),
                existing.idSasaran(),
                existing.sasaran(),
                existing.targetId(),
                existing.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.jenisRealisasi(),
                RekinStatus.UNCHECKED,
                keteranganCapaian,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version());
    }
}
