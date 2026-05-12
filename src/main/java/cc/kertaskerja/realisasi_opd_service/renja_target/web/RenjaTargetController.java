package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetService;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiService;
import cc.kertaskerja.renja.domain.JenisRenja;
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
    private final RenjaOpdHierarkiService renjaOpdHierarkiService;

    public RenjaTargetController(RenjaTargetService renjaTargetService, RenjaOpdHierarkiService renjaOpdHierarkiService) {
        this.renjaTargetService = renjaTargetService;
        this.renjaOpdHierarkiService = renjaOpdHierarkiService;
    }

    @GetMapping("/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja target berdasarkan kode OPD, tahun, dan bulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaOpdHierarkiResponse> getRealisasiRenjaTargetByFilters(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun") @PathVariable String tahun,
            @Parameter(description = "Bulan") @PathVariable String bulan
    ) {
        return renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan(
                kodeOpd,
                tahun,
                bulan,
                RenjaOpdHierarkiService.DataSource.TARGET
        );
    }

    @GetMapping("/kodeOpd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja}/by-jenis-renja-id/{jenisRenjaId}")
    @Operation(summary = "Ambil realisasi renja target berdasarkan filter lengkap")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realizations renja target", content = @Content(schema = @Schema(implementation = RenjaTarget.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenjaTarget> getRealisasiRenjaTargetByFilters(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun") @PathVariable String tahun,
            @Parameter(description = "Bulan") @PathVariable String bulan,
            @Parameter(description = "Jenis renja") @PathVariable JenisRenja jenisRenja,
            @Parameter(description = "Kode renja") @PathVariable String kodeRenja,
            @Parameter(description = "ID jenis renja") @PathVariable String jenisRenjaId
    ) {
        return renjaTargetService.getRealisasiRenjaTargetByFilters(kodeOpd, tahun, bulan, jenisRenja, kodeRenja, jenisRenjaId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja target (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi renja target OPD.")
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
                renjaTargetRequest.jenisRenjaId(),
                renjaTargetRequest.jenisRenja(),
                renjaTargetRequest.indikatorId(),
                renjaTargetRequest.indikator(),
                renjaTargetRequest.targetId(),
                renjaTargetRequest.target(),
                renjaTargetRequest.realisasi(),
                renjaTargetRequest.satuan(),
                renjaTargetRequest.tahun(),
                renjaTargetRequest.bulan(),
                renjaTargetRequest.jenisRealisasi(),
                renjaTargetRequest.kodeOpd(),
                renjaTargetRequest.kodeRenja()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja target", description = "Menyimpan beberapa data realizations renja target dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTarget.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTarget> batchSubmitRealisasiRenjaTarget(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realizations renja target", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenjaTargetRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": 10,\n" +
                                    "    \"jenisRenjaId\": \"REN-001\",\n" +
                                    "    \"jenisRenja\": \"PROGRAM\",\n" +
                                    "    \"indikatorId\": \"IND-REN-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian program\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"1\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\",\n" +
                                    "    \"kodeOpd\": \"OPD-001\",\n" +
                                    "    \"kodeRenja\": \"5\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaTargetRequest> renjaTargetRequests) {
        return renjaTargetService.batchSubmitRealisasiRenjaTarget(renjaTargetRequests);
    }

@DeleteMapping("/{jenisRenjaId}")
    @Operation(summary = "Hapus realizations renja target (belum digunakan di endpoint realisasi)", description = "Menghapus satu data realizations renja target berdasarkan ID. (belum digunakan di endpoint realisasi)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realizations renja target terhapus", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Void> deleteRealisasiRenjaTarget(
            @Parameter(description = "ID jenis renja", example = "REN-001") @PathVariable String jenisRenjaId) {
        return renjaTargetService.deleteRealisasiRenjaTarget(jenisRenjaId);
    }
}
