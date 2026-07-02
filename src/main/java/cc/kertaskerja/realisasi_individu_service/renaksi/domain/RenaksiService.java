package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenghambatRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenunjangRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.LaporanRealisasiRenaksiIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiIndividuRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RenaksiService {
    private final RenaksiIndividuRepository renaksiIndividuRepository;

    public RenaksiService(RenaksiIndividuRepository renaksiIndividuRepository) {
        this.renaksiIndividuRepository = renaksiIndividuRepository;
    }

    public Mono<RenaksiIndividu> submitRealisasiTarget(RenaksiIndividuRequest req) {
        if (req.targetRealisasiId() != null) {
            return renaksiIndividuRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> renaksiIndividuRepository.save(buildUpdatedRealisasiTarget(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> {
                        RenaksiIndividu baru = buildUncheckedRealisasiTarget(req);
                        return renaksiIndividuRepository.save(baru);
                    }));
        }
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget(),
                        req.tahun(), req.bulan())
                .flatMap(existing -> renaksiIndividuRepository.save(buildUpdatedRealisasiTarget(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    RenaksiIndividu baru = buildUncheckedRealisasiTarget(req);
                    return renaksiIndividuRepository.save(baru);
                }));
    }

    public Flux<RenaksiIndividu> batchSubmitRealisasiTarget(List<RenaksiIndividuRequest> reqs) {
        return Flux.fromIterable(reqs)
                .flatMap(req -> {
                    if (req.targetRealisasiId() != null) {
                        return renaksiIndividuRepository.findById(req.targetRealisasiId())
                                .flatMap(existing -> {
                                    RenaksiIndividu updated = buildUpdatedRealisasiTarget(existing, req);
                                    return renaksiIndividuRepository.save(updated);
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    RenaksiIndividu baru = buildUncheckedRealisasiTarget(req);
                                    return renaksiIndividuRepository.save(baru);
                                }));
                    } else {
                        RenaksiIndividu baru = buildUncheckedRealisasiTarget(req);
                        return renaksiIndividuRepository.save(baru);
                    }
                });
    }

    public Flux<RenaksiIndividu> getAllByNipAndKodeOpdAndTahunAndBulan(String nip, String kodeOpd, String tahun, String bulan) {
        return renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasi(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<RenaksiIndividu>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));

                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenaksiIndividu first = groupList.get(0);
                        
                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(t -> bulan.equals(t.bulan()))
                                        .filter(t -> t.realisasi() != null)
                                        .mapToDouble(t -> t.realisasi().doubleValue())
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (RenaksiIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi().doubleValue(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (RenaksiIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    bulanMap.merge(t.bulan(), t.realisasi().doubleValue(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }
                        
                        return new LaporanRealisasiRenaksiIndividuResponse(tahun, kodeOpd, nip, first.indikator(), first.target() != null ? first.target().toString() : null, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Mono<RenaksiIndividu> updateFaktorPenunjang(FaktorPenunjangRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget(),
                        req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(),
                            existing.kodeSasaran(), existing.sasaran(),
                            existing.kodeRenaksi(), existing.renaksi(),
                            existing.kodeIndikator(), existing.indikator(),
                            existing.kodeTarget(), existing.target(),
                            existing.paguAnggaran(), existing.realisasi(),
                            existing.tahun(), existing.bulan(), existing.satuan(), existing.status(),
                            existing.jenisRealisasi(),
                            req.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                });
    }

    public Mono<RenaksiIndividu> updateFaktorPenghambat(FaktorPenghambatRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget(),
                        req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(),
                            existing.kodeSasaran(), existing.sasaran(),
                            existing.kodeRenaksi(), existing.renaksi(),
                            existing.kodeIndikator(), existing.indikator(),
                            existing.kodeTarget(), existing.target(),
                            existing.paguAnggaran(), existing.realisasi(),
                            existing.tahun(), existing.bulan(), existing.satuan(), existing.status(),
                            existing.jenisRealisasi(),
                            existing.faktorPenunjang(), req.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                });
    }

    private static RenaksiIndividu buildUpdatedRealisasiTarget(RenaksiIndividu existing, RenaksiIndividuRequest req) {
        return new RenaksiIndividu(
                existing.id(),
                req.kodeOpd(), req.nip(),
                req.kodeSasaran(), "Realisasi Sasaran " + req.kodeSasaran(),
                req.kodeRenaksi(), "Realisasi Renaksi " + req.kodeRenaksi(),
                req.kodeIndikator(), "Realisasi Indikator " + req.kodeIndikator(),
                req.kodeTarget(), req.target(),
                req.paguAnggaran(), req.realisasi(),
                req.tahun(), req.bulan(), req.satuan(), RenaksiStatus.UNCHECKED,
                req.jenisRealisasi(),
                existing.faktorPenunjang(), existing.faktorPenghambat(),
                existing.createdBy(), existing.lastModifiedBy(),
                existing.createdDate(), existing.lastModifiedDate()
        );
    }

    private static RenaksiIndividu buildUncheckedRealisasiTarget(RenaksiIndividuRequest req) {
        return RenaksiIndividu.of(
                req.kodeOpd(), req.nip(),
                req.kodeSasaran(), "Realisasi Sasaran " + req.kodeSasaran(),
                req.kodeRenaksi(), "Realisasi Renaksi " + req.kodeRenaksi(),
                req.kodeIndikator(), "Realisasi Indikator " + req.kodeIndikator(),
                req.kodeTarget(), req.target(),
                req.paguAnggaran(), req.realisasi(),
                req.tahun(), req.bulan(), req.satuan(), RenaksiStatus.UNCHECKED,
                req.jenisRealisasi(), "", ""
        );
    }
}
