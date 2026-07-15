package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenghambatRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenunjangRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.LaporanRealisasiTujuanResponse;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TujuanService {
    private final TujuanRepository tujuanRepository;
    private final UploadClient uploadClient;

    public TujuanService(TujuanRepository tujuanRepository, UploadClient uploadClient) {
        this.tujuanRepository = tujuanRepository;
        this.uploadClient = uploadClient;
    }

    public Mono<Tujuan> submitRealisasiTujuan(TujuanRequest req) {
        String bukti = req.buktiPendukung() != null ? req.buktiPendukung() : "";

            if (req.targetRealisasiId() != null) {
                return tujuanRepository.findById(req.targetRealisasiId())
                        .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req, bukti)))
                        .switchIfEmpty(Mono.defer(() -> {
                            Tujuan baru = buildUncheckedRealisasiTujuan(
                                    req.tujuanId(), req.indikatorId(), req.targetId(),
                                    req.target(), req.realisasi(), req.satuan(),
                                    req.tahun(), req.bulan(), req.visiMisi(),
                                    req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                            return tujuanRepository.save(baru);
                        }));
            }
            return tujuanRepository
                    .findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                            req.tujuanId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan())
                    .flatMap(existing -> tujuanRepository.save(buildUpdatedRealisasiTujuan(existing, req, bukti)))
                    .switchIfEmpty(Mono.defer(() -> {
                        Tujuan baru = buildUncheckedRealisasiTujuan(
                                req.tujuanId(), req.indikatorId(), req.targetId(),
                                req.target(), req.realisasi(), req.satuan(),
                                req.tahun(), req.bulan(), req.visiMisi(),
                                req.rumusPerhitungan(), req.sumberData(), req.jenisRealisasi(), bukti, req.keteranganBuktiPendukung());
                        return tujuanRepository.save(baru);
                    }));
    }

    private static Tujuan buildUpdatedRealisasiTujuan(Tujuan existing, TujuanRequest req, String buktiPendukung) {
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
                buktiPendukung != null && !buktiPendukung.isBlank() ? buktiPendukung : existing.buktiPendukung(),
                req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
    }

    public static Tujuan buildUncheckedRealisasiTujuan(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, String visiMisi, String rumusPerhitungan, String sumberData, JenisRealisasi jenisRealisasi, String buktiPendukung, String keteranganBuktiPendukung) {
        return Tujuan.of(tujuanId,
                "Realisasi Tujuan " + tujuanId,
                indikatorId,
                "Realisasi Indikator " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan, visiMisi, rumusPerhitungan, sumberData,
                "",
                "",
                jenisRealisasi,
                TujuanStatus.UNCHECKED,
                buktiPendukung,
                keteranganBuktiPendukung);
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
                            existing.keteranganBuktiPendukung(),
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
                            existing.keteranganBuktiPendukung(),
                            existing.createdBy(),
                            existing.createdDate(),
                            existing.lastModifiedDate(),
                            existing.lastModifiedBy()
                    );
                    return tujuanRepository.save(updated);
                });
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }
}
