package cc.kertaskerja.realisasi_opd_service.renja.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaOpdService;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaProgramOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenghambatTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenunjangTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanOpdRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_opd")
@Tag(name = "OPD - Renja", description = "Endpoint realisasi renja tingkat OPD.")
public class RenjaOpdController {
    private final RenjaOpdService renjaOpdService;

    public RenjaOpdController(RenjaOpdService renjaOpdService) {
        this.renjaOpdService = renjaOpdService;
    }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja OPD", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja OPD berdasarkan kode OPD dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = RenjaOpdPenetapanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaOpdPenetapanResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return renjaOpdService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renja OPD per periode", description = "Mengambil total realisasi renja OPD yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN). Data diagregasi dari program, kegiatan, dan subkegiatan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renja OPD", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenjaOpdResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<LaporanRealisasiRenjaOpdResponse> getLaporanRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        return renjaOpdService.getLaporanRealisasi(kodeOpd, tahun, jenisLaporan, bulan);
    }

    @PostMapping("/program/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja program OPD", description = "Memperbarui field faktor_penunjang pada semua record realisasi_target_renja_program_opd yang cocok dengan kode_opd, kode_program, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaProgramOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Program OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaProgramOpd> updateFaktorPenunjangProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaProgramOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaProgramOpdRequest req) {
        return renjaOpdService.updateFaktorPenunjangProgram(req);
    }

    @PostMapping("/program/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja program OPD", description = "Memperbarui field faktor_penghambat pada semua record realisasi_target_renja_program_opd yang cocok dengan kode_opd, kode_program, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaProgramOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Program OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaProgramOpd> updateFaktorPenghambatProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaProgramOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaProgramOpdRequest req) {
        return renjaOpdService.updateFaktorPenghambatProgram(req);
    }

    @PostMapping("/kegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja kegiatan OPD", description = "Memperbarui field faktor_penunjang pada semua record realisasi_target_renja_kegiatan_opd yang cocok dengan kode_opd, kode_kegiatan, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaKegiatanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Kegiatan OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaKegiatanOpd> updateFaktorPenunjangKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaKegiatanOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaKegiatanOpdRequest req) {
        return renjaOpdService.updateFaktorPenunjangKegiatan(req);
    }

    @PostMapping("/kegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja kegiatan OPD", description = "Memperbarui field faktor_penghambat pada semua record realisasi_target_renja_kegiatan_opd yang cocok dengan kode_opd, kode_kegiatan, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaKegiatanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Kegiatan OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaKegiatanOpd> updateFaktorPenghambatKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaKegiatanOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaKegiatanOpdRequest req) {
        return renjaOpdService.updateFaktorPenghambatKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja subkegiatan OPD", description = "Memperbarui field faktor_penunjang pada semua record realisasi_target_renja_subkegiatan_opd yang cocok dengan kode_opd, kode_subkegiatan, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaSubKegiatanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subkegiatan OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaSubKegiatanOpd> updateFaktorPenunjangSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaSubKegiatanOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaSubKegiatanOpdRequest req) {
        return renjaOpdService.updateFaktorPenunjangSubKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja subkegiatan OPD", description = "Memperbarui field faktor_penghambat pada semua record realisasi_target_renja_subkegiatan_opd yang cocok dengan kode_opd, kode_subkegiatan, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenjaSubKegiatanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subkegiatan OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenjaSubKegiatanOpd> updateFaktorPenghambatSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaSubKegiatanOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaSubKegiatanOpdRequest req) {
        return renjaOpdService.updateFaktorPenghambatSubKegiatan(req);
    }
}
