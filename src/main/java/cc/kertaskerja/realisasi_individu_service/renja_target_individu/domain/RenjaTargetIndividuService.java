package cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renja_target_individu.web.RenjaTargetIndividuRequest;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RenjaTargetIndividuService {
    private final RenjaTargetIndividuRepository renjaTargetIndividuRepository;

    public RenjaTargetIndividuService(RenjaTargetIndividuRepository renjaTargetIndividuRepository) {
        this.renjaTargetIndividuRepository = renjaTargetIndividuRepository;
    }

    public Flux<RenjaTargetIndividu> getAllRealisasiRenjaTargetIndividu() {
        return renjaTargetIndividuRepository.findAll();
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndNip(String tahun, String nip) {
        return renjaTargetIndividuRepository.findAllByTahunAndNip(tahun, nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByNip(String nip) {
        return renjaTargetIndividuRepository.findAllByNip(nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByRenjaId(String renjaId) {
        return renjaTargetIndividuRepository.findAllByRenjaId(renjaId);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByPeriodeRpjmd(String tahunAwal, String tahunAkhir, String nip) {
        return renjaTargetIndividuRepository.findAllByTahunBetweenAndNip(tahunAwal, tahunAkhir, nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndRenjaIdAndNip(String tahun, String renjaId, String nip) {
        return renjaTargetIndividuRepository.findAllByTahunAndRenjaIdAndNip(tahun, renjaId, nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndJenisRenjaAndKodeRenjaAndNip(
            String tahun,
            JenisRenja jenisRenja,
            String kodeRenja,
            String nip) {
        return renjaTargetIndividuRepository.findAllByTahunAndJenisRenjaAndKodeRenjaAndNip(tahun, jenisRenja, kodeRenja, nip);
    }

    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByJenisRenjaAndKodeRenjaAndNip(
            JenisRenja jenisRenja,
            String kodeRenja,
            String nip) {
        return renjaTargetIndividuRepository.findAllByJenisRenjaAndKodeRenjaAndNip(jenisRenja, kodeRenja, nip);
    }

    public Mono<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuById(Long id) {
        return renjaTargetIndividuRepository.findById(id);
    }

    public Mono<RenjaTargetIndividu> submitRealisasiRenjaTargetIndividu(
            String renjaId,
            String renja,
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiRenjaTargetIndividu(
                        renjaId,
                        renja,
                        kodeRenja,
                        jenisRenja,
                        nip,
                        idIndikator,
                        indikator,
                        targetId,
                        target,
                        realisasi,
                        satuan,
                        tahun,
                        jenisRealisasi))
                .flatMap(renjaTargetIndividuRepository::save);
    }

    public static RenjaTargetIndividu buildUncheckedRealisasiRenjaTargetIndividu(
            String renjaId,
            String renja,
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            JenisRealisasi jenisRealisasi) {
        String keteranganCapaian = buildKeteranganCapaian(target, realisasi, jenisRealisasi);
        return RenjaTargetIndividu.of(
                renjaId,
                renja,
                kodeRenja,
                jenisRenja,
                nip,
                idIndikator,
                indikator,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                jenisRealisasi,
                RenjaTargetIndividuStatus.UNCHECKED,
                keteranganCapaian
        );
    }

    public Flux<RenjaTargetIndividu> batchSubmitRealisasiRenjaTargetIndividu(@Valid List<RenjaTargetIndividuRequest> renjaTargetIndividuRequests) {
        return Flux.fromIterable(renjaTargetIndividuRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renjaTargetIndividuRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository.save(buildUncheckedRealisasiRenjaTargetIndividu(
                                        req.renjaId(),
                                        req.renja(),
                                        req.kodeRenja(),
                                        req.jenisRenja(),
                                        req.nip(),
                                        req.idIndikator(),
                                        req.indikator(),
                                        req.targetId(),
                                        req.target(),
                                        req.realisasi(),
                                        req.satuan(),
                                        req.tahun(),
                                        req.jenisRealisasi()
                                ))));
                    }

                    return renjaTargetIndividuRepository
                            .findFirstByNipAndTahunAndKodeRenjaAndJenisRenja(
                                    req.nip(),
                                    req.tahun(),
                                    req.kodeRenja(),
                                    req.jenisRenja())
                            .flatMap(existing -> renjaTargetIndividuRepository.save(buildUpdatedRealisasiRenjaTargetIndividu(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renjaTargetIndividuRepository.save(buildUncheckedRealisasiRenjaTargetIndividu(
                                    req.renjaId(),
                                    req.renja(),
                                    req.kodeRenja(),
                                    req.jenisRenja(),
                                    req.nip(),
                                    req.idIndikator(),
                                    req.indikator(),
                                    req.targetId(),
                                    req.target(),
                                    req.realisasi(),
                                    req.satuan(),
                                    req.tahun(),
                                    req.jenisRealisasi()
                            ))));
                });
    }

    private static RenjaTargetIndividu buildUpdatedRealisasiRenjaTargetIndividu(RenjaTargetIndividu existing, RenjaTargetIndividuRequest req) {
        String keteranganCapaian = buildKeteranganCapaian(req.target(), req.realisasi(), req.jenisRealisasi());
        return new RenjaTargetIndividu(
                existing.id(),
                existing.renjaId(),
                existing.renja(),
                existing.kodeRenja(),
                existing.jenisRenja(),
                existing.nip(),
                existing.idIndikator(),
                existing.indikator(),
                existing.targetId(),
                req.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.jenisRealisasi(),
                RenjaTargetIndividuStatus.UNCHECKED,
                keteranganCapaian,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate()
        );
    }

    private static String buildKeteranganCapaian(String target, Integer realisasi, JenisRealisasi jenisRealisasi) {
        if (realisasi == null || target == null || target.isBlank() || jenisRealisasi == null) {
            return null;
        }

        double capaian = new cc.kertaskerja.capaian.domain.Capaian(realisasi.doubleValue(), target, jenisRealisasi).hasilCapaian();
        return capaian > 100 ? "Peringatan: nilai capaian melebihi 100 %" : null;
    }
}
