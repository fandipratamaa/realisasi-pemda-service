package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.SasaranIndividuRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SasaranIndividuService {
    private final SasaranIndividuRepository sasaranIndividuRepository;

    public SasaranIndividuService(SasaranIndividuRepository sasaranIndividuRepository) {
        this.sasaranIndividuRepository = sasaranIndividuRepository;
    }

    public Flux<SasaranIndividu> getAllRealisasiSasaranIndividu() {
        return sasaranIndividuRepository.findAll();
    }

    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndNip(String tahun, String bulan, String nip) {
        return sasaranIndividuRepository.findAllByTahunAndBulanAndNip(tahun, bulan, nip);
    }

    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpd(String tahun, String bulan, String kodeOpd) {
        return sasaranIndividuRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpdAndNip(String tahun, String bulan, String kodeOpd, String nip) {
        return sasaranIndividuRepository.findAllByTahunAndBulanAndKodeOpdAndNip(tahun, bulan, kodeOpd, nip);
    }

    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndNipAndRenjaId(String tahun, String bulan, String nip, String renjaId) {
        return sasaranIndividuRepository.findAllByTahunAndBulanAndNipAndRenjaId(tahun, bulan, nip, renjaId);
    }

    public Mono<SasaranIndividu> submitRealisasiSasaranIndividu(String renjaId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String nip, String namaPegawai, String kodeOpd, String rumusPerhitungan, String sumberData) {
        return Mono.just(buildUncheckedRealisasiSasaranIndividu(renjaId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, nip, namaPegawai, kodeOpd, rumusPerhitungan, sumberData))
                .flatMap(sasaranIndividuRepository::save);
    }

    public Mono<Void> deleteRealisasiSasaranIndividuBySasaranId(String sasaranId) {
        return sasaranIndividuRepository.deleteByRenjaId(sasaranId);
    }

    public static SasaranIndividu buildUncheckedRealisasiSasaranIndividu(String renjaId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String nip, String namaPegawai, String kodeOpd, String rumusPerhitungan, String sumberData) {
        return SasaranIndividu.of(
                renjaId,
                "Realisasi Renja Individu " + renjaId,
                indikatorId,
                "Realisasi Indikator Individu " + indikatorId,
                targetId, target, realisasi, satuan, tahun,
                bulan, jenisRealisasi, nip, namaPegawai, kodeOpd, rumusPerhitungan, sumberData,
                SasaranIndividuStatus.UNCHECKED
        );
    }

    public Flux<SasaranIndividu> batchSubmitRealisasiSasaranIndividu(@Valid List<SasaranIndividuRequest> sasaranIndividuRequests) {
        return Flux.fromIterable(sasaranIndividuRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return sasaranIndividuRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    SasaranIndividu updated = new SasaranIndividu(
                                            existing.id(),
                                            existing.renjaId(),
                                            existing.renja(),
                                            existing.indikatorId(),
                                            existing.indikator(),
                                            existing.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi(),
                                            req.nip(),
                                            req.namaPegawai(),
                                            req.kodeOpd(),
                                            req.rumusPerhitungan(),
                                            req.sumberData(),
                                            SasaranIndividuStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy(),
                                            existing.version()
                                    );
                                    return sasaranIndividuRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    SasaranIndividu baru = buildUncheckedRealisasiSasaranIndividu(
                                            req.renjaId(),
                                            req.indikatorId(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.jenisRealisasi(),
                                            req.nip(),
                                            req.namaPegawai(),
                                            req.kodeOpd(),
                                            req.rumusPerhitungan(),
                                            req.sumberData()
                                    );
                                    return sasaranIndividuRepository.save(baru);
                                }));
                    }
                    else {
                        SasaranIndividu baru = buildUncheckedRealisasiSasaranIndividu(
                                req.renjaId(),
                                req.indikatorId(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.bulan(),
                                req.jenisRealisasi(),
                                req.nip(),
                                req.namaPegawai(),
                                req.kodeOpd(),
                                req.rumusPerhitungan(),
                                req.sumberData()
                        );
                        return sasaranIndividuRepository.save(baru);
                    }
                });
    }
}
