package cc.kertaskerja.realisasi_individu_service.renja.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.RenjaIndividuService;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.LaporanRealisasiRenjaKegiatanIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenunjangTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenghambatTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.LaporanRealisasiRenjaProgramIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.LaporanRealisasiRenjaSubKegiatanIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_individu")
@Tag(name = "Individu - Renja", description = "Endpoint realisasi renja tingkat individu.")
public class RenjaIndividuController {
    private final RenjaIndividuService renjaIndividuService;

    public RenjaIndividuController(RenjaIndividuService renjaIndividuService) {
        this.renjaIndividuService = renjaIndividuService;
    }

    @PostMapping("/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/sync/penetapan")
    @Operation(summary = "Sinkronisasi renja individu", description = "Memicu sinkronisasi data renja individu dari service penetapan dan langsung mengembalikan data terbarunya.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan ter-sinkronisasi dan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenjaIndividuResponse> syncRenjaIndividu(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.syncPenetapanRenjaIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .thenReturn(true)
                .defaultIfEmpty(true)
                .flatMap(ignored -> renjaIndividuService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan));
    }

    @GetMapping("/program/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja berdasarkan kode OPD, NIP, dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenjaIndividuResponse> getPenetapanProgramByNipAndTahun(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/kegiatan/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu (kegiatan)", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja kegiatan berdasarkan kode OPD, NIP, dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenjaIndividuResponse> getPenetapanKegiatanByNipAndTahun(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/subkegiatan/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu (subkegiatan)", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja subkegiatan berdasarkan kode OPD, NIP, dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenjaIndividuResponse> getPenetapanSubKegiatanByNipAndTahun(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/program/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Mencari realisasi renja individu - PROGRAM", description = "Endpoint untuk fitur pencarian realisasi renja individu tingkat PROGRAM di frontend. Memvalidasi NIP ke service pegawai lalu mengambil data penetapan terintegrasi realisasi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pegawai tidak ditemukan", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Mono<PenetapanRenjaIndividuResponse> searchProgram(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun") @PathVariable String tahun,
            @Parameter(description = "Bulan") @PathVariable String bulan,
            @Parameter(description = "Level Role (LEVEL_1, dll)") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renjaIndividuService.searchProgram(kodeOpd, tahun, bulan, levelRole, nip);
    }

    @GetMapping("/kegiatan/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Mencari realisasi renja individu - KEGIATAN", description = "Endpoint untuk fitur pencarian realisasi renja individu tingkat KEGIATAN di frontend. Memvalidasi NIP ke service pegawai lalu mengambil data penetapan terintegrasi realisasi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pegawai tidak ditemukan", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Mono<PenetapanRenjaIndividuResponse> searchKegiatan(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun") @PathVariable String tahun,
            @Parameter(description = "Bulan") @PathVariable String bulan,
            @Parameter(description = "Level Role (LEVEL_1, dll)") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renjaIndividuService.searchKegiatan(kodeOpd, tahun, bulan, levelRole, nip);
    }

    @GetMapping("/subkegiatan/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Mencari realisasi renja individu - SUBKEGIATAN", description = "Endpoint untuk fitur pencarian realisasi renja individu tingkat SUBKEGIATAN di frontend. Memvalidasi NIP ke service pegawai lalu mengambil data penetapan terintegrasi realisasi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenjaIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pegawai tidak ditemukan", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Mono<PenetapanRenjaIndividuResponse> searchSubKegiatan(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun") @PathVariable String tahun,
            @Parameter(description = "Bulan") @PathVariable String bulan,
            @Parameter(description = "Level Role (LEVEL_1, dll)") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renjaIndividuService.searchSubKegiatan(kodeOpd, tahun, bulan, levelRole, nip);
    }

    @GetMapping("/program/laporan/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu program per periode", description = "Mengambil total realisasi renja individu tingkat program yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu program", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaProgramIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenjaProgramIndividuResponse> getLaporanRealisasiProgram(
            @Parameter(description = "NIP pegawai", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        validateLaporanParams(nip, kodeOpd, tahun);
        return renjaIndividuService.getLaporanRealisasiProgram(nip, kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/kegiatan/laporan/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu kegiatan per periode", description = "Mengambil total realisasi renja individu tingkat kegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu kegiatan", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaKegiatanIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenjaKegiatanIndividuResponse> getLaporanRealisasiKegiatan(
            @Parameter(description = "NIP pegawai", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        validateLaporanParams(nip, kodeOpd, tahun);
        return renjaIndividuService.getLaporanRealisasiKegiatan(nip, kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/subkegiatan/laporan/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu subkegiatan per periode", description = "Mengambil total realisasi target renja individu tingkat subkegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu subkegiatan", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaSubKegiatanIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenjaSubKegiatanIndividuResponse> getLaporanRealisasiSubKegiatan(
            @Parameter(description = "NIP pegawai") @PathVariable String nip,
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        validateLaporanParams(nip, kodeOpd, tahun);
        return renjaIndividuService.getLaporanRealisasiSubKegiatan(nip, kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/program/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Laporan realisasi renja individu program per periode (OPD)", description = "Mengambil total realisasi renja individu tingkat program yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk NIP tertentu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu program", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaProgramIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<LaporanRealisasiRenjaProgramIndividuResponse> getLaporanRealisasiProgramByOpd(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Level Role") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getLaporanRealisasiProgramByOpd(kodeOpd, tahun, jenisLaporan, bulan, levelRole, nip);
    }

    @GetMapping("/kegiatan/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Laporan realisasi renja individu kegiatan per periode (OPD)", description = "Mengambil total realisasi renja individu tingkat kegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk NIP tertentu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu kegiatan", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaKegiatanIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<LaporanRealisasiRenjaKegiatanIndividuResponse> getLaporanRealisasiKegiatanByOpd(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Level Role") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getLaporanRealisasiKegiatanByOpd(kodeOpd, tahun, jenisLaporan, bulan, levelRole, nip);
    }

    @GetMapping("/subkegiatan/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Laporan realisasi renja individu subkegiatan per periode (OPD)", description = "Mengambil total realisasi target renja individu tingkat subkegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk NIP tertentu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja individu subkegiatan", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaSubKegiatanIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<LaporanRealisasiRenjaSubKegiatanIndividuResponse> getLaporanRealisasiSubKegiatanByOpd(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Level Role") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renjaIndividuService.getLaporanRealisasiSubKegiatanByOpd(kodeOpd, tahun, jenisLaporan, bulan, levelRole, nip);
    }

    @PostMapping(value = "/program", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi renja individu - PROGRAM", description = "Menyimpan realisasi renja individu tingkat PROGRAM.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja program tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuProgramResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuProgramResponse> submitRealisasiProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja program", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuProgramRequest.class)))
            @RequestBody @Valid RenjaIndividuProgramRequest request) {
        return renjaIndividuService.submitProgram(request);
    }

    @PostMapping(value = "/kegiatan", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi renja individu - KEGIATAN", description = "Menyimpan realisasi renja individu tingkat KEGIATAN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja kegiatan tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuKegiatanResponse> submitRealisasiKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanRequest.class)))
            @RequestBody @Valid RenjaIndividuKegiatanRequest request) {
        return renjaIndividuService.submitKegiatan(request);
    }

    @PostMapping(value = "/subkegiatan", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi renja individu - SUBKEGIATAN", description = "Menyimpan realisasi renja individu tingkat SUBKEGIATAN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja subkegiatan tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuSubKegiatanResponse> submitRealisasiSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanRequest.class)))
            @RequestBody @Valid RenjaIndividuSubKegiatanRequest request) {
        return renjaIndividuService.submitSubKegiatan(request);
    }

    @PostMapping("/program/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja program", description = "Memperbarui hanya field faktor_penunjang pada record RenjaProgramIndividu yang cocok dengan kode_opd, kode_program, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaProgramIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaProgramIndividu> updateFaktorPenunjangProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaProgramRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaProgramRequest req) {
        return renjaIndividuService.updateFaktorPenunjangProgram(req);
    }

    @PostMapping("/program/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja program", description = "Memperbarui hanya field faktor_penghambat pada record RenjaProgramIndividu yang cocok dengan kode_opd, kode_program, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaProgramIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaProgramIndividu> updateFaktorPenghambatProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaProgramRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaProgramRequest req) {
        return renjaIndividuService.updateFaktorPenghambatProgram(req);
    }

    @PostMapping("/kegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja kegiatan", description = "Memperbarui hanya field faktor_penunjang pada record RenjaKegiatanIndividu yang cocok dengan kode_opd, kode_kegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaKegiatanIndividu> updateFaktorPenunjangKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenunjangKegiatan(req);
    }

    @PostMapping("/kegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja kegiatan", description = "Memperbarui hanya field faktor_penghambat pada record RenjaKegiatanIndividu yang cocok dengan kode_opd, kode_kegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaKegiatanIndividu> updateFaktorPenghambatKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenghambatKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja subkegiatan", description = "Memperbarui hanya field faktor_penunjang pada record RenjaSubKegiatanIndividu yang cocok dengan kode_opd, kode_subkegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaSubKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenunjangSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaSubKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenunjangSubKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja subkegiatan", description = "Memperbarui hanya field faktor_penghambat pada record RenjaSubKegiatanIndividu yang cocok dengan kode_opd, kode_subkegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaSubKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<RenjaSubKegiatanIndividu> updateFaktorPenghambatSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaSubKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenghambatSubKegiatan(req);
    }

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file bukti pendukung", description = "Mengunggah file dan mengembalikan string URL.")
    public Mono<java.util.Map<String, String>> uploadFile(
            @Parameter(description = "File yang akan diupload", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") FilePart file) {
        return renjaIndividuService.uploadFile(file)
                .map(url -> java.util.Map.of("url", url));
    }

    private void validateLaporanParams(String nip, String kodeOpd, String tahun) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
    }
}
