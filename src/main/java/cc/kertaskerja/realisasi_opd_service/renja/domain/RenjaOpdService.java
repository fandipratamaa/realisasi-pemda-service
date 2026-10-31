package cc.kertaskerja.realisasi_opd_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.LaporanRealisasiRenjaKegiatanOpdResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenghambatTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenunjangTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.LaporanRealisasiRenjaProgramOpdResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.LaporanRealisasiRenjaSubKegiatanOpdResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RenjaOpdService {
    private final PenetapanRenjaOpdClient penetapanClient;
    private final RenjaProgramOpdRepository targetProgramRepo;
    private final RenjaKegiatanOpdRepository targetKegiatanRepo;
    private final RenjaSubKegiatanOpdRepository targetSubKegiatanRepo;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    record RealisasiData(Double realisasi, String faktorPenunjang, String faktorPenghambat, String buktiPendukung) {}

    record CapaianResult(Double capaian, String keteranganCapaian) {}

    record PeriodeRealisasi(String bulan, BigDecimal realisasi) {}

    public RenjaOpdService(
            PenetapanRenjaOpdClient penetapanClient,
            RenjaProgramOpdRepository targetProgramRepo,
            RenjaKegiatanOpdRepository targetKegiatanRepo,
            RenjaSubKegiatanOpdRepository targetSubKegiatanRepo
    ) {
        this.penetapanClient = penetapanClient;
        this.targetProgramRepo = targetProgramRepo;
        this.targetKegiatanRepo = targetKegiatanRepo;
        this.targetSubKegiatanRepo = targetSubKegiatanRepo;
    }

    public Mono<RenjaOpdPenetapanResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRenjaOpd(kodeOpd, tahun)
                .flatMap(root -> {
                    String effectiveKodeOpd = root.kodeOpd() != null ? root.kodeOpd() : kodeOpd;
                    Integer effectiveTahun = root.tahunAktif() != null ? root.tahunAktif() : tahun;

                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(mapWithoutRealisasi(root, effectiveKodeOpd, effectiveTahun));
                    }

                    return fetchRealisasiAndMerge(root, effectiveKodeOpd, effectiveTahun, bulan);
                })
                .defaultIfEmpty(new RenjaOpdPenetapanResponse(
                        kodeOpd, tahun, parseInteger(bulan),
                        List.of(), List.of(), List.of()
                ));
    }

    public Mono<String> syncPenetapanRenjaOpd(String kodeOpd, int tahun) {
        return penetapanClient.syncRenjaOpd(kodeOpd, tahun);
    }

    public Mono<RenjaProgramOpd> syncRealisasiProgram(
            String kodeOpd, String tahun, String bulan,
            String kodeProgram, String kodeIndikator, String kodeTarget, String kodePagu,
            BigDecimal realisasi
    ) {
        return targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        kodeOpd, kodeProgram, kodeIndikator, kodeTarget, tahun, bulan)
                .flatMap(existing -> {
                    RenjaProgramOpd updated = new RenjaProgramOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeProgram(), existing.kodeIndikator(), existing.kodeTarget(),
                            existing.kodePagu() != null ? existing.kodePagu() : (kodePagu != null ? kodePagu : ""),
                            realisasi,
                            existing.jenisRealisasi() != null ? existing.jenisRealisasi() : "NAIK",
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.buktiPendukung(),
                            existing.createdDate(), null, existing.createdBy(), null
                    );
                    return targetProgramRepo.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RenjaProgramOpd newRecord = new RenjaProgramOpd(
                            null, kodeOpd, tahun, bulan,
                            kodeProgram, kodeIndikator, kodeTarget,
                            kodePagu != null ? kodePagu : "",
                            realisasi,
                            "NAIK",
                            "", "",
                            null,
                            null, null, null, null
                    );
                    return targetProgramRepo.save(newRecord);
                }));
    }

    public Mono<RenjaKegiatanOpd> syncRealisasiKegiatan(
            String kodeOpd, String tahun, String bulan,
            String kodeKegiatan, String kodeIndikator, String kodeTarget, String kodePagu,
            BigDecimal realisasi
    ) {
        return targetKegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        kodeOpd, kodeKegiatan, kodeIndikator, kodeTarget, tahun, bulan)
                .flatMap(existing -> {
                    RenjaKegiatanOpd updated = new RenjaKegiatanOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeKegiatan(), existing.kodeIndikator(), existing.kodeTarget(),
                            existing.kodePagu() != null ? existing.kodePagu() : (kodePagu != null ? kodePagu : ""),
                            realisasi,
                            existing.jenisRealisasi() != null ? existing.jenisRealisasi() : "NAIK",
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.buktiPendukung(),
                            existing.createdDate(), null, existing.createdBy(), null
                    );
                    return targetKegiatanRepo.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RenjaKegiatanOpd newRecord = new RenjaKegiatanOpd(
                            null, kodeOpd, tahun, bulan,
                            kodeKegiatan, kodeIndikator, kodeTarget,
                            kodePagu != null ? kodePagu : "",
                            realisasi,
                            "NAIK",
                            "", "",
                            null,
                            null, null, null, null
                    );
                    return targetKegiatanRepo.save(newRecord);
                }));
    }

    public Mono<RenjaSubKegiatanOpd> syncRealisasiSubKegiatan(
            String kodeOpd, String tahun, String bulan,
            String kodeSubkegiatan, String kodeIndikator, String kodeTarget, String kodePagu,
            BigDecimal realisasiTarget, BigDecimal realisasiPagu
    ) {
        String effectiveKodePagu = kodePagu != null ? kodePagu : "";
        return targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndKodePaguAndTahunAndBulan(
                        kodeOpd, kodeSubkegiatan, kodeIndikator, kodeTarget, effectiveKodePagu, tahun, bulan)
                .switchIfEmpty(Mono.defer(() -> targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        kodeOpd, kodeSubkegiatan, kodeIndikator, kodeTarget, tahun, bulan)))
                .flatMap(existing -> {
                    RenjaSubKegiatanOpd updated = new RenjaSubKegiatanOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeSubkegiatan(), existing.kodeIndikator(), existing.kodeTarget(),
                            existing.kodePagu() != null && !existing.kodePagu().isBlank() ? existing.kodePagu() : effectiveKodePagu,
                            realisasiTarget,
                            realisasiPagu,
                            existing.jenisRealisasi() != null ? existing.jenisRealisasi() : "NAIK",
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.buktiPendukung(),
                            existing.createdDate(), null, existing.createdBy(), null
                    );
                    return targetSubKegiatanRepo.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RenjaSubKegiatanOpd newRecord = new RenjaSubKegiatanOpd(
                            null, kodeOpd, tahun, bulan,
                            kodeSubkegiatan, kodeIndikator, kodeTarget,
                            effectiveKodePagu,
                            realisasiTarget,
                            realisasiPagu,
                            "NAIK",
                            "", "",
                            null,
                            null, null, null, null
                    );
                    return targetSubKegiatanRepo.save(newRecord);
                }));
    }

    public Mono<RenjaProgramOpd> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramOpdRequest req) {
        return targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program OPD tidak ditemukan")))
                .flatMap(existing -> targetProgramRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaProgramOpd> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramOpdRequest req) {
        return targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program OPD tidak ditemukan")))
                .flatMap(existing -> targetProgramRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanOpdRequest req) {
        return targetKegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanOpdRequest req) {
        return targetKegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenunjangSubKegiatan(FaktorPenunjangTargetRenjaSubKegiatanOpdRequest req) {
        return targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubkegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetSubKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenghambatSubKegiatan(FaktorPenghambatTargetRenjaSubKegiatanOpdRequest req) {
        return targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubkegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetSubKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Flux<LaporanRealisasiRenjaProgramOpdResponse> getLaporanRealisasiProgram(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        Mono<PenetapanRenjaOpd.PenetapanRenjaOpdRoot> penetapanMono = fetchPenetapanOrEmpty(kodeOpd, tahun);
        return Mono.zip(targetProgramRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList(), penetapanMono)
                .flatMapMany(tuple -> {
                    List<RenjaProgramOpd> list = tuple.getT1();
                    PenetapanRenjaOpd.PenetapanRenjaOpdRoot root = tuple.getT2();
                    Map<String, List<RenjaProgramOpd>> grouped = list.stream()
                            .collect(Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaProgramOpd first = groupList.get(0);
                        String program = first.kodeProgram();
                        String indikatorName = first.kodeIndikator();
                        String targetName = first.kodeTarget();
                        for (PenetapanRenjaOpd.ProgramPenetapanData p : safeList(root.programs())) {
                            for (PenetapanRenjaOpd.IndikatorPenetapanData ind : safeList(p.indikators())) {
                                for (PenetapanRenjaOpd.TargetPenetapanData tgt : safeList(ind.targets())) {
                                    if (first.kodeTarget().equals(tgt.kodeTarget())) {
                                        program = p.program() != null ? p.program() : program;
                                        indikatorName = ind.indikator() != null ? ind.indikator() : indikatorName;
                                        targetName = tgt.target() != null ? String.valueOf(tgt.target()) : targetName;
                                        break;
                                    }
                                }
                            }
                        }
                        List<PeriodeRealisasi> periode = groupList.stream()
                                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()))
                                .toList();
                        Map<String, Double> listData = buildLaporanData(periode, jenisLaporan, bulan);
                        Double totalRealisasi = hitungTotalRealisasi(listData, jenisLaporan);
                        return new LaporanRealisasiRenjaProgramOpdResponse(
                                tahun, kodeOpd, program, indikatorName, targetName,
                                jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenjaKegiatanOpdResponse> getLaporanRealisasiKegiatan(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        Mono<PenetapanRenjaOpd.PenetapanRenjaOpdRoot> penetapanMono = fetchPenetapanOrEmpty(kodeOpd, tahun);
        return Mono.zip(targetKegiatanRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList(), penetapanMono)
                .flatMapMany(tuple -> {
                    List<RenjaKegiatanOpd> list = tuple.getT1();
                    PenetapanRenjaOpd.PenetapanRenjaOpdRoot root = tuple.getT2();
                    Map<String, List<RenjaKegiatanOpd>> grouped = list.stream()
                            .collect(Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaKegiatanOpd first = groupList.get(0);
                        String kegiatan = first.kodeKegiatan();
                        String indikatorName = first.kodeIndikator();
                        String targetName = first.kodeTarget();
                        for (PenetapanRenjaOpd.KegiatanPenetapanData k : safeList(root.kegiatans())) {
                            for (PenetapanRenjaOpd.IndikatorPenetapanData ind : safeList(k.indikators())) {
                                for (PenetapanRenjaOpd.TargetPenetapanData tgt : safeList(ind.targets())) {
                                    if (first.kodeTarget().equals(tgt.kodeTarget())) {
                                        kegiatan = k.kegiatan() != null ? k.kegiatan() : kegiatan;
                                        indikatorName = ind.indikator() != null ? ind.indikator() : indikatorName;
                                        targetName = tgt.target() != null ? String.valueOf(tgt.target()) : targetName;
                                        break;
                                    }
                                }
                            }
                        }
                        List<PeriodeRealisasi> periode = groupList.stream()
                                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()))
                                .toList();
                        Map<String, Double> listData = buildLaporanData(periode, jenisLaporan, bulan);
                        Double totalRealisasi = hitungTotalRealisasi(listData, jenisLaporan);
                        return new LaporanRealisasiRenjaKegiatanOpdResponse(
                                tahun, kodeOpd, kegiatan, indikatorName, targetName,
                                jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenjaSubKegiatanOpdResponse> getLaporanRealisasiSubKegiatan(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        Mono<PenetapanRenjaOpd.PenetapanRenjaOpdRoot> penetapanMono = fetchPenetapanOrEmpty(kodeOpd, tahun);
        return Mono.zip(targetSubKegiatanRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList(), penetapanMono)
                .flatMapMany(tuple -> {
                    List<RenjaSubKegiatanOpd> list = tuple.getT1();
                    PenetapanRenjaOpd.PenetapanRenjaOpdRoot root = tuple.getT2();
                    Map<String, List<RenjaSubKegiatanOpd>> grouped = list.stream()
                            .collect(Collectors.groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaSubKegiatanOpd first = groupList.get(0);
                        String subKegiatan = first.kodeSubkegiatan();
                        String indikatorName = first.kodeIndikator();
                        String targetName = first.kodeTarget();
                        for (PenetapanRenjaOpd.SubkegiatanPenetapanData s : safeList(root.subkegiatans())) {
                            for (PenetapanRenjaOpd.IndikatorPenetapanData ind : safeList(s.indikators())) {
                                for (PenetapanRenjaOpd.TargetPenetapanData tgt : safeList(ind.targets())) {
                                    if (first.kodeTarget().equals(tgt.kodeTarget())) {
                                        subKegiatan = s.subkegiatan() != null ? s.subkegiatan() : subKegiatan;
                                        indikatorName = ind.indikator() != null ? ind.indikator() : indikatorName;
                                        targetName = tgt.target() != null ? String.valueOf(tgt.target()) : targetName;
                                        break;
                                    }
                                }
                            }
                        }
                        List<PeriodeRealisasi> periode = groupList.stream()
                                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()))
                                .toList();
                        Map<String, Double> listData = buildLaporanData(periode, jenisLaporan, bulan);
                        Double totalRealisasi = hitungTotalRealisasi(listData, jenisLaporan);
                        return new LaporanRealisasiRenjaSubKegiatanOpdResponse(
                                tahun, kodeOpd, subKegiatan, indikatorName, targetName,
                                jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    private Mono<PenetapanRenjaOpd.PenetapanRenjaOpdRoot> fetchPenetapanOrEmpty(String kodeOpd, String tahun) {
        return penetapanClient.fetchRenjaOpd(kodeOpd, Integer.parseInt(tahun))
                .onErrorReturn(new PenetapanRenjaOpd.PenetapanRenjaOpdRoot(
                        kodeOpd, Integer.parseInt(tahun), null, List.of(), List.of(), List.of()));
    }

    private Map<String, Double> buildLaporanData(
            List<PeriodeRealisasi> list, JenisLaporan jenisLaporan, String bulan) {
        return switch (jenisLaporan) {
            case BULANAN -> {
                if (bulan == null || bulan.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                }
                double total = list.stream()
                        .filter(p -> bulan.equals(p.bulan()))
                        .filter(p -> p.realisasi() != null)
                        .mapToDouble(p -> p.realisasi().doubleValue())
                        .sum();
                yield Map.of(bulan, total);
            }
            case TRIWULAN -> hitungTriwulanKumulatif(list);
            case TAHUNAN -> hitungBulanKumulatif(list);
        };
    }

    private Double hitungTotalRealisasi(Map<String, Double> listData, JenisLaporan jenisLaporan) {
        if (jenisLaporan != JenisLaporan.TRIWULAN && jenisLaporan != JenisLaporan.TAHUNAN) {
            return null;
        }
        return listData.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // ========================================================================
    // Private - Fetch realisasi data and merge with penetapan
    // ========================================================================

    private Mono<RenjaOpdPenetapanResponse> fetchRealisasiAndMerge(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);

        Mono<Map<String, RealisasiData>> programRealisasiMap = targetProgramRepo.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahunStr, bulan)
                .collectMap(RenjaProgramOpd::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat(), t.buktiPendukung()));

        Mono<Map<String, RealisasiData>> kegiatanRealisasiMap = targetKegiatanRepo.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahunStr, bulan)
                .collectMap(RenjaKegiatanOpd::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat(), t.buktiPendukung()));

        Mono<List<RenjaSubKegiatanOpd>> subKegiatanListMono = targetSubKegiatanRepo.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahunStr, bulan)
                .collectList();

        return Mono.zip(programRealisasiMap, kegiatanRealisasiMap, subKegiatanListMono)
                .map(tuple -> {
                    Map<String, RealisasiData> progMap = tuple.getT1();
                    Map<String, RealisasiData> kegMap = tuple.getT2();
                    List<RenjaSubKegiatanOpd> subList = tuple.getT3();

                    Map<String, RealisasiData> subTargetMap = new HashMap<>();
                    Map<String, Double> subPaguMap = new HashMap<>();

                    for (RenjaSubKegiatanOpd item : subList) {
                        if (item.kodeTarget() != null) {
                            subTargetMap.put(item.kodeTarget(), new RealisasiData(
                                    item.realisasi() != null ? item.realisasi().doubleValue() : null,
                                    item.faktorPenunjang(), item.faktorPenghambat(), item.buktiPendukung()));
                        }
                        if (item.kodePagu() != null && item.realisasiPagu() != null) {
                            subPaguMap.put(item.kodePagu(), item.realisasiPagu().doubleValue());
                            if (item.kodeSubkegiatan() != null) {
                                subPaguMap.put(item.kodeSubkegiatan() + "|" + item.kodePagu(), item.realisasiPagu().doubleValue());
                            }
                        }
                    }

                    List<RenjaOpdPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                            .map(p -> mergeProgramWithRealisasi(p, progMap))
                            .toList();

                    List<RenjaOpdPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                            .map(k -> mergeKegiatanWithRealisasi(k, kegMap))
                            .toList();

                    List<RenjaOpdPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                            .map(s -> mergeSubKegiatanWithRealisasi(s, subTargetMap, subPaguMap))
                            .toList();

                    return new RenjaOpdPenetapanResponse(
                            kodeOpd, tahun, parseInteger(bulan),
                            programs, kegiatans, subkegiatans
                    );
                });
    }

    // ========================================================================
    // Private - Map penetapan response tanpa realisasi
    // ========================================================================

    private RenjaOpdPenetapanResponse mapWithoutRealisasi(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun
    ) {
        List<RenjaOpdPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                .map(this::toProgramPenetapan)
                .toList();

        List<RenjaOpdPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                .map(this::toKegiatanPenetapan)
                .toList();

        List<RenjaOpdPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                .map(this::toSubKegiatanPenetapan)
                .toList();

        return new RenjaOpdPenetapanResponse(kodeOpd, tahun, null, programs, kegiatans, subkegiatans);
    }

    // ========================================================================
    // Private - Entity to Response mapping
    // ========================================================================

    private RenjaOpdPenetapanResponse.ProgramPenetapan toProgramPenetapan(
            PenetapanRenjaOpd.ProgramPenetapanData p
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), p.isLocked(),
                indikators, mapPaguAnggaran(p.paguAnggaran())
        );
    }

    private RenjaOpdPenetapanResponse.KegiatanPenetapan toKegiatanPenetapan(
            PenetapanRenjaOpd.KegiatanPenetapanData k
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = k.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), k.isLocked(),
                indikators, mapPaguAnggaran(k.paguAnggaran())
        );
    }

    private RenjaOpdPenetapanResponse.SubkegiatanPenetapan toSubKegiatanPenetapan(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = s.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), s.isLocked(),
                indikators, mapPaguAnggaran(s.paguAnggaran())
        );
    }

    // ========================================================================
    // Private - Merge realisasi data ke penetapan response
    // ========================================================================

    private RenjaOpdPenetapanResponse.ProgramPenetapan mergeProgramWithRealisasi(
            PenetapanRenjaOpd.ProgramPenetapanData p,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> {
                                    RealisasiData data = realisasiMap.get(t.kodeTarget());
                                    Double realisasi = data != null ? data.realisasi() : null;
                                    var capaianResult = hitungCapaian(realisasi, t.target());
                                    return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                            t.id(), t.kodeTarget(), t.tahun(), null,
                                            t.target(), realisasi, t.satuan(),
                                            capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                            data != null ? data.faktorPenunjang() : null,
                                            data != null ? data.faktorPenghambat() : null,
                                            data != null ? data.buktiPendukung() : null
                                    );
                                })
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), p.isLocked(),
                indikators, mapPaguAnggaran(p.paguAnggaran())
        );
    }

    private RenjaOpdPenetapanResponse.KegiatanPenetapan mergeKegiatanWithRealisasi(
            PenetapanRenjaOpd.KegiatanPenetapanData k,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = k.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> {
                                    RealisasiData data = realisasiMap.get(t.kodeTarget());
                                    Double realisasi = data != null ? data.realisasi() : null;
                                    var capaianResult = hitungCapaian(realisasi, t.target());
                                    return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                            t.id(), t.kodeTarget(), t.tahun(), null,
                                            t.target(), realisasi, t.satuan(),
                                            capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                            data != null ? data.faktorPenunjang() : null,
                                            data != null ? data.faktorPenghambat() : null,
                                            data != null ? data.buktiPendukung() : null
                                    );
                                })
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), k.isLocked(),
                indikators, mapPaguAnggaran(k.paguAnggaran())
        );
    }

    private RenjaOpdPenetapanResponse.SubkegiatanPenetapan mergeSubKegiatanWithRealisasi(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s,
            Map<String, RealisasiData> realisasiMap,
            Map<String, Double> paguRealisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = s.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> {
                                    RealisasiData data = realisasiMap.get(t.kodeTarget());
                                    Double realisasi = data != null ? data.realisasi() : null;
                                    var capaianResult = hitungCapaian(realisasi, t.target());
                                    return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                            t.id(), t.kodeTarget(), t.tahun(), null,
                                            t.target(), realisasi, t.satuan(),
                                            capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                            data != null ? data.faktorPenunjang() : null,
                                            data != null ? data.faktorPenghambat() : null,
                                            data != null ? data.buktiPendukung() : null
                                    );
                                })
                                .toList()
                ))
                .toList();

        List<RenjaOpdPenetapanResponse.PaguAnggaranPenetapan> paguAnggaran = safeList(s.paguAnggaran()).stream()
                .map(p -> {
                    Double realisasiPagu = paguRealisasiMap.get(s.kodeSubkegiatan() + "|" + p.kodePagu());
                    if (realisasiPagu == null) {
                        realisasiPagu = paguRealisasiMap.get(p.kodePagu());
                    }
                    var capaianResult = hitungCapaian(realisasiPagu, p.pagu());
                    return new RenjaOpdPenetapanResponse.PaguAnggaranPenetapan(
                            p.id(), p.kodePagu(), p.pagu(), p.jenisPagu(),
                            realisasiPagu, capaianResult.capaian(), capaianResult.keteranganCapaian()
                    );
                })
                .toList();

        return new RenjaOpdPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), s.isLocked(),
                indikators, paguAnggaran
        );
    }

    // ========================================================================
    // Private - Helpers sementara karena belum ada endpoint di renja individu
    // ========================================================================

    static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (realisasi == null || target == null || target == 0) {
            return new CapaianResult(null, null);
        }
        double calculated = realisasi / target * 100;
        String keterangan = null;
        if (calculated > 100) {
            keterangan = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculated) + ")";
        }
        return new CapaianResult(Math.min(calculated, 100), keterangan);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private List<RenjaOpdPenetapanResponse.PaguAnggaranPenetapan> mapPaguAnggaran(List<PenetapanRenjaOpd.PaguAnggaranData> paguList) {
        return safeList(paguList).stream()
                .map(p -> new RenjaOpdPenetapanResponse.PaguAnggaranPenetapan(
                        p.id(), p.kodePagu(), p.pagu(), p.jenisPagu()
                ))
                .toList();
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    public Mono<RenjaProgramOpd> uploadBuktiPendukungProgram(Long id, FilePart file) {
        return targetProgramRepo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program OPD tidak ditemukan")))
                .flatMap(existing -> uploadFile(file).flatMap(filePath -> {
                    RenjaProgramOpd updated = new RenjaProgramOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeProgram(), existing.kodeIndikator(), existing.kodeTarget(), existing.kodePagu(),
                            existing.realisasi(), existing.jenisRealisasi(), existing.faktorPenunjang(), existing.faktorPenghambat(),
                            filePath, existing.createdDate(), existing.lastModifiedDate(), existing.createdBy(), existing.lastModifiedBy()
                    );
                    return targetProgramRepo.save(updated);
                }));
    }

    public Mono<RenjaKegiatanOpd> uploadBuktiPendukungKegiatan(Long id, FilePart file) {
        return targetKegiatanRepo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan OPD tidak ditemukan")))
                .flatMap(existing -> uploadFile(file).flatMap(filePath -> {
                    RenjaKegiatanOpd updated = new RenjaKegiatanOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeKegiatan(), existing.kodeIndikator(), existing.kodeTarget(), existing.kodePagu(),
                            existing.realisasi(), existing.jenisRealisasi(), existing.faktorPenunjang(), existing.faktorPenghambat(),
                            filePath, existing.createdDate(), existing.lastModifiedDate(), existing.createdBy(), existing.lastModifiedBy()
                    );
                    return targetKegiatanRepo.save(updated);
                }));
    }

    public Mono<RenjaSubKegiatanOpd> uploadBuktiPendukungSubKegiatan(Long id, FilePart file) {
        return targetSubKegiatanRepo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan OPD tidak ditemukan")))
                .flatMap(existing -> uploadFile(file).flatMap(filePath -> {
                    RenjaSubKegiatanOpd updated = new RenjaSubKegiatanOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeSubkegiatan(), existing.kodeIndikator(), existing.kodeTarget(), existing.kodePagu(),
                            existing.realisasi(), existing.realisasiPagu(), existing.jenisRealisasi(), existing.faktorPenunjang(), existing.faktorPenghambat(),
                            filePath, existing.createdDate(), existing.lastModifiedDate(), existing.createdBy(), existing.lastModifiedBy()
                    );
                    return targetSubKegiatanRepo.save(updated);
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

    private Map<String, Double> hitungTriwulanKumulatif(List<PeriodeRealisasi> groupList) {
        Map<String, Double> triwulanMap = new TreeMap<>();
        for (int triwulan = 1; triwulan <= 4; triwulan++) {
            int awalBulan = (triwulan - 1) * 3 + 1;
            int akhirBulan = triwulan * 3;
            boolean adaData = groupList.stream()
                    .filter(p -> p.realisasi() != null)
                    .anyMatch(p -> {
                        int b = Integer.parseInt(p.bulan());
                        return b >= awalBulan && b <= akhirBulan;
                    });
            if (adaData) {
                double kumulatif = groupList.stream()
                        .filter(p -> p.realisasi() != null)
                        .filter(p -> Integer.parseInt(p.bulan()) <= akhirBulan)
                        .mapToDouble(p -> p.realisasi().doubleValue())
                        .sum();
                triwulanMap.put(String.valueOf(triwulan), kumulatif);
            }
        }
        return triwulanMap;
    }

    private Map<String, Double> hitungBulanKumulatif(List<PeriodeRealisasi> groupList) {
        TreeMap<Integer, Double> bulanMap = new TreeMap<>();
        for (PeriodeRealisasi p : groupList) {
            if (p.realisasi() == null) continue;
            int noBulan = Integer.parseInt(p.bulan());
            bulanMap.merge(noBulan, p.realisasi().doubleValue(), Double::sum);
        }
        Map<String, Double> result = new TreeMap<>();
        for (var entry : bulanMap.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }
}
