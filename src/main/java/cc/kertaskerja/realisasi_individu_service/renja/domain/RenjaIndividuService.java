package cc.kertaskerja.realisasi_individu_service.renja.domain;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.LaporanRealisasiRenjaKegiatanIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenghambatTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenunjangTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.LaporanRealisasiRenjaProgramIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.LaporanRealisasiRenjaSubKegiatanIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanResponse;
import cc.kertaskerja.integration.penetapan.PenetapanRenjaIndividuClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.web.PenetapanRenjaIndividuResponse;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.integration.kepegawaian.PegawaiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RenjaIndividuService {
    private static final Logger log = LoggerFactory.getLogger(RenjaIndividuService.class);
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final RenjaProgramIndividuRepository programRepo;
    private final RenjaKegiatanIndividuRepository kegiatanRepo;
    private final RenjaSubKegiatanIndividuRepository subKegiatanRepo;
    private final UploadClient uploadClient;
    private final PegawaiClient pegawaiClient;
    private final PenetapanRenjaIndividuClient penetapanClient;

    record CapaianResult(Double capaian, String keteranganCapaian) {
    }

    record RealisasiData(
            Double realisasi,
            Double realisasiPagu,
            String faktorPenunjang,
            String faktorPenghambat,
            String buktiPendukung,
            String keteranganBuktiPendukung,
            String jenisRealisasi
    ) {}

    public RenjaIndividuService(
            RenjaProgramIndividuRepository programRepo,
            RenjaKegiatanIndividuRepository kegiatanRepo,
            RenjaSubKegiatanIndividuRepository subKegiatanRepo,
            UploadClient uploadClient,
            PegawaiClient pegawaiClient,
            PenetapanRenjaIndividuClient penetapanClient) {
        this.programRepo = programRepo;
        this.kegiatanRepo = kegiatanRepo;
        this.subKegiatanRepo = subKegiatanRepo;
        this.uploadClient = uploadClient;
        this.pegawaiClient = pegawaiClient;
        this.penetapanClient = penetapanClient;
    }

    @Transactional
    public Mono<RenjaIndividuProgramResponse> submitProgram(RenjaIndividuProgramRequest req) {
        return penetapanClient.fetchRenjaIndividu(req.nip(), req.kodeOpd(), Integer.parseInt(req.tahun()))
                .flatMap(penetapanData -> upsertProgram(req)
                        .map(saved -> enrichProgramWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan untuk nip={}, kodeOpd={}, tahun={}: {}",
                            req.nip(), req.kodeOpd(), req.tahun(), e.getMessage());
                    return upsertProgram(req).flatMap(this::enrichProgramResponse);
                });
    }

    @Transactional
    public Mono<RenjaIndividuKegiatanResponse> submitKegiatan(RenjaIndividuKegiatanRequest req) {
        return penetapanClient.fetchRenjaIndividu(req.nip(), req.kodeOpd(), Integer.parseInt(req.tahun()))
                .flatMap(penetapanData -> upsertKegiatan(req)
                        .map(saved -> enrichKegiatanWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan untuk nip={}, kodeOpd={}, tahun={}: {}",
                            req.nip(), req.kodeOpd(), req.tahun(), e.getMessage());
                    return upsertKegiatan(req).flatMap(this::enrichKegiatanResponse);
                });
    }

    @Transactional
    public Mono<RenjaIndividuSubKegiatanResponse> submitSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        return penetapanClient.fetchRenjaIndividu(req.nip(), req.kodeOpd(), Integer.parseInt(req.tahun()))
                .flatMap(penetapanData -> upsertSubKegiatan(req)
                        .map(saved -> enrichSubKegiatanWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan untuk nip={}, kodeOpd={}, tahun={}: {}",
                            req.nip(), req.kodeOpd(), req.tahun(), e.getMessage());
                    return upsertSubKegiatan(req).flatMap(this::enrichSubKegiatanResponse);
                });
    }

    public Mono<RenjaProgramIndividu> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaProgramIndividu> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaKegiatanIndividu> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaKegiatanIndividu> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenunjangSubKegiatan(
            FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenghambatSubKegiatan(
            FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Flux<RenjaIndividuProgramResponse> getProgramByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return penetapanClient.fetchRenjaIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .flatMapMany(penetapanData ->
                        programRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                                .map(saved -> enrichProgramWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan saat search program nip={}, kodeOpd={}, tahun={}: {}",
                            nip, kodeOpd, tahun, e.getMessage());
                    return programRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                            .flatMap(this::enrichProgramResponse);
                });
    }

    public Flux<RenjaIndividuKegiatanResponse> getKegiatanByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return penetapanClient.fetchRenjaIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .flatMapMany(penetapanData ->
                        kegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                                .map(saved -> enrichKegiatanWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan saat search kegiatan nip={}, kodeOpd={}, tahun={}: {}",
                            nip, kodeOpd, tahun, e.getMessage());
                    return kegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                            .flatMap(this::enrichKegiatanResponse);
                });
    }

    public Flux<RenjaIndividuSubKegiatanResponse> getSubKegiatanByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return penetapanClient.fetchRenjaIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .flatMapMany(penetapanData ->
                        subKegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                                .map(saved -> enrichSubKegiatanWithPenetapan(saved, penetapanData)))
                .onErrorResume(e -> {
                    log.warn("Gagal menghubungi penetapan saat search subkegiatan nip={}, kodeOpd={}, tahun={}: {}",
                            nip, kodeOpd, tahun, e.getMessage());
                    return subKegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                            .flatMap(this::enrichSubKegiatanResponse);
                });
    }

    public Flux<RenjaIndividuProgramResponse> searchProgram(
            String kodeOpd, String tahun, String bulan, String levelRole, String nip) {
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
                    
                    return getProgramByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
                });
    }

    public Flux<RenjaIndividuKegiatanResponse> searchKegiatan(
            String kodeOpd, String tahun, String bulan, String levelRole, String nip) {
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
                    
                    return getKegiatanByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
                });
    }

    public Flux<RenjaIndividuSubKegiatanResponse> searchSubKegiatan(
            String kodeOpd, String tahun, String bulan, String levelRole, String nip) {
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
                    
                    return getSubKegiatanByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
                });
    }

    public Flux<LaporanRealisasiRenjaProgramIndividuResponse> getLaporanRealisasiProgram(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return programRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, java.util.List<RenjaProgramIndividu>> grouped = list.stream()
                            .collect(java.util.stream.Collectors
                                    .groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaProgramIndividu first = groupList.get(0);
                        Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                item -> item.realisasi() != null ? item.realisasi().doubleValue() : null);
                        Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                || jenisLaporan == JenisLaporan.TAHUNAN)
                                        ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                        : null;
                        return new LaporanRealisasiRenjaProgramIndividuResponse(tahun, kodeOpd, nip, null,
                                null, jenisLaporan, listData,
                                totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenjaKegiatanIndividuResponse> getLaporanRealisasiKegiatan(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return kegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, java.util.List<RenjaKegiatanIndividu>> grouped = list.stream()
                            .collect(java.util.stream.Collectors
                                    .groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaKegiatanIndividu first = groupList.get(0);
                        Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                item -> item.realisasi() != null ? item.realisasi().doubleValue() : null);
                        Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                || jenisLaporan == JenisLaporan.TAHUNAN)
                                        ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                        : null;
                        return new LaporanRealisasiRenjaKegiatanIndividuResponse(tahun, kodeOpd, nip, null,
                                null, jenisLaporan, listData,
                                totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenjaSubKegiatanIndividuResponse> getLaporanRealisasiSubKegiatan(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return subKegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, java.util.List<RenjaSubKegiatanIndividu>> grouped = list.stream()
                            .collect(java.util.stream.Collectors
                                    .groupingBy(t -> t.kodeIndikator() + "|" + t.kodeTarget()));
                    return Flux.fromIterable(grouped.values()).map(groupList -> {
                        RenjaSubKegiatanIndividu first = groupList.get(0);
                        Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                item -> item.realisasiTarget() != null ? item.realisasiTarget().doubleValue() : null);
                        Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                || jenisLaporan == JenisLaporan.TAHUNAN)
                                        ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                        : null;
                        return new LaporanRealisasiRenjaSubKegiatanIndividuResponse(tahun, kodeOpd, nip,
                                null,
                                null,
                                jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public Flux<LaporanRealisasiRenjaProgramIndividuResponse> getLaporanRealisasiProgramByOpd(
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
                    
                    return programRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                            .collectList()
                            .flatMapMany(list -> {
                                Map<String, java.util.List<RenjaProgramIndividu>> grouped = list.stream()
                                        .collect(java.util.stream.Collectors
                                                .groupingBy(t -> t.nip() + "|" + t.kodeIndikator() + "|" + t.kodeTarget()));
                                return Flux.fromIterable(grouped.values()).map(groupList -> {
                                    RenjaProgramIndividu first = groupList.get(0);
                                    Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                            item -> item.realisasi() != null ? item.realisasi().doubleValue() : null);
                                    Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                            || jenisLaporan == JenisLaporan.TAHUNAN)
                                                    ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                                    : null;
                                    return new LaporanRealisasiRenjaProgramIndividuResponse(tahun, kodeOpd, first.nip(),
                                            null, null,
                                            jenisLaporan, listData, totalRealisasi);
                                });
                            });
                });
    }

    public Flux<LaporanRealisasiRenjaKegiatanIndividuResponse> getLaporanRealisasiKegiatanByOpd(
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
                    
                    return kegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                            .collectList()
                            .flatMapMany(list -> {
                                Map<String, java.util.List<RenjaKegiatanIndividu>> grouped = list.stream()
                                        .collect(java.util.stream.Collectors
                                                .groupingBy(t -> t.nip() + "|" + t.kodeIndikator() + "|" + t.kodeTarget()));
                                return Flux.fromIterable(grouped.values()).map(groupList -> {
                                    RenjaKegiatanIndividu first = groupList.get(0);
                                    Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                            item -> item.realisasi() != null ? item.realisasi().doubleValue() : null);
                                    Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                            || jenisLaporan == JenisLaporan.TAHUNAN)
                                                    ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                                    : null;
                                    return new LaporanRealisasiRenjaKegiatanIndividuResponse(tahun, kodeOpd, first.nip(),
                                            null, null,
                                            jenisLaporan, listData, totalRealisasi);
                                });
                            });
                });
    }

    public Flux<LaporanRealisasiRenjaSubKegiatanIndividuResponse> getLaporanRealisasiSubKegiatanByOpd(
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
                    
                    return subKegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                            .collectList()
                            .flatMapMany(list -> {
                                Map<String, java.util.List<RenjaSubKegiatanIndividu>> grouped = list.stream()
                                        .collect(java.util.stream.Collectors
                                                .groupingBy(t -> t.nip() + "|" + t.kodeIndikator() + "|" + t.kodeTarget()));
                                return Flux.fromIterable(grouped.values()).map(groupList -> {
                                    RenjaSubKegiatanIndividu first = groupList.get(0);
                                    Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan,
                                            item -> item.realisasiTarget() != null ? item.realisasiTarget().doubleValue() : null);
                                    Double totalRealisasi = (jenisLaporan == JenisLaporan.TRIWULAN
                                            || jenisLaporan == JenisLaporan.TAHUNAN)
                                                    ? listData.values().stream().mapToDouble(Double::doubleValue).sum()
                                                    : null;
                                    return new LaporanRealisasiRenjaSubKegiatanIndividuResponse(tahun, kodeOpd, first.nip(),
                                            null,
                                            null,
                                            jenisLaporan, listData, totalRealisasi);
                                });
                            });
                });
    }

    private Mono<RenjaProgramIndividu> upsertProgram(RenjaIndividuProgramRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> programRepo.save(new RenjaProgramIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeProgram(),
                        existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(),
                        BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        req.buktiPendukung() != null && !req.buktiPendukung().isBlank() ? req.buktiPendukung() : existing.buktiPendukung(),
                        req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                        existing.createdDate(), null, existing.createdBy(), null)))
                .switchIfEmpty(Mono.defer(() -> programRepo.save(new RenjaProgramIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeProgram(),
                        req.kodeIndikator(),
                        req.kodeTarget(), kodePagu,
                        BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "", req.buktiPendukung(), req.keteranganBuktiPendukung(),
                        null, null, null, null))));
    }

    private Mono<RenjaKegiatanIndividu> upsertKegiatan(RenjaIndividuKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeKegiatan(),
                        existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(),
                        BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        req.buktiPendukung() != null && !req.buktiPendukung().isBlank() ? req.buktiPendukung() : existing.buktiPendukung(),
                        req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                        existing.createdDate(), null, existing.createdBy(), null)))
                .switchIfEmpty(Mono.defer(() -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeKegiatan(),
                        req.kodeIndikator(),
                        req.kodeTarget(), kodePagu,
                        BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "", req.buktiPendukung(), req.keteranganBuktiPendukung(),
                        null, null, null, null))));
    }

    private Mono<RenjaSubKegiatanIndividu> upsertSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> subKegiatanRepo.save(new RenjaSubKegiatanIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeSubKegiatan(),
                        existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(),
                        BigDecimal.valueOf(req.realisasiTarget()), BigDecimal.valueOf(req.realisasiPagu()),
                        jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        req.buktiPendukung() != null && !req.buktiPendukung().isBlank() ? req.buktiPendukung() : existing.buktiPendukung(),
                        req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                        existing.createdDate(), null, existing.createdBy(), null)))
                .switchIfEmpty(Mono.defer(() -> subKegiatanRepo.save(new RenjaSubKegiatanIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeSubKegiatan(),
                        req.kodeIndikator(),
                        req.kodeTarget(), kodePagu,
                        BigDecimal.valueOf(req.realisasiTarget()), BigDecimal.valueOf(req.realisasiPagu()),
                        jenisRealisasi,
                        "", "", req.buktiPendukung(), req.keteranganBuktiPendukung(),
                        null, null, null, null))));
    }

    private RenjaIndividuProgramResponse enrichProgramWithPenetapan(
            RenjaProgramIndividu saved,
            PenetapanRenjaIndividu.RenjaIndividuData data
    ) {
        if (data == null || data.renjas() == null) {
            return buildFallbackProgramResponse(saved);
        }
        for (PenetapanRenjaIndividu.RenjaData r : safeList(data.renjas())) {
            for (PenetapanRenjaIndividu.IndikatorPenetapanData ind : safeList(r.indikatorPrograms())) {
                for (PenetapanRenjaIndividu.TargetPenetapanData tgt : safeList(ind.targets())) {
                    if (saved.kodeTarget().equals(tgt.kodeTarget())) {
                        Double realisasi = saved.realisasi() != null ? saved.realisasi().doubleValue() : null;
                        var capaianResult = hitungCapaian(realisasi, tgt.target());
                        return new RenjaIndividuProgramResponse(
                                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                                saved.kodeProgram(), r.namaProgram(), saved.kodeIndikator(),
                                ind.indikator(), saved.kodeTarget(),
                                saved.kodePagu(), r.paguProgram() != null ? r.paguProgram().doubleValue() : null,
                                tgt.target(),
                                realisasi,
                                saved.jenisRealisasi(),
                                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                                saved.createdBy(), saved.lastModifiedBy()
                        );
                    }
                }
            }
        }
        return buildFallbackProgramResponse(saved);
    }

    private Mono<RenjaIndividuProgramResponse> enrichProgramResponse(RenjaProgramIndividu saved) {
        return Mono.just(buildFallbackProgramResponse(saved));
    }

    private RenjaIndividuProgramResponse buildFallbackProgramResponse(RenjaProgramIndividu saved) {
        return new RenjaIndividuProgramResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeProgram(), null, saved.kodeIndikator(),
                null, saved.kodeTarget(),
                saved.kodePagu(), null,
                null,
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                saved.jenisRealisasi(),
                null, null,
                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                saved.createdBy(), saved.lastModifiedBy());
    }

    private RenjaIndividuKegiatanResponse enrichKegiatanWithPenetapan(
            RenjaKegiatanIndividu saved,
            PenetapanRenjaIndividu.RenjaIndividuData data
    ) {
        if (data == null || data.renjas() == null) {
            return buildFallbackKegiatanResponse(saved);
        }
        for (PenetapanRenjaIndividu.RenjaData r : safeList(data.renjas())) {
            for (PenetapanRenjaIndividu.IndikatorPenetapanData ind : safeList(r.indikatorKegiatans())) {
                for (PenetapanRenjaIndividu.TargetPenetapanData tgt : safeList(ind.targets())) {
                    if (saved.kodeTarget().equals(tgt.kodeTarget())) {
                        Double realisasi = saved.realisasi() != null ? saved.realisasi().doubleValue() : null;
                        var capaianResult = hitungCapaian(realisasi, tgt.target());
                        return new RenjaIndividuKegiatanResponse(
                                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                                saved.kodeKegiatan(), r.namaKegiatan(), saved.kodeIndikator(),
                                ind.indikator(), saved.kodeTarget(),
                                saved.kodePagu(), r.paguKegiatan() != null ? r.paguKegiatan().doubleValue() : null,
                                tgt.target(),
                                realisasi,
                                saved.jenisRealisasi(),
                                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                                saved.createdBy(), saved.lastModifiedBy()
                        );
                    }
                }
            }
        }
        return buildFallbackKegiatanResponse(saved);
    }

    private Mono<RenjaIndividuKegiatanResponse> enrichKegiatanResponse(RenjaKegiatanIndividu saved) {
        return Mono.just(buildFallbackKegiatanResponse(saved));
    }

    private RenjaIndividuKegiatanResponse buildFallbackKegiatanResponse(RenjaKegiatanIndividu saved) {
        return new RenjaIndividuKegiatanResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeKegiatan(), null, saved.kodeIndikator(),
                null, saved.kodeTarget(),
                saved.kodePagu(), null,
                null,
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                saved.jenisRealisasi(),
                null, null,
                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                saved.createdBy(), saved.lastModifiedBy());
    }

    private RenjaIndividuSubKegiatanResponse enrichSubKegiatanWithPenetapan(
            RenjaSubKegiatanIndividu saved,
            PenetapanRenjaIndividu.RenjaIndividuData data
    ) {
        if (data == null || data.renjas() == null) {
            return buildFallbackSubKegiatanResponse(saved);
        }
        for (PenetapanRenjaIndividu.RenjaData r : safeList(data.renjas())) {
            for (PenetapanRenjaIndividu.IndikatorPenetapanData ind : safeList(r.indikatorSubkegiatans())) {
                for (PenetapanRenjaIndividu.TargetPenetapanData tgt : safeList(ind.targets())) {
                    if (saved.kodeTarget().equals(tgt.kodeTarget())) {
                        Double realisasiTarget = saved.realisasiTarget() != null ? saved.realisasiTarget().doubleValue() : null;
                        Double realisasiPagu = saved.realisasiPagu() != null ? saved.realisasiPagu().doubleValue() : null;
                        Double targetPagu = r.paguSubkegiatan() != null ? r.paguSubkegiatan().doubleValue() : null;
                        var capaianTargetResult = hitungCapaian(realisasiTarget, tgt.target());
                        var capaianPaguResult = hitungCapaian(realisasiPagu, targetPagu);
                        return new RenjaIndividuSubKegiatanResponse(
                                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                                saved.kodeSubKegiatan(), r.namaSubkegiatan(), saved.kodeIndikator(),
                                ind.indikator(), saved.kodeTarget(),
                                saved.kodePagu(), targetPagu,
                                tgt.target(),
                                realisasiTarget,
                                realisasiPagu,
                                saved.jenisRealisasi(),
                                capaianTargetResult.capaian(), capaianTargetResult.keteranganCapaian(),
                                capaianPaguResult.capaian(), capaianPaguResult.keteranganCapaian(),
                                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                                saved.createdBy(), saved.lastModifiedBy()
                        );
                    }
                }
            }
        }
        return buildFallbackSubKegiatanResponse(saved);
    }

    private Mono<RenjaIndividuSubKegiatanResponse> enrichSubKegiatanResponse(RenjaSubKegiatanIndividu saved) {
        return Mono.just(buildFallbackSubKegiatanResponse(saved));
    }

    private RenjaIndividuSubKegiatanResponse buildFallbackSubKegiatanResponse(RenjaSubKegiatanIndividu saved) {
        return new RenjaIndividuSubKegiatanResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeSubKegiatan(), null, saved.kodeIndikator(),
                null, saved.kodeTarget(),
                saved.kodePagu(), null,
                null,
                saved.realisasiTarget() != null ? saved.realisasiTarget().doubleValue() : null,
                saved.realisasiPagu() != null ? saved.realisasiPagu().doubleValue() : null,
                saved.jenisRealisasi(),
                null, null,
                null, null,
                saved.faktorPenunjang(), saved.faktorPenghambat(), saved.buktiPendukung(),
                saved.createdBy(), saved.lastModifiedBy());
    }

    // hitung capaian ditaruh di service agar lebih simple karena di renja individu
    // ada 3 domain
    static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (target == null || target == 0 || realisasi == null || realisasi == 0) {
            return new CapaianResult(0.0, null);
        }
        double calculated = realisasi / target * 100;
        String keterangan = null;
        if (calculated > 100) {
            keterangan = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculated) + ")";
            // nilai default capaian ketika lebih dari 100% menjadi 100%
            calculated = 100.0;
        }
        return new CapaianResult(calculated, keterangan);
    }

    private <T> Map<String, Double> buildLaporanData(
            java.util.List<T> list,
            JenisLaporan jenisLaporan,
            String bulan,
            Function<T, Double> nilaiExtractor) {
        return switch (jenisLaporan) {
            case BULANAN -> {
                if (bulan == null || bulan.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Parameter bulan wajib diisi untuk laporan BULANAN");
                }
                double total = list.stream()
                        .filter(item -> bulan.equals(extractBulan(item)))
                        .map(nilaiExtractor)
                        .filter(nilai -> nilai != null)
                        .mapToDouble(Double::doubleValue)
                        .sum();
                yield Map.of(bulan, total);
            }
            case TRIWULAN -> {
                Map<String, Double> triwulanMap = new HashMap<>();
                for (int i = 1; i <= 4; i++) {
                    triwulanMap.put(String.valueOf(i), 0.0);
                }
                for (T item : list) {
                    Double nilai = nilaiExtractor.apply(item);
                    if (nilai == null) {
                        continue;
                    }
                    int noBulan = Integer.parseInt(extractBulan(item));
                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                    triwulanMap.merge(triwulan, nilai, Double::sum);
                }
                yield triwulanMap;
            }
            case TAHUNAN -> {
                Map<String, Double> bulanMap = new HashMap<>();
                for (int i = 1; i <= 12; i++) {
                    bulanMap.put(String.valueOf(i), 0.0);
                }
                for (T item : list) {
                    Double nilai = nilaiExtractor.apply(item);
                    if (nilai == null) {
                        continue;
                    }
                    bulanMap.merge(extractBulan(item), nilai, Double::sum);
                }
                yield bulanMap;
            }
        };
    }

    private String extractBulan(Object item) {
        if (item instanceof RenjaProgramIndividu program) {
            return program.bulan();
        }
        if (item instanceof RenjaKegiatanIndividu kegiatan) {
            return kegiatan.bulan();
        }
        if (item instanceof RenjaSubKegiatanIndividu subKegiatan) {
            return subKegiatan.bulan();
        }
        throw new IllegalArgumentException("Tipe data laporan tidak didukung");
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }

    // ========================================================================
    // Penetapan Integration
    // ========================================================================

    public Mono<PenetapanRenjaIndividuResponse> getPenetapanByNip(String nip, String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRenjaIndividu(nip, kodeOpd, tahun)
                .flatMap(data -> {
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(mapWithoutRealisasi(data, nip, kodeOpd, tahun));
                    }
                    return fetchRealisasiAndMerge(data, nip, kodeOpd, tahun, bulan);
                })
                .defaultIfEmpty(new PenetapanRenjaIndividuResponse(
                        nip, null, kodeOpd, tahun, parseInteger(bulan), List.of()
                ));
    }

    public Mono<String> syncPenetapanRenjaIndividu(String nip, String kodeOpd, int tahun) {
        return penetapanClient.syncRenjaIndividu(nip, kodeOpd, tahun);
    }

    private Mono<PenetapanRenjaIndividuResponse> fetchRealisasiAndMerge(
            PenetapanRenjaIndividu.RenjaIndividuData data,
            String nip, String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);

        Mono<Map<String, RealisasiData>> programRealisasiMap = programRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahunStr, bulan)
                .collectMap(RenjaProgramIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                null,
                                t.faktorPenunjang(), t.faktorPenghambat(), t.buktiPendukung(),
                                t.keteranganBuktiPendukung(), t.jenisRealisasi()));

        Mono<Map<String, RealisasiData>> kegiatanRealisasiMap = kegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahunStr, bulan)
                .collectMap(RenjaKegiatanIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                null,
                                t.faktorPenunjang(), t.faktorPenghambat(), t.buktiPendukung(),
                                t.keteranganBuktiPendukung(), t.jenisRealisasi()));

        Mono<Map<String, RealisasiData>> subKegiatanRealisasiMap = subKegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahunStr, bulan)
                .collectMap(RenjaSubKegiatanIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasiTarget() != null ? t.realisasiTarget().doubleValue() : null,
                                t.realisasiPagu() != null ? t.realisasiPagu().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat(), t.buktiPendukung(),
                                t.keteranganBuktiPendukung(), t.jenisRealisasi()));

        return Mono.zip(programRealisasiMap, kegiatanRealisasiMap, subKegiatanRealisasiMap)
                .map(tuple -> {
                    Map<String, RealisasiData> progMap = tuple.getT1();
                    Map<String, RealisasiData> kegMap = tuple.getT2();
                    Map<String, RealisasiData> subMap = tuple.getT3();

                    List<PenetapanRenjaIndividuResponse.RenjaPenetapanResponse> renjas = safeList(data.renjas()).stream()
                            .map(r -> mergeRenjaWithRealisasi(r, progMap, kegMap, subMap))
                            .toList();

                    return new PenetapanRenjaIndividuResponse(
                            data.pegawaiId() != null ? data.pegawaiId() : nip,
                            data.nama(),
                            data.kodeOpd() != null ? data.kodeOpd() : kodeOpd,
                            data.tahunAktif() != null ? data.tahunAktif() : tahun,
                            parseInteger(bulan),
                            renjas
                    );
                });
    }

    private PenetapanRenjaIndividuResponse mapWithoutRealisasi(
            PenetapanRenjaIndividu.RenjaIndividuData data,
            String nip, String kodeOpd, int tahun
    ) {
        List<PenetapanRenjaIndividuResponse.RenjaPenetapanResponse> renjas = safeList(data.renjas()).stream()
                .map(r -> mergeRenjaWithRealisasi(r, Map.of(), Map.of(), Map.of()))
                .toList();

        return new PenetapanRenjaIndividuResponse(
                data.pegawaiId() != null ? data.pegawaiId() : nip,
                data.nama(),
                data.kodeOpd() != null ? data.kodeOpd() : kodeOpd,
                data.tahunAktif() != null ? data.tahunAktif() : tahun,
                null,
                renjas
        );
    }

    private PenetapanRenjaIndividuResponse.RenjaPenetapanResponse mergeRenjaWithRealisasi(
            PenetapanRenjaIndividu.RenjaData r,
            Map<String, RealisasiData> progMap,
            Map<String, RealisasiData> kegMap,
            Map<String, RealisasiData> subMap
    ) {
        List<PenetapanRenjaIndividuResponse.IndikatorPenetapanResponse> indProg = safeList(r.indikatorPrograms()).stream()
                .map(i -> mergeIndikatorWithRealisasi(i, progMap))
                .toList();

        List<PenetapanRenjaIndividuResponse.IndikatorPenetapanResponse> indKeg = safeList(r.indikatorKegiatans()).stream()
                .map(i -> mergeIndikatorWithRealisasi(i, kegMap))
                .toList();

        List<PenetapanRenjaIndividuResponse.IndikatorPenetapanResponse> indSub = safeList(r.indikatorSubkegiatans()).stream()
                .map(i -> mergeIndikatorWithRealisasi(i, subMap))
                .toList();

        return new PenetapanRenjaIndividuResponse.RenjaPenetapanResponse(
                r.id(), r.kodePk(), r.levelPk(), r.pegawaiId(), r.namaPegawai(),
                r.kodeProgram(), r.namaProgram(), r.kodePaguProgram(), r.paguProgram(), indProg,
                r.kodeKegiatan(), r.namaKegiatan(), r.kodePaguKegiatan(), r.paguKegiatan(), indKeg,
                r.kodeSubkegiatan(), r.namaSubkegiatan(), r.kodePaguSubkegiatan(), r.paguSubkegiatan(), indSub
        );
    }

    private PenetapanRenjaIndividuResponse.IndikatorPenetapanResponse mergeIndikatorWithRealisasi(
            PenetapanRenjaIndividu.IndikatorPenetapanData i,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<PenetapanRenjaIndividuResponse.TargetPenetapanResponse> targets = safeList(i.targets()).stream()
                .map(t -> mergeTargetWithRealisasi(t, realisasiMap))
                .toList();

        return new PenetapanRenjaIndividuResponse.IndikatorPenetapanResponse(
                i.id(), i.kodeIndikator(), i.indikator(), targets
        );
    }

    private PenetapanRenjaIndividuResponse.TargetPenetapanResponse mergeTargetWithRealisasi(
            PenetapanRenjaIndividu.TargetPenetapanData t,
            Map<String, RealisasiData> realisasiMap
    ) {
        RealisasiData data = realisasiMap.get(t.kodeTarget());
        // realisasiTarget = nilai realisasi terhadap target indikator
        Double realisasiTarget = data != null ? data.realisasi() : null;
        Double realisasiPagu = data != null ? data.realisasiPagu() : null;
        // capaianTarget = realisasiTarget / target(penetapan) * 100
        var capaianTargetResult = hitungCapaian(realisasiTarget, t.target());
        // capaianPagu = realisasiPagu / target(penetapan) * 100
        var capaianPaguResult = hitungCapaian(realisasiPagu, t.target());
        Double capaianPagu = realisasiPagu != null ? capaianPaguResult.capaian() : null;
        String keteranganCapaianPagu = realisasiPagu != null ? capaianPaguResult.keteranganCapaian() : null;
        return new PenetapanRenjaIndividuResponse.TargetPenetapanResponse(
                t.id(), t.kodeTarget(), t.tahun(), t.target(), t.satuan(),
                realisasiTarget, realisasiPagu,
                capaianTargetResult.capaian(), capaianTargetResult.keteranganCapaian(),
                capaianPagu, keteranganCapaianPagu,
                data != null ? data.faktorPenunjang() : null,
                data != null ? data.faktorPenghambat() : null,
                data != null ? data.buktiPendukung() : null,
                data != null ? data.keteranganBuktiPendukung() : null,
                data != null && data.jenisRealisasi() != null ? data.jenisRealisasi() : "NAIK"
        );
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : List.of();
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
