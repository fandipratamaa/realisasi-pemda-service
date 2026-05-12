package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.web.RenjaPaguIndividuRequest;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RenjaPaguIndividuService {
    private final RenjaPaguIndividuRepository renjaPaguIndividuRepository;

    public RenjaPaguIndividuService(RenjaPaguIndividuRepository renjaPaguIndividuRepository) {
        this.renjaPaguIndividuRepository = renjaPaguIndividuRepository;
    }

    public Flux<RenjaPaguIndividu> getAllRealisasiRenjaPaguIndividu() {
        return renjaPaguIndividuRepository.findAll();
    }

    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByNipAndTahun(String nip, String tahun) {
        return renjaPaguIndividuRepository.findAllByNipAndTahun(nip, tahun);
    }

    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan(String nip, String tahun, String bulan) {
        return renjaPaguIndividuRepository.findAllByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByFilters(
            String tahun, String nip, JenisRenja jenisRenja, String kodeRenja) {
        return renjaPaguIndividuRepository.findAllByTahunAndNipAndJenisRenjaAndKodeRenja(
                tahun, nip, jenisRenja, kodeRenja);
    }

    public Mono<Void> deleteRealisasiRenjaPaguIndividuByFilters(String tahun, String nip, JenisRenja jenisRenja, String kodeRenja) {
        return renjaPaguIndividuRepository.deleteByTahunAndNipAndJenisRenjaAndKodeRenja(tahun, nip, jenisRenja, kodeRenja);
    }

public Mono<RenjaPaguIndividu> submitRealisasiRenjaPaguIndividu(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            Integer pagu,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUncheckedRealisasiRenjaPaguIndividu(
                        kodeRenja,
                        jenisRenja,
                        nip,
                        idIndikator,
                        indikator,
                        pagu,
                        realisasi,
                        satuan,
                        tahun,
                        bulan,
                        jenisRealisasi))
                .flatMap(renjaPaguIndividuRepository::save);
    }

    public static RenjaPaguIndividu buildUncheckedRealisasiRenjaPaguIndividu(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            Integer pagu,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi) {
        return RenjaPaguIndividu.of(
                kodeRenja,
                jenisRenja,
                nip,
                idIndikator,
                indikator,
                pagu,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
                RenjaPaguIndividuStatus.UNCHECKED
        );
    }

    public Flux<RenjaPaguIndividu> batchSubmitRealisasiRenjaPaguIndividu(@Valid List<RenjaPaguIndividuRequest> renjaPaguIndividuRequests) {
        return Flux.fromIterable(renjaPaguIndividuRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renjaPaguIndividuRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> renjaPaguIndividuRepository.save(buildUpdatedRealisasiRenjaPaguIndividu(existing, req)))
                                .switchIfEmpty(Mono.defer(() -> renjaPaguIndividuRepository.save(buildUncheckedRealisasiRenjaPaguIndividu(
                                        req.kodeRenja(),
                                        req.jenisRenja(),
                                        req.nip(),
                                        req.idIndikator(),
                                        req.indikator(),
                                        req.pagu(),
                                        req.realisasi(),
                                        req.satuan(),
                                        req.tahun(),
                                        req.bulan(),
                                        req.jenisRealisasi()
                                ))));
                    }

                    return renjaPaguIndividuRepository
                            .findFirstByNipAndTahunAndJenisRenjaAndKodeRenja(
                                    req.nip(),
                                    req.tahun(),
                                    req.jenisRenja(),
                                    req.kodeRenja())
                            .flatMap(existing -> renjaPaguIndividuRepository.save(buildUpdatedRealisasiRenjaPaguIndividu(existing, req)))
                            .switchIfEmpty(Mono.defer(() -> renjaPaguIndividuRepository.save(buildUncheckedRealisasiRenjaPaguIndividu(
                                    req.kodeRenja(),
                                    req.jenisRenja(),
                                    req.nip(),
                                    req.idIndikator(),
                                    req.indikator(),
                                    req.pagu(),
                                    req.realisasi(),
                                    req.satuan(),
                                    req.tahun(),
                                    req.bulan(),
                                    req.jenisRealisasi()
                            ))));
                });
    }

    private static RenjaPaguIndividu buildUpdatedRealisasiRenjaPaguIndividu(RenjaPaguIndividu existing, RenjaPaguIndividuRequest req) {
        return new RenjaPaguIndividu(
                existing.id(),
                existing.kodeRenja(),
                existing.jenisRenja(),
                existing.nip(),
                existing.idIndikator(),
                existing.indikator(),
                req.pagu(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                req.jenisRealisasi(),
                RenjaPaguIndividuStatus.UNCHECKED,
                existing.createdBy(),
                existing.lastModifiedBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.version()
        );
    }

}
