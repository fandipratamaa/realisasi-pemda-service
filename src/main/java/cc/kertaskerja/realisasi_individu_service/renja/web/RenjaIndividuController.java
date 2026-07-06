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

    @GetMapping("/program/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - PROGRAM saja", description = "Mengembalikan data realisasi renja individu tingkat PROGRAM yang cocok dengan kode_opd, nip, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja program ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuProgramResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaIndividuProgramResponse> getProgramByKodeOpdAndNipAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String nip,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getProgramByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    @GetMapping("/kegiatan/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - KEGIATAN saja", description = "Mengembalikan data realisasi renja individu tingkat KEGIATAN yang cocok dengan kode_opd, nip, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja kegiatan ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaIndividuKegiatanResponse> getKegiatanByKodeOpdAndNipAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String nip,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getKegiatanByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    @GetMapping("/subkegiatan/kodeOpd/{kodeOpd}/nip/{nip}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - SUBKEGIATAN saja", description = "Mengembalikan data realisasi renja individu tingkat SUBKEGIATAN yang cocok dengan kode_opd, nip, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja subkegiatan ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaIndividuSubKegiatanResponse> getSubKegiatanByKodeOpdAndNipAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String nip,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getSubKegiatanByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan);
    }

    @GetMapping("/program/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - PROGRAM OPD", description = "Mengembalikan data realisasi renja individu tingkat PROGRAM yang cocok dengan kode_opd, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja program ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuProgramResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<RenjaIndividuProgramResponse> getProgramByKodeOpdAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getProgramByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @GetMapping("/kegiatan/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - KEGIATAN OPD", description = "Mengembalikan data realisasi renja individu tingkat KEGIATAN yang cocok dengan kode_opd, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja kegiatan ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<RenjaIndividuKegiatanResponse> getKegiatanByKodeOpdAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getKegiatanByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @GetMapping("/subkegiatan/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja individu - SUBKEGIATAN OPD", description = "Mengembalikan data realisasi renja individu tingkat SUBKEGIATAN yang cocok dengan kode_opd, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja subkegiatan ditemukan", content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<RenjaIndividuSubKegiatanResponse> getSubKegiatanByKodeOpdAndTahunAndBulan(
            @PathVariable String kodeOpd,
            @PathVariable String tahun,
            @PathVariable String bulan) {
        return renjaIndividuService.getSubKegiatanByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
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

    @GetMapping("/program/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu program per periode (OPD)", description = "Mengambil total realisasi renja individu tingkat program yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk seluruh OPD.")
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
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getLaporanRealisasiProgramByOpd(kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/kegiatan/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu kegiatan per periode (OPD)", description = "Mengambil total realisasi renja individu tingkat kegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk seluruh OPD.")
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
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getLaporanRealisasiKegiatanByOpd(kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/subkegiatan/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja individu subkegiatan per periode (OPD)", description = "Mengambil total realisasi target renja individu tingkat subkegiatan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk seluruh OPD.")
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
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getLaporanRealisasiSubKegiatanByOpd(kodeOpd, tahun, jenisLaporan, bulan);
    }

    @PostMapping("/program")
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

    @PostMapping("/kegiatan")
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

    @PostMapping("/subkegiatan")
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

    private void validateLaporanParams(String nip, String kodeOpd, String tahun) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
    }
}
