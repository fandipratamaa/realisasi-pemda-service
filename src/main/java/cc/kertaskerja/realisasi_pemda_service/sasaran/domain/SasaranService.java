package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SasaranService {
    private final SasaranRepository sasaranRepository;

    public SasaranService(SasaranRepository sasaranRepository) {
        this.sasaranRepository = sasaranRepository;
    }

    public Flux<Sasaran> getAllRealisasiSasaran() {
        return sasaranRepository.findAll();
    }

    public Flux<Sasaran> getAllRealisasiSasaranByTahun(String tahun) {
        return sasaranRepository.findAllByTahun(tahun);
    }

    public Flux<Sasaran> getAllRealisasiSasaranByTahunAndBulan(String tahun, String bulan) {
        return sasaranRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Flux<Sasaran> getAllRealisasiSasaranByTahunAndSasaranId(String tahun, String sasaranId) {
        return sasaranRepository.findAllByTahunAndSasaranId(tahun,  sasaranId);
    }

    public Flux<Sasaran> getAllRealisasiSasaranBySasaranId(String sasaranId) {
        return sasaranRepository.findAllBySasaranId(sasaranId);
    }

    public Mono<Sasaran> getSasaranById(Long id) {
        return sasaranRepository.findById(id);
    }

    public Mono<Sasaran> submitRealisasiSasaran(String sasaranId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, String rumusPerhitungan, String sumberData, JenisRealisasi jenisRealisasi) {
        return Mono.just(buildUnchekcedRealisasiSasaran(sasaranId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, rumusPerhitungan, sumberData, jenisRealisasi))
                .flatMap(sasaranRepository::save);
    }

    // sasaranId check to sasaranService
    // and modify sasaran, check target, satuan, and change status to CHECKED
    public static Sasaran buildUnchekcedRealisasiSasaran(String sasaranId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, String rumusPerhitungan, String sumberData, JenisRealisasi jenisRealisasi) {
        return Sasaran.of(sasaranId,
                "Realisasi Sasaran " + sasaranId,
                indikatorId,
                "Realisasi Indikator " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan,
                rumusPerhitungan, sumberData,
                jenisRealisasi,
                SasaranStatus.UNCHECKED);
    }

    public Flux<Sasaran> getRealisasiSasaranByPeriodeRpjmd(String tahunAwal, String tahunAkhir) {
        return sasaranRepository.findAllByTahunBetween(tahunAwal, tahunAkhir);
    }

    public Flux<Sasaran> batchSubmitRealisasiSasaran(@Valid List<SasaranRequest> sasaranRequests) {
        return Flux.fromIterable(sasaranRequests)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return sasaranRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    Sasaran updated = new Sasaran(
                                            existing.id(),
                                            existing.sasaranId(),
                                            existing.sasaran(),
                                            existing.indikatorId(),
                                            existing.indikator(),
                                            existing.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.rumusPerhitungan(),
                                            req.sumberData(),
                                            req.jenisRealisasi(),
                                            SasaranStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy(),
                                            existing.version()
                                    );
                                    return sasaranRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    Sasaran baru = buildUnchekcedRealisasiSasaran(
                                            req.sasaranId(),
                                            req.indikatorId(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.rumusPerhitungan(),
                                            req.sumberData(),
                                            req.jenisRealisasi()
                                    );
                                    return sasaranRepository.save(baru);
                                }));
                    }
                    else {
                        Sasaran baru = buildUnchekcedRealisasiSasaran(
                                req.sasaranId(),
                                req.indikatorId(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.bulan(),
                                req.rumusPerhitungan(),
                                req.sumberData(),
                                req.jenisRealisasi()
                        );
                        return sasaranRepository.save(baru);
                    }
                });
    }
}
