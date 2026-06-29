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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class RenjaIndividuService {
    private final RenjaProgramIndividuRepository programRepo;
    private final RenjaKegiatanIndividuRepository kegiatanRepo;
    private final RenjaSubKegiatanIndividuRepository subKegiatanRepo;

    record CapaianResult(Double capaian, String keteranganCapaian) {}

    public RenjaIndividuService(
            RenjaProgramIndividuRepository programRepo,
            RenjaKegiatanIndividuRepository kegiatanRepo,
            RenjaSubKegiatanIndividuRepository subKegiatanRepo
    ) {
        this.programRepo = programRepo;
        this.kegiatanRepo = kegiatanRepo;
        this.subKegiatanRepo = subKegiatanRepo;
    }

    @Transactional
    public Mono<RenjaIndividuProgramResponse> submitProgram(RenjaIndividuProgramRequest req) {
        return upsertProgram(req)
                .flatMap(this::enrichProgramResponse);
    }

    @Transactional
    public Mono<RenjaIndividuKegiatanResponse> submitKegiatan(RenjaIndividuKegiatanRequest req) {
        return upsertKegiatan(req)
                .flatMap(this::enrichKegiatanResponse);
    }

    @Transactional
    public Mono<RenjaIndividuSubKegiatanResponse> submitSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        return upsertSubKegiatan(req)
                .flatMap(this::syncParentPaguFromSubKegiatan)
                .flatMap(this::enrichSubKegiatanResponse);
    }

    public Mono<RenjaProgramIndividu> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaProgramIndividu> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaKegiatanIndividu> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaKegiatanIndividu> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenunjangSubKegiatan(FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenghambatSubKegiatan(FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Flux<RenjaIndividuProgramResponse> getProgramByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return programRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichProgramResponse);
    }

    public Flux<RenjaIndividuKegiatanResponse> getKegiatanByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return kegiatanRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichKegiatanResponse);
    }

    public Flux<RenjaIndividuSubKegiatanResponse> getSubKegiatanByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        return subKegiatanRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichSubKegiatanResponse);
    }

    public Mono<LaporanRealisasiRenjaProgramIndividuResponse> getLaporanRealisasiProgram(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return programRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .map(list -> new LaporanRealisasiRenjaProgramIndividuResponse(
                        tahun,
                        kodeOpd,
                        nip,
                        jenisLaporan,
                        buildLaporanData(list, jenisLaporan, bulan, item -> item.realisasi() != null ? item.realisasi().doubleValue() : null)
                ));
    }

    public Mono<LaporanRealisasiRenjaKegiatanIndividuResponse> getLaporanRealisasiKegiatan(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return kegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .map(list -> new LaporanRealisasiRenjaKegiatanIndividuResponse(
                        tahun,
                        kodeOpd,
                        nip,
                        jenisLaporan,
                        buildLaporanData(list, jenisLaporan, bulan, item -> item.realisasi() != null ? item.realisasi().doubleValue() : null)
                ));
    }

    public Mono<LaporanRealisasiRenjaSubKegiatanIndividuResponse> getLaporanRealisasiSubKegiatan(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        return subKegiatanRepo.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun)
                .collectList()
                .map(list -> new LaporanRealisasiRenjaSubKegiatanIndividuResponse(
                        tahun,
                        kodeOpd,
                        nip,
                        jenisLaporan,
                        buildLaporanData(list, jenisLaporan, bulan, item -> item.realisasiTarget() != null ? item.realisasiTarget().doubleValue() : null)
                ));
    }

    private Mono<RenjaProgramIndividu> upsertProgram(RenjaIndividuProgramRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> programRepo.save(new RenjaProgramIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeProgram(),  "Realisasi Program " + existing.kodeProgram(),
                        existing.kodeIndikator(), "Realisasi indikator " + existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(), existing.pagu(),
                        BigDecimal.valueOf(req.target()), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> programRepo.save(new RenjaProgramIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeProgram(), "Realisasi program " + req.kodeProgram(),
                        req.kodeIndikator(), "Realisasi Subkegiatan " + req.kodeIndikator(),
                        req.kodeTarget(), kodePagu, null,
                        BigDecimal.valueOf(req.target()), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaKegiatanIndividu> upsertKegiatan(RenjaIndividuKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeKegiatan(), "Realisasi Kegiatan " + existing.kodeKegiatan(),
                        existing.kodeIndikator(), "Realisasi indikator " + existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(), existing.pagu(),
                        BigDecimal.valueOf(req.target()), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeKegiatan(), "Realisasi kegiatan " + req.kodeKegiatan(),
                        req.kodeIndikator(), "Realisasi indikator " + req.kodeIndikator(),
                        req.kodeTarget(), kodePagu, null,
                        BigDecimal.valueOf(req.target()), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaSubKegiatanIndividu> upsertSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        BigDecimal pagu = BigDecimal.valueOf(req.pagu());
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> subKegiatanRepo.save(new RenjaSubKegiatanIndividu(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeSubKegiatan(), "Realisasi SubKegiatan " + existing.kodeSubKegiatan(),
                        existing.kodeIndikator(), "Realisasi indikator " + existing.kodeIndikator(),
                        existing.kodeTarget(), existing.kodePagu(), pagu,
                        BigDecimal.valueOf(req.targetRealisasi()),
                        BigDecimal.valueOf(req.realisasiTarget()), BigDecimal.valueOf(req.realisasiPagu()),
                        jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> subKegiatanRepo.save(new RenjaSubKegiatanIndividu(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeSubKegiatan(), "Realisasi Subkegiatan " + req.kodeSubKegiatan(),
                        req.kodeIndikator(), "Realisasi Indikator " + req.kodeIndikator(),
                        req.kodeTarget(), kodePagu, pagu,
                        BigDecimal.valueOf(req.targetRealisasi()),
                        BigDecimal.valueOf(req.realisasiTarget()), BigDecimal.valueOf(req.realisasiPagu()),
                        jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaIndividuProgramResponse> enrichProgramResponse(RenjaProgramIndividu saved) {
        var capaianResult = hitungCapaian(
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                saved.target() != null ? saved.target().doubleValue() : null);
        return sumPaguForPrefix(saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan(), saved.kodeProgram())
                .map(pagu -> new RenjaIndividuProgramResponse(
                        saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                        saved.kodeProgram(), "Realisasi program " + saved.kodeProgram(),  saved.kodeIndikator(), "Realisasi indikator " + saved.kodeIndikator(), saved.kodeTarget(),
                        saved.kodePagu(), pagu != null ? pagu.doubleValue() : null,
                        saved.target() != null ? saved.target().doubleValue() : null,
                        saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                        saved.jenisRealisasi(),
                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                        saved.faktorPenunjang(), saved.faktorPenghambat(),
                        saved.createdBy(), saved.lastModifiedBy()
                ));
    }

    private Mono<RenjaIndividuKegiatanResponse> enrichKegiatanResponse(RenjaKegiatanIndividu saved) {
        var capaianResult = hitungCapaian(
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                saved.target() != null ? saved.target().doubleValue() : null);
        return sumPaguForPrefix(saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan(), saved.kodeKegiatan())
                .map(pagu -> new RenjaIndividuKegiatanResponse(
                        saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                        saved.kodeKegiatan(), "Realisasi Kegiatan " + saved.kodeKegiatan(),  saved.kodeIndikator(), "Realisasi indikator " +saved.kodeIndikator(), saved.kodeTarget(),
                        saved.kodePagu(), pagu != null ? pagu.doubleValue() : null,
                        saved.target() != null ? saved.target().doubleValue() : null,
                        saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                        saved.jenisRealisasi(),
                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                        saved.faktorPenunjang(), saved.faktorPenghambat(),
                        saved.createdBy(), saved.lastModifiedBy()
                ));
    }

    private Mono<BigDecimal> sumPaguForPrefix(String kodeOpd, String nip, String tahun, String bulan, String kodePrefix) {
        if (kodePrefix == null || kodePrefix.isBlank()) {
            return Mono.just(BigDecimal.ZERO);
        }
        return subKegiatanRepo.sumPaguByKodeSubKegiatanPrefix(
                kodeOpd,
                nip,
                tahun,
                bulan,
                kodePrefix + ".%"
        );
    }

    private Mono<RenjaSubKegiatanIndividu> syncParentPaguFromSubKegiatan(RenjaSubKegiatanIndividu saved) {
        String kodeKegiatan = extractParentKegiatanCode(saved.kodeSubKegiatan());
        String kodeProgram = extractParentProgramCode(saved.kodeSubKegiatan());

        Mono<Void> syncKegiatan = kodeKegiatan == null
                ? Mono.empty()
                : syncKegiatanPagu(saved, kodeKegiatan);
        Mono<Void> syncProgram = kodeProgram == null
                ? Mono.empty()
                : syncProgramPagu(saved, kodeProgram);

        return syncKegiatan.then(syncProgram).thenReturn(saved);
    }

    private Mono<Void> syncKegiatanPagu(RenjaSubKegiatanIndividu saved, String kodeKegiatan) {
        return sumPaguForPrefix(saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan(), kodeKegiatan)
                .flatMap(totalPagu -> kegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan(
                                saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan())
                        .filter(kegiatan -> kodeKegiatan.equals(kegiatan.kodeKegiatan()))
                        .flatMap(kegiatan -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                                kegiatan.id(), kegiatan.kodeOpd(), kegiatan.nip(),
                                kegiatan.tahun(), kegiatan.bulan(),
                                kegiatan.kodeKegiatan(), kegiatan.kegiatan(),
                                kegiatan.kodeIndikator(), kegiatan.indikator(),
                                kegiatan.kodeTarget(), kegiatan.kodePagu(), totalPagu,
                                kegiatan.target(), kegiatan.realisasi(), kegiatan.jenisRealisasi(),
                                kegiatan.faktorPenunjang(), kegiatan.faktorPenghambat(),
                                kegiatan.createdDate(), null, kegiatan.createdBy(), null
                        )))
                        .then());
    }

    private Mono<Void> syncProgramPagu(RenjaSubKegiatanIndividu saved, String kodeProgram) {
        return sumPaguForPrefix(saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan(), kodeProgram)
                .flatMap(totalPagu -> programRepo.findAllByKodeOpdAndNipAndTahunAndBulan(
                                saved.kodeOpd(), saved.nip(), saved.tahun(), saved.bulan())
                        .filter(program -> kodeProgram.equals(program.kodeProgram()))
                        .flatMap(program -> programRepo.save(new RenjaProgramIndividu(
                                program.id(), program.kodeOpd(), program.nip(),
                                program.tahun(), program.bulan(),
                                program.kodeProgram(), program.program(),
                                program.kodeIndikator(), program.indikator(),
                                program.kodeTarget(), program.kodePagu(), totalPagu,
                                program.target(), program.realisasi(), program.jenisRealisasi(),
                                program.faktorPenunjang(), program.faktorPenghambat(),
                                program.createdDate(), null, program.createdBy(), null
                        )))
                        .then());
    }

    private String extractParentKegiatanCode(String kodeSubKegiatan) {
        String[] parts = splitKode(kodeSubKegiatan);
        if (parts.length < 4) {
            return null;
        }
        return String.join(".", parts[0], parts[1], parts[2], parts[3]);
    }

    private String extractParentProgramCode(String kodeSubKegiatan) {
        String[] parts = splitKode(kodeSubKegiatan);
        if (parts.length < 3) {
            return null;
        }
        return String.join(".", parts[0], parts[1], parts[2]);
    }

    private String[] splitKode(String kode) {
        return kode == null ? new String[0] : kode.split("\\.");
    }

    private Mono<RenjaIndividuSubKegiatanResponse> enrichSubKegiatanResponse(RenjaSubKegiatanIndividu saved) {
        var capaianFisik = hitungCapaian(
                saved.realisasiTarget() != null ? saved.realisasiTarget().doubleValue() : null,
                saved.targetRealisasi() != null ? saved.targetRealisasi().doubleValue() : null);
        var capaianPagu = hitungCapaian(
                saved.realisasiPagu() != null ? saved.realisasiPagu().doubleValue() : null,
                saved.pagu() != null ? saved.pagu().doubleValue() : null);
        return Mono.just(new RenjaIndividuSubKegiatanResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeSubKegiatan(), "Realisasi Subkegiatan " + saved.kodeSubKegiatan(), saved.kodeIndikator(), "Realisasi indikator " + saved.kodeIndikator(), saved.kodeTarget(),
                saved.kodePagu(), saved.pagu() != null ? saved.pagu().doubleValue() : null,
                saved.targetRealisasi() != null ? saved.targetRealisasi().doubleValue() : null,
                saved.realisasiTarget() != null ? saved.realisasiTarget().doubleValue() : null,
                saved.realisasiPagu() != null ? saved.realisasiPagu().doubleValue() : null,
                saved.jenisRealisasi(),
                capaianFisik.capaian(), capaianFisik.keteranganCapaian(),
                capaianPagu.capaian(), capaianPagu.keteranganCapaian(),
                saved.faktorPenunjang(), saved.faktorPenghambat(),
                saved.createdBy(), saved.lastModifiedBy()
        ));
    }

    // hitung capaian ditaruh di service agar lebih simple karena di renja individu ada 3 domain
    static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (target == null || target == 0 || realisasi == null || realisasi == 0) {
            return new CapaianResult(0.0, null);
        }
        double calculated = realisasi / target * 100;
        String keterangan = null;
        if (calculated > 100) {
            keterangan = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculated) + ")";
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
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
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
}
