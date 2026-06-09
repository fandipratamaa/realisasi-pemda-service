package cc.kertaskerja.realisasi_individu_service.renja.web;

import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.TargetRenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.TargetRenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.TargetRenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.RenjaIndividuService;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenunjangTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenghambatTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_individu")
@Tag(name = "Individu - Renja", description = "Endpoint realisasi renja tingkat individu.")
public class RenjaIndividuController {
    private final RenjaIndividuService renjaIndividuService;

    public RenjaIndividuController(RenjaIndividuService renjaIndividuService) {
        this.renjaIndividuService = renjaIndividuService;
    }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja individu berdasarkan kode OPD dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = RenjaIndividuPenetapanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/program/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu - PROGRAM", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja individu tingkat PROGRAM. Hanya untuk level_2.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan program terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = RenjaIndividuPenetapanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiProgram(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getPenetapanWithRealisasiProgram(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/kegiatan/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu - KEGIATAN", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja individu tingkat KEGIATAN. Hanya untuk level_3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan kegiatan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = RenjaIndividuPenetapanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiKegiatan(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getPenetapanWithRealisasiKegiatan(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @GetMapping("/subkegiatan/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renja individu - SUBKEGIATAN", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renja individu tingkat SUBKEGIATAN. Hanya untuk level_3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan subkegiatan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = RenjaIndividuPenetapanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiSubKegiatan(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return renjaIndividuService.getPenetapanWithRealisasiSubKegiatan(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @PostMapping("/program")
    @Operation(summary = "Simpan realisasi renja individu - PROGRAM", description = "Menyimpan realisasi renja individu tingkat PROGRAM. Hanya dapat diakses level_2 dan level_3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja program tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuProgramResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuProgramResponse> submitRealisasiProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja program", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuProgramRequest.class)))
            @RequestBody @Valid RenjaIndividuProgramRequest request) {
        return renjaIndividuService.submitProgram(request);
    }

    @PostMapping("/kegiatan")
    @Operation(summary = "Simpan realisasi renja individu - KEGIATAN", description = "Menyimpan realisasi renja individu tingkat KEGIATAN. Hanya dapat diakses level_3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja kegiatan tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuKegiatanResponse> submitRealisasiKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuKegiatanRequest.class)))
            @RequestBody @Valid RenjaIndividuKegiatanRequest request) {
        return renjaIndividuService.submitKegiatan(request);
    }

    @PostMapping("/subkegiatan")
    @Operation(summary = "Simpan realisasi renja individu - SUBKEGIATAN", description = "Menyimpan realisasi renja individu tingkat SUBKEGIATAN. Hanya dapat diakses level_3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja subkegiatan tersimpan", content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaIndividuSubKegiatanResponse> submitRealisasiSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaIndividuSubKegiatanRequest.class)))
            @RequestBody @Valid RenjaIndividuSubKegiatanRequest request) {
        return renjaIndividuService.submitSubKegiatan(request);
    }

    @PostMapping("/program/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja program", description = "Memperbarui hanya field faktor_penunjang pada record TargetRenjaProgramIndividu yang cocok dengan kode_program, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaProgramIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaProgramIndividu> updateFaktorPenunjangProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaProgramRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaProgramRequest req) {
        return renjaIndividuService.updateFaktorPenunjangProgram(req);
    }

    @PostMapping("/program/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja program", description = "Memperbarui hanya field faktor_penghambat pada record TargetRenjaProgramIndividu yang cocok dengan kode_program, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaProgramIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaProgramIndividu> updateFaktorPenghambatProgram(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target program", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaProgramRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaProgramRequest req) {
        return renjaIndividuService.updateFaktorPenghambatProgram(req);
    }

    @PostMapping("/kegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja kegiatan", description = "Memperbarui hanya field faktor_penunjang pada record TargetRenjaKegiatanIndividu yang cocok dengan kode_kegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaKegiatanIndividu> updateFaktorPenunjangKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenunjangKegiatan(req);
    }

    @PostMapping("/kegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja kegiatan", description = "Memperbarui hanya field faktor_penghambat pada record TargetRenjaKegiatanIndividu yang cocok dengan kode_kegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaKegiatanIndividu> updateFaktorPenghambatKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target kegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenghambatKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang target renja subkegiatan", description = "Memperbarui hanya field faktor_penunjang pada record TargetRenjaSubKegiatanIndividu yang cocok dengan kode_subkegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaSubKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaSubKegiatanIndividu> updateFaktorPenunjangSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTargetRenjaSubKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenunjangSubKegiatan(req);
    }

    @PostMapping("/subkegiatan/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat target renja subkegiatan", description = "Memperbarui hanya field faktor_penghambat pada record TargetRenjaSubKegiatanIndividu yang cocok dengan kode_subkegiatan, kode_indikator, kode_target, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetRenjaSubKegiatanIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Target tidak ditemukan", content = @Content)
    })
    public Mono<TargetRenjaSubKegiatanIndividu> updateFaktorPenghambatSubKegiatan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat target subkegiatan", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTargetRenjaSubKegiatanRequest.class)))
            @RequestBody @Valid FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return renjaIndividuService.updateFaktorPenghambatSubKegiatan(req);
    }
}
