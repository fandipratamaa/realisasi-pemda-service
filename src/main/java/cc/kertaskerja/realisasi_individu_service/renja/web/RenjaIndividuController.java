package cc.kertaskerja.realisasi_individu_service.renja.web;

import cc.kertaskerja.realisasi_individu_service.renja.domain.RenjaIndividuService;
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
}
