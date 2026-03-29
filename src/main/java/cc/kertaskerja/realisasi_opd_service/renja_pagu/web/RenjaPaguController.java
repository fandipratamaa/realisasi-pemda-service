package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import java.util.List;

import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_pagu")
@Tag(name = "OPD - Renja Pagu", description = "Endpoint realisasi renja pagu tingkat OPD")
public class RenjaPaguController {
    private final RenjaPaguService renjaPaguService;

    public RenjaPaguController(RenjaPaguService renjaPaguService) {
        this.renjaPaguService = renjaPaguService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renja pagu", description = "Mengambil seluruh data realisasi renja pagu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getAllRealisasiRenjaPagu() {
        return renjaPaguService.getAllRealisasiRenjaPagu();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Ambil realisasi renja pagu berdasarkan ID", description = "Mengambil satu data realisasi renja pagu berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja pagu ditemukan", content = @Content(schema = @Schema(implementation = RenjaPagu.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenjaPagu> getRealisasiRenjaPagu(
            @Parameter(description = "ID internal realisasi renja pagu", example = "1") @PathVariable Long id) {
        return renjaPaguService.getRealisasiRenjaPaguById(id);
    }

    @GetMapping("/by-renjaPagu/{renjaPaguId}")
    @Operation(summary = "Cari realisasi renja pagu berdasarkan ID renja pagu", description = "Mengambil daftar realisasi renja pagu berdasarkan `renjaPaguId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getRealisasiRenjaPaguByRenjaPaguId(
            @Parameter(description = "ID renja pagu", example = "RENPAGU-001") @PathVariable String renjaPaguId) {
        return renjaPaguService.getRealisasiRenjaPaguByRenjaPaguId(renjaPaguId);
    }

    @GetMapping("/{kodeOpd}")
    @Operation(summary = "Cari realisasi renja pagu berdasarkan kode OPD", description = "Mengambil seluruh realisasi renja pagu untuk satu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getRealisasiRencanaPaguByKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
        return renjaPaguService.getRealisasiRenjaPaguByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi renja pagu per tahun", description = "Mengambil realisasi renja pagu berdasarkan kode OPD dan tahun, dapat difilter lagi dengan `renjaPaguId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getRealisasiRenjaPaguByTahunAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID renja pagu", example = "RENPAGU-001") @RequestParam(required = false) String renjaPaguId) {
        if (renjaPaguId != null && !renjaPaguId.isBlank()) {
            return renjaPaguService.getRealisasiRenjaPaguByTahunAndRenjaPaguIdAndKodeOpd(tahun, renjaPaguId, kodeOpd);
        }
        return renjaPaguService.getRealisasiRenjaPaguByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi renja pagu periode RPJMD", description = "Mengambil realisasi renja pagu pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getRealisasiRenjaPaguByPeriodeRpjmd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return renjaPaguService.getRealisasiRenjaPaguByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja pagu", description = "Menyimpan satu data realisasi renja pagu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja pagu tersimpan", content = @Content(schema = @Schema(implementation = RenjaPagu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaPagu> submitRealisasiRenjaPagu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja pagu", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaPaguRequest.class)))
            @RequestBody @Valid RenjaPaguRequest renjaPaguRequest) {
        return renjaPaguService.submitRealisasiRenjaPagu(
                renjaPaguRequest.renjaPaguId(),
                renjaPaguRequest.renjaPagu(),
                renjaPaguRequest.jenisRenjaPagu(),
                renjaPaguRequest.pagu(),
                renjaPaguRequest.realisasi(),
                renjaPaguRequest.satuan(),
                renjaPaguRequest.tahun(),
                renjaPaguRequest.jenisRealisasi(),
                renjaPaguRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja pagu", description = "Menyimpan beberapa data realisasi renja pagu dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> batchSubmitRealisasiRenjaPagu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renja pagu", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenjaPaguRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": 10,\n" +
                                    "    \"renjaPaguId\": \"RENPAGU-001\",\n" +
                                    "    \"renjaPagu\": \"Program Pembangunan Jalan\",\n" +
                                    "    \"jenisRenjaPagu\": \"PROGRAM\",\n" +
                                    "    \"pagu\": 100000000,\n" +
                                    "    \"realisasi\": 70000000,\n" +
                                    "    \"satuan\": \"Rp\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\",\n" +
                                    "    \"kodeOpd\": \"OPD-001\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaPaguRequest> renjaPaguRequests) {
        return renjaPaguService.batchSubmitRealisasiRenjaPagu(renjaPaguRequests);
    }
}
