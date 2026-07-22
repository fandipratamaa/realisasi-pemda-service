package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenghambatRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenunjangRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.LaporanRealisasiRenaksiIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiIndividuRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiResponse;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.integration.kepegawaian.PegawaiClient;
import cc.kertaskerja.integration.penetapan.PenetapanRekinIndividuClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RenaksiService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RenaksiService.class);
    private final RenaksiIndividuRepository renaksiIndividuRepository;
    private final UploadClient uploadClient;
    private final PegawaiClient pegawaiClient;
    private final PenetapanRekinIndividuClient penetapanClient;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public RenaksiService(RenaksiIndividuRepository renaksiIndividuRepository, UploadClient uploadClient, PegawaiClient pegawaiClient, PenetapanRekinIndividuClient penetapanClient) {
        this.renaksiIndividuRepository = renaksiIndividuRepository;
        this.uploadClient = uploadClient;
        this.pegawaiClient = pegawaiClient;
        this.penetapanClient = penetapanClient;
    }

    public Mono<RenaksiResponse> submitRealisasiTarget(RenaksiIndividuRequest req) {
        return penetapanClient.fetchRekinIndividu(req.nip(), req.kodeOpd(), Integer.parseInt(req.tahun()))
                .flatMap(penetapanData -> {
                    return saveRealisasiTarget(req)
                            .map(response -> enrichWithPenetapan(response, penetapanData));
                })
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan untuk nip={}, kodeOpd={}, tahun={}: {}",
                            req.nip(), req.kodeOpd(), req.tahun(), e.getMessage());
                    return saveRealisasiTarget(req)
                            .map(r -> RenaksiResponse.from(r, null, null, null));
                });
    }

    private Mono<RenaksiIndividu> saveRealisasiTarget(RenaksiIndividuRequest req) {
        if (req.targetRealisasiId() != null) {
            return renaksiIndividuRepository.findById(req.targetRealisasiId())
                    .flatMap(existing -> renaksiIndividuRepository.save(buildUpdatedRealisasiTarget(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> renaksiIndividuRepository.save(buildUncheckedRealisasiTarget(req))));
        } else {
            return renaksiIndividuRepository
                    .findFirstByKodeOpdAndNipAndKodeRekinAndKodeRenaksiAndKodePelaksanaanAndTahunAndBulan(
                            req.kodeOpd(), req.nip(), req.kodeRekin(),
                            req.kodeRenaksi(), req.kodePelaksanaan(),
                            req.tahun(), req.bulan())
                    .flatMap(existing -> renaksiIndividuRepository.save(buildUpdatedRealisasiTarget(existing, req)))
                    .switchIfEmpty(Mono.defer(() -> renaksiIndividuRepository.save(buildUncheckedRealisasiTarget(req))));
        }
    }

    private RenaksiResponse enrichWithPenetapan(
            RenaksiIndividu response,
            cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinIndividuData data
    ) {
        log.info("enrichWithPenetapan called for req: rekin={}, renaksi={}, pel={}", response.kodeRekin(), response.kodeRenaksi(), response.kodePelaksanaan());
        if (data.rekins() == null || data.rekins().isEmpty()) {
            log.warn("enrichWithPenetapan: data.rekins() is null or empty");
            return RenaksiResponse.from(response, null, null, null);
        }
        cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinData matchingRekin = data.rekins().stream()
                .filter(r -> r.kodePk().equals(response.kodeRekin()))
                .findFirst()
                .orElse(null);
        if (matchingRekin == null) {
            log.warn("enrichWithPenetapan: matchingRekin not found for {}", response.kodeRekin());
            return RenaksiResponse.from(response, null, null, null);
        }
        cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RenaksiRekinData matchingRenaksi = matchingRekin.renaksis().stream()
                .filter(r -> r.kodeRenaksi().equals(response.kodeRenaksi()))
                .findFirst()
                .orElse(null);
        if (matchingRenaksi == null) {
            log.warn("enrichWithPenetapan: matchingRenaksi not found for {}", response.kodeRenaksi());
            return RenaksiResponse.from(response, null, null, null);
        }
        cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.PelaksanaanRekinData matchingPelaksanaan = matchingRenaksi.pelaksanaans().stream()
                .filter(p -> p.kodePelaksanaan().equals(response.kodePelaksanaan()))
                .findFirst()
                .orElse(null);
        if (matchingPelaksanaan == null) {
            log.warn("enrichWithPenetapan: matchingPelaksanaan not found for {}", response.kodePelaksanaan());
            return RenaksiResponse.from(response, null, null, null);
        }
        Double realisasiValue = response.realisasi() != null
                ? response.realisasi().doubleValue() : null;
        Double bobotValue = matchingPelaksanaan.bobotPelaksanaan() != null
                ? matchingPelaksanaan.bobotPelaksanaan().doubleValue() : null;
        log.info("enrichWithPenetapan: found bobotValue={}", bobotValue);
        var capaianResult = RenaksiIndividu.hitungCapaian(realisasiValue, bobotValue);
        return RenaksiResponse.from(
                response, bobotValue, capaianResult.capaian(), capaianResult.keteranganCapaian()
        );
    }

    public Flux<RenaksiIndividu> getAllByNipAndKodeOpdAndTahunAndBulan(String nip, String kodeOpd, String tahun, String bulan) {
        return renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    public Flux<RenaksiIndividu> searchRealisasi(String kodeOpd, String tahun, String bulan, String levelRole, String nip) {
        java.util.List<String> validRoles = java.util.List.of("LEVEL_1", "LEVEL_2", "LEVEL_3", "LEVEL_4");
        if (!validRoles.contains(levelRole.toUpperCase())) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "levelRole tidak valid"));
        }

        return pegawaiClient.fetchAllPegawai()
                .flatMapMany(pegawais -> {
                    boolean nipExists = pegawais.stream()
                            .anyMatch(p -> nip.equals(p.nip()));
                    
                    if (!nipExists) {
                        return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pegawai dengan NIP tersebut tidak ditemukan di service Kepegawaian"));
                    }
                    
                    return getAllByNipAndKodeOpdAndTahunAndBulan(nip, kodeOpd, tahun, bulan);
                });
    }

    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasi(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<RenaksiIndividu>> grouped = list.stream()
                            .collect(java.util.stream.Collectors.groupingBy(t -> t.kodeRenaksi() + "|" + t.kodePelaksanaan()));

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
                        
                        return new LaporanRealisasiRenaksiIndividuResponse(tahun, kodeOpd, nip, first.kodeRenaksi(), null, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasiByOpd(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan, String levelRole, String nip) {
        java.util.List<String> validRoles = java.util.List.of("LEVEL_1", "LEVEL_2", "LEVEL_3", "LEVEL_4");
        if (!validRoles.contains(levelRole.toUpperCase())) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "levelRole tidak valid"));
        }

        return pegawaiClient.fetchAllPegawai()
                .flatMapMany(pegawais -> {
                    boolean nipExists = pegawais.stream()
                            .anyMatch(p -> nip.equals(p.nip()));
                    
                    if (!nipExists) {
                        return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pegawai dengan NIP tersebut tidak ditemukan di service Kepegawaian"));
                    }
                    
                    return renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                            .collectList()
                            .flatMapMany(list -> {
                                Map<String, List<RenaksiIndividu>> grouped = list.stream()
                                        .collect(java.util.stream.Collectors.groupingBy(t -> t.nip() + "|" + t.kodeRenaksi() + "|" + t.kodePelaksanaan()));

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
                                    
                                    return new LaporanRealisasiRenaksiIndividuResponse(tahun, kodeOpd, first.nip(), first.kodeRenaksi(), null, jenisLaporan, listData, totalRealisasi);
                                });
                            });
                });
    }

    public Mono<RenaksiIndividu> updateFaktorPenunjang(FaktorPenunjangRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeRekinAndKodeRenaksiAndKodePelaksanaanAndTahunAndBulan(
                        req.kodeOpd(), req.nip(), req.kodeRekin(),
                        req.kodeRenaksi(), req.kodePelaksanaan(),
                        req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(),
                            existing.kodeRekin(),
                            existing.kodeRenaksi(),
                            existing.kodePelaksanaan(),
                            existing.realisasi(),
                            existing.tahun(), existing.bulan(), existing.satuan(), existing.status(),
                            existing.jenisRealisasi(),
                            req.faktorPenunjang(), existing.faktorPenghambat(), existing.buktiPendukung(), existing.keteranganBuktiPendukung(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                });
    }

    public Mono<RenaksiIndividu> updateFaktorPenghambat(FaktorPenghambatRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeRekinAndKodeRenaksiAndKodePelaksanaanAndTahunAndBulan(
                        req.kodeOpd(), req.nip(), req.kodeRekin(),
                        req.kodeRenaksi(), req.kodePelaksanaan(),
                        req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(),
                            existing.kodeRekin(),
                            existing.kodeRenaksi(),
                            existing.kodePelaksanaan(),
                            existing.realisasi(),
                            existing.tahun(), existing.bulan(), existing.satuan(), existing.status(),
                            existing.jenisRealisasi(),
                            existing.faktorPenunjang(), req.faktorPenghambat(), existing.buktiPendukung(), existing.keteranganBuktiPendukung(),
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
                req.kodeRekin(),
                req.kodeRenaksi(),
                req.kodePelaksanaan(),
                req.realisasi(),
                req.tahun(), req.bulan(), req.satuan(), RenaksiStatus.UNCHECKED,
                req.jenisRealisasi(),
                existing.faktorPenunjang(), existing.faktorPenghambat(),
                req.buktiPendukung() != null && !req.buktiPendukung().isBlank() ? req.buktiPendukung() : existing.buktiPendukung(),
                req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                existing.createdBy(), existing.lastModifiedBy(),
                existing.createdDate(), existing.lastModifiedDate()
        );
    }

    private static RenaksiIndividu buildUncheckedRealisasiTarget(RenaksiIndividuRequest req) {
        return RenaksiIndividu.of(
                req.kodeOpd(), req.nip(),
                req.kodeRekin(),
                req.kodeRenaksi(),
                req.kodePelaksanaan(),
                req.realisasi(),
                req.tahun(), req.bulan(), req.satuan(), RenaksiStatus.UNCHECKED,
                req.jenisRealisasi(), "", "", req.buktiPendukung(), req.keteranganBuktiPendukung()
        );
    }

    public Mono<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse> getPenetapanByNip(String nip, String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRekinIndividu(nip, kodeOpd, tahun)
                .flatMap(data -> buildResponseWithBulan(data, nip, kodeOpd, tahun, bulan));
    }

    public Mono<String> syncPenetapanRenaksiIndividu(String nip, String kodeOpd, int tahun) {
        return penetapanClient.syncRekinIndividu(nip, kodeOpd, tahun);
    }

    private cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mapIndikatorToResponse(
            cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.IndikatorRekinData indikator
    ) {
        List<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                        t.id(), t.kodeTargetPk(), t.tahun(), t.target(), t.satuan(),
                        null, null, null, null, null, null, null, null
                ))
                .toList();
        return new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                indikator.id(), indikator.kodeIndikatorPk(), indikator.namaIndikatorPk(), targets
        );
    }

    private Mono<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse> buildResponseWithBulan(
            cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinIndividuData data,
            String nip, String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);
        Flux<RenaksiIndividu> realisasiFlux;
        if (bulan == null || bulan.isBlank()) {
            realisasiFlux = renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahunStr);
        } else {
            realisasiFlux = renaksiIndividuRepository.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahunStr, bulan);
        }

        return realisasiFlux
                .collectList()
                .map(localList -> {
                    Map<String, RenaksiIndividu> localTargetMap = buildLocalTargetMap(localList);
                    List<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.RekinPenetapanResponse> rekins = data.rekins().stream()
                            .map(r -> mergeRekinWithRealisasi(r, localTargetMap))
                            .toList();
                    return new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse(
                            data.pegawaiId(), data.nama(), data.kodeOpd(), data.tahunAktif(),
                            (bulan == null || bulan.isBlank()) ? null : Integer.parseInt(bulan), rekins
                    );
                });
    }

    private Map<String, RenaksiIndividu> buildLocalTargetMap(List<RenaksiIndividu> localList) {
        return localList.stream()
                .collect(java.util.stream.Collectors.toMap(
                        r -> buildTargetKey(r.kodeRekin(), r.kodeRenaksi(), r.kodePelaksanaan()),
                        java.util.function.Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    private String buildTargetKey(String kodeRekin, String kodeRenaksi, String kodePelaksanaan) {
        return kodeRekin + "|" + kodeRenaksi + "|" + kodePelaksanaan;
    }

    private cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.RekinPenetapanResponse mergeRekinWithRealisasi(
            cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinData rekin,
            Map<String, RenaksiIndividu> localTargetMap
    ) {
        List<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.IndikatorPenetapanResponse> indikators = rekin.indikatorPk().stream()
                .map(this::mapIndikatorToResponse)
                .toList();
        List<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.RenaksiPenetapanResponse> renaksis = rekin.renaksis().stream()
                .map(r -> {
                    List<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.PelaksanaanPenetapanResponse> pelaksanaans = r.pelaksanaans().stream()
                            .map(p -> mergePelaksanaanWithRealisasi(rekin.kodePk(), r.kodeRenaksi(), p, localTargetMap))
                            .toList();
                    return new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.RenaksiPenetapanResponse(
                            r.id(), r.urutanRenaksi(), r.kodeRenaksi(), r.namaRenaksi(), r.anggaranRenaksi(), pelaksanaans
                    );
                })
                .toList();
        return new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                rekin.id(), rekin.kodeSasaranOpd(), rekin.kodePk(), rekin.rekin(),
                rekin.anggaranPk(), rekin.versi(), indikators, renaksis
        );
    }

    private cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.PelaksanaanPenetapanResponse mergePelaksanaanWithRealisasi(
            String kodeRekin, String kodeRenaksi,
            cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.PelaksanaanRekinData pelaksanaan,
            Map<String, RenaksiIndividu> localTargetMap
    ) {
        String key = buildTargetKey(kodeRekin, kodeRenaksi, pelaksanaan.kodePelaksanaan());
        RenaksiIndividu local = localTargetMap.get(key);
        Double realisasiValue = local != null && local.realisasi() != null
                ? local.realisasi().doubleValue() : null;
        Double bobotValue = pelaksanaan.bobotPelaksanaan() != null ? pelaksanaan.bobotPelaksanaan().doubleValue() : null;
        RenaksiIndividu.CapaianResult capaianResult = RenaksiIndividu.hitungCapaian(realisasiValue, bobotValue);
        String faktorPenunjang = local != null ? local.faktorPenunjang() : null;
        String faktorPenghambat = local != null ? local.faktorPenghambat() : null;
        String buktiPendukung = local != null ? local.buktiPendukung() : null;
        String keteranganBuktiPendukung = local != null ? local.keteranganBuktiPendukung() : null;
        String jenisRealisasi = local != null && local.jenisRealisasi() != null ? local.jenisRealisasi().name() : null;
        return new cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.PelaksanaanPenetapanResponse(
                pelaksanaan.id(), pelaksanaan.kodePelaksanaan(), pelaksanaan.bulanPelaksanaan(), pelaksanaan.bobotPelaksanaan(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung, jenisRealisasi
        );
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }
}

