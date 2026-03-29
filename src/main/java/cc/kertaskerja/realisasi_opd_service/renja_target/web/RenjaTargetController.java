package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renja_target")
@Tag(name = "OPD - Renja Target", description = "Endpoint realisasi renja target tingkat OPD")
public class RenjaTargetController {
    private final RenjaTargetService renjaTargetService;

    public RenjaTargetController(RenjaTargetService renjaTargetService) {
        this.renjaTargetService = renjaTargetService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renja target", description = "Mengambil seluruh data realisasi renja target OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getAllRealisasiRenjaTarget() {
        return renjaTargetService.getAllRealisasiRenjaTarget();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Ambil realisasi renja target berdasarkan ID", description = "Mengambil satu data realisasi renja target berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target ditemukan", content = @Content(schema = @Schema(implementation = RenjaTarget.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenjaTarget> getRealisasiRenjaTarget(
            @Parameter(description = "ID internal realisasi renja target", example = "1") @PathVariable("id") Long id) {
        return renjaTargetService.getRealisasiRenjaTargetById(id);
    }

    @GetMapping("/by-renja/{renjaId}")
    @Operation(summary = "Cari realisasi renja target berdasarkan ID renja", description = "Mengambil daftar realisasi renja target berdasarkan `renjaId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getRealisasiRenjaTargetByRenjaId(
            @Parameter(description = "ID renja", example = "REN-001") @PathVariable String renjaId) {
        return renjaTargetService.getRealisasiRenjaTargetByRenjaId(renjaId);
    }

    @GetMapping("/{kodeOpd}")
    @Operation(summary = "Cari realisasi renja target berdasarkan kode OPD", description = "Mengambil seluruh realisasi renja target untuk satu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getRealisasiRenjaTargetByKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
        return renjaTargetService.getRealisasiRenjaTargetByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi renja target per tahun", description = "Mengambil realisasi renja target berdasarkan kode OPD dan tahun, dapat difilter lagi dengan `renjaTargetId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getRealisasiRenjaTargetByTahunAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID renja target", example = "REN-001") @RequestParam(required = false) String renjaTargetId) {
        if (renjaTargetId != null && !renjaTargetId.isBlank()) {
            return renjaTargetService.getRealisasiRenjaTargetByTahunAndRenjaTargetIdAndKodeOpd(tahun, renjaTargetId, kodeOpd);
        }
        return renjaTargetService.getRealisasiRenjaTargetByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi renja target periode RPJMD", description = "Mengambil realisasi renja target pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getRealisasiRenjaTargetByPeriodeRpjmd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return renjaTargetService.getRealisasiRenjaTargetByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @GetMapping("/by-indikator/{indikatorId}")
    @Operation(summary = "Cari realisasi renja target berdasarkan indikator", description = "Mengambil realisasi renja target berdasarkan `indikatorId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> getRealisasiRenjaTargetByIndikatorId(
            @Parameter(description = "ID indikator", example = "IND-REN-123") @PathVariable String indikatorId) {
        return renjaTargetService.getRealisasiRenjaTargetByIndikatorId(indikatorId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja target", description = "Menyimpan satu data realisasi renja target OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target tersimpan", content = @Content(schema = @Schema(implementation = RenjaTarget.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaTarget> submitRealisasiRenjaTarget(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja target", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaTargetRequest.class)))
            @RequestBody @Valid RenjaTargetRequest renjaTargetRequest) {
        return renjaTargetService.submitRealisasiRenjaTarget(
                renjaTargetRequest.renjaTargetId(),
                renjaTargetRequest.renjaTarget(),
                renjaTargetRequest.jenisRenjaTarget(),
                renjaTargetRequest.indikatorId(),
                renjaTargetRequest.indikator(),
                renjaTargetRequest.targetId(),
                renjaTargetRequest.target(),
                renjaTargetRequest.realisasi(),
                renjaTargetRequest.satuan(),
                renjaTargetRequest.tahun(),
                renjaTargetRequest.jenisRealisasi(),
                renjaTargetRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja target", description = "Menyimpan beberapa data realisasi renja target dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> batchSubmitRealisasiRenjaTarget(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renja target", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenjaTargetRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": 10,\n" +
                                    "    \"renjaTargetId\": \"REN-001\",\n" +
                                    "    \"renjaTarget\": \"Program Peningkatan Infrastruktur\",\n" +
                                    "    \"jenisRenjaTarget\": \"PROGRAM\",\n" +
                                    "    \"indikatorId\": \"IND-REN-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian program\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\",\n" +
                                    "    \"kodeOpd\": \"OPD-001\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaTargetRequest> renjaTargetRequests) {
        return renjaTargetService.batchSubmitRealisasiRenjaTarget(renjaTargetRequests);
    }
}
