package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenghambatSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenunjangSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.LaporanRealisasiSasaranResponse;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SasaranService {
    private final SasaranRepository sasaranRepository;

    public SasaranService(SasaranRepository sasaranRepository) {
        this.sasaranRepository = sasaranRepository;
    }

    public Flux<Sasaran> getAllRealisasiSasaranByTahunAndBulan(String tahun, String bulan) {
        return sasaranRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Mono<Sasaran> submitRealisasiSasaran(SasaranRequest req) {
        if (req.targetRealisasiId() != null) {
            return sasaranRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> sasaranRepository.save(buildUpdatedRealisasiSasaran(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Sasaran baru = buildUnchekcedRealisasiSasaran(
                                req.sasaranId(), req.indikatorId(), req.targetId(),
                                req.target(), req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(), req.rumusPerhitungan(),
                                req.sumberData(), req.jenisRealisasi());
                        return sasaranRepository.save(baru);
                    }));
        }
        return sasaranRepository
                .findFirstBySasaranIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                        req.sasaranId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .flatMap(existing -> sasaranRepository.save(buildUpdatedRealisasiSasaran(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Sasaran baru = buildUnchekcedRealisasiSasaran(
                            req.sasaranId(), req.indikatorId(), req.targetId(),
                            req.target(), req.realisasi(), req.satuan(),
                            req.tahun(), req.bulan(), req.rumusPerhitungan(),
                            req.sumberData(), req.jenisRealisasi());
                    return sasaranRepository.save(baru);
                }));
    }

    private static Sasaran buildUpdatedRealisasiSasaran(Sasaran existing, SasaranRequest req) {
        return new Sasaran(
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
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                SasaranStatus.UNCHECKED,
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
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
                "",
                "",
                jenisRealisasi,
                SasaranStatus.UNCHECKED);
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
                                            existing.faktorPenunjang(),
                                            existing.faktorPenghambat(),
                                            req.jenisRealisasi(),
                                            SasaranStatus.UNCHECKED,
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy()
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

    public Flux<LaporanRealisasiSasaranResponse> getLaporanRealisasi(String tahun, JenisLaporan jenisLaporan, String bulan) {
        return sasaranRepository.findAllByTahun(tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<Sasaran>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(s -> s.indikatorId() + "|" + s.targetId()));
                    
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        Sasaran first = groupList.get(0);
                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(s -> bulan.equals(s.bulan()))
                                        .filter(s -> s.realisasi() != null)
                                        .mapToDouble(Sasaran::realisasi)
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (Sasaran s : groupList) {
                                    if (s.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(s.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, s.realisasi(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (Sasaran s : groupList) {
                                    if (s.realisasi() == null) continue;
                                    String key = s.bulan();
                                    bulanMap.merge(key, s.realisasi(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        return new LaporanRealisasiSasaranResponse(tahun, first.indikator(), first.target(), jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Mono<Sasaran> updateFaktorPenunjang(FaktorPenunjangSasaranRequest req) {
        return sasaranRepository
                .findFirstBySasaranIdAndIndikatorIdAndTargetIdAndTahunAndBulan(req.sasaranId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(existing -> {
                    Sasaran updated = new Sasaran(
                            existing.id(),
                            existing.sasaranId(),
                            existing.sasaran(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            SasaranStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranRepository.save(updated);
                });
    }

    public Mono<Sasaran> updateFaktorPenghambat(FaktorPenghambatSasaranRequest req) {
        return sasaranRepository
                .findFirstBySasaranIdAndIndikatorIdAndTargetIdAndTahunAndBulan(req.sasaranId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")))
                .flatMap(existing -> {
                    Sasaran updated = new Sasaran(
                            existing.id(),
                            existing.sasaranId(),
                            existing.sasaran(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            SasaranStatus.UNCHECKED,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return sasaranRepository.save(updated);
                });
    }
}
