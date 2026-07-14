package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenghambatRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenunjangRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.LaporanRealisasiTujuanResponse;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TujuanService {
    private final TujuanRepository tujuanRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public TujuanService(TujuanRepository tujuanRepository) {
        this.tujuanRepository = tujuanRepository;
    }

    public Mono<Tujuan> submitRealisasiTujuan(TujuanRequest req) {
        if (req.targetRealisasiId() != null) {
            return tujuanRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.tujuanId(), req.indikatorId(), req.targetId(),
                                req.target(), req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(), req.visiMisi(),
                                req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi(), req.buktiPendukung());
                        return tujuanRepository.save(baru);
                    }));
        }
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                        req.tujuanId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    Tujuan baru = buildUncheckedRealisasiTujuan(
                            req.tujuanId(), req.indikatorId(), req.targetId(),
                            req.target(), req.realisasi(), req.satuan(),
                            req.tahun(), req.bulan(), req.visiMisi(),
                            req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi(), req.buktiPendukung());
                    return tujuanRepository.save(baru);
                }));
    }

    private static Tujuan buildUpdatedRealisasiTujuan(Tujuan existing, TujuanRequest req) {
        return new Tujuan(
                existing.id(),
                existing.tujuanId(),
                existing.tujuan(),
                existing.indikatorId(),
                existing.indikator(),
                existing.targetId(),
                req.target(),
                req.realisasi(),
                req.satuan(),
                req.tahun(),
                req.bulan(),
                req.visiMisi(),
                req.rumusPerhitungan(),
                req.sumberData(),
                existing.faktorPenunjang(),
                existing.faktorPenghambat(),
                req.jenisRealisasi(),
                TujuanStatus.UNCHECKED,
                req.buktiPendukung(),
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
    }

    public Flux<Tujuan> batchSubmitRealisasiTujuan(List<TujuanRequest> tujuans) {
        return Flux.fromIterable(tujuans)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return tujuanRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    Tujuan updated = new Tujuan(
                                            existing.id(),
                                            existing.tujuanId(),
                                            existing.tujuan(),
                                            existing.indikatorId(),
                                            existing.indikator(),
                                            existing.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.visiMisi(),
                                            req.rumusPerhitungan(),
                                            req.sumberData(),
                                            existing.faktorPenunjang(),
                                            existing.faktorPenghambat(),
                                            req.jenisRealisasi(),
                                            TujuanStatus.UNCHECKED,
                                            req.buktiPendukung(),
                                            existing.createdBy(),
                                            existing.createdDate(),
                                            existing.lastModifiedDate(),
                                            existing.lastModifiedBy()
                                    );
                                    return tujuanRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    Tujuan baru = buildUncheckedRealisasiTujuan(
                                            req.tujuanId(),
                                            req.indikatorId(),
                                            req.targetId(),
                                            req.target(),
                                            req.realisasi(),
                                            req.satuan(),
                                            req.tahun(),
                                            req.bulan(),
                                            req.visiMisi(),
                                            req.rumusPerhitungan(),
                                        req.sumberData(),
                                        req.jenisRealisasi(),
                                        req.buktiPendukung()
                                    );
                                    return tujuanRepository.save(baru);
                                }));
                    }
                    else {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.tujuanId(),
                                req.indikatorId(),
                                req.targetId(),
                                req.target(),
                                req.realisasi(),
                                req.satuan(),
                                req.tahun(),
                                req.bulan(),
                                req.visiMisi(),
                                req.rumusPerhitungan(),
                                req.sumberData(),
                                req.jenisRealisasi(),
                                req.buktiPendukung()
                        );
                        return tujuanRepository.save(baru);
                    }
                }
        );
    }

    public static Tujuan buildUncheckedRealisasiTujuan(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, String visiMisi, String rumusPerhitungan, String sumberData, JenisRealisasi jenisRealisasi, String buktiPendukung) {
        return Tujuan.of(tujuanId,
                "Realisasi Tujuan " + tujuanId,
                indikatorId,
                "Realisasi Indikator " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan, visiMisi, rumusPerhitungan, sumberData,
                "",
                "",
                jenisRealisasi,
                TujuanStatus.UNCHECKED,
                buktiPendukung);
    }

    public Flux<Tujuan> getRealisasiTujuanByTahunAndBulan(String tahun, String bulan) {
        return tujuanRepository.findAllByTahunAndBulan(tahun, bulan);
    }

    public Flux<LaporanRealisasiTujuanResponse> getLaporanRealisasi(String tahun, JenisLaporan jenisLaporan, String bulan) {
        return tujuanRepository.findAllByTahun(tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<Tujuan>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(t -> t.indikatorId() + "|" + t.targetId()));
                    
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        Tujuan first = groupList.get(0);
                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(t -> bulan.equals(t.bulan()))
                                        .filter(t -> t.realisasi() != null)
                                        .mapToDouble(Tujuan::realisasi)
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (Tujuan t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (Tujuan t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    String key = t.bulan();
                                    bulanMap.merge(key, t.realisasi(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        return new LaporanRealisasiTujuanResponse(tahun, first.indikator(), first.target(), jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Mono<Tujuan> updateFaktorPenunjang(FaktorPenunjangRequest req) {
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(req.tujuanId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.tujuanId(),
                            existing.tujuan(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.visiMisi(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                });
    }

    public Mono<Tujuan> updateFaktorPenghambat(FaktorPenghambatRequest req) {
        return tujuanRepository
                .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(req.tujuanId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.tujuanId(),
                            existing.tujuan(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.visiMisi(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            existing.buktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                });
    }

    public Mono<Tujuan> uploadBuktiPendukung(Long id, FilePart file) {
        return tujuanRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")))
                .flatMap(existing -> uploadFile(file).flatMap(filePath -> {
                    Tujuan updated = new Tujuan(
                            existing.id(),
                            existing.tujuanId(),
                            existing.tujuan(),
                            existing.indikatorId(),
                            existing.indikator(),
                            existing.targetId(),
                            existing.target(),
                            existing.realisasi(),
                            existing.satuan(),
                            existing.tahun(),
                            existing.bulan(),
                            existing.visiMisi(),
                            existing.rumusPerhitungan(),
                            existing.sumberData(),
                            existing.faktorPenunjang(),
                            existing.faktorPenghambat(),
                            existing.jenisRealisasi(),
                            TujuanStatus.UNCHECKED,
                            filePath,
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                }));
    }

    private Mono<String> uploadFile(FilePart file) {
        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Gagal membuat direktori upload", e));
        }

        String filename = System.currentTimeMillis() + "_" + file.filename();
        Path targetPath = basePath.resolve(filename);

        return file.transferTo(targetPath)
                .thenReturn("/uploads/" + filename);
    }
}
