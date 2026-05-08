package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import java.util.List;

import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguService;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_pagu")
@Tag(name = "OPD - Renja Pagu", description = "Endpoint realisasi renja pagu tingkat OPD")
public class RenjaPaguController {
    private final RenjaPaguService renjaPaguService;
    private final RenjaOpdHierarkiService renjaOpdHierarkiService;

    public RenjaPaguController(RenjaPaguService renjaPaguService, RenjaOpdHierarkiService renjaOpdHierarkiService) {
        this.renjaPaguService = renjaPaguService;
        this.renjaOpdHierarkiService = renjaOpdHierarkiService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renja pagu (belum digunakan di endpoint realisasi)", description = "Mengambil seluruh data realisasi renja pagu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getAllRealisasiRenjaPagu() {
        return renjaPaguService.getAllRealisasiRenjaPagu();
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja pagu (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi renja pagu OPD.")
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
                renjaPaguRequest.jenisRenjaId(),
                renjaPaguRequest.jenisRenja(),
                renjaPaguRequest.pagu(),
                renjaPaguRequest.realisasi(),
                renjaPaguRequest.satuan(),
                renjaPaguRequest.tahun(),
                renjaPaguRequest.bulan(),
                renjaPaguRequest.jenisRealisasi(),
                renjaPaguRequest.kodeOpd(),
                renjaPaguRequest.kodeRenja()
        );
    }

    @GetMapping("/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Ambil realisasi renja pagu berdasarkan OPD, tahun, dan bulan", description = "Mengambil data realisasi renja pagu berdasarkan kode OPD, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaOpdHierarkiResponse> getRealisasiRenjaPaguByKodeOpdAndTahunAndBulan(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        return renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan(
                kodeOpd,
                tahun,
                bulan,
                RenjaOpdHierarkiService.DataSource.PAGU
        );
    }

    @GetMapping("/kodeOpd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja}/by-jenis-renja-id/{jenisRenjaId}")
    @Operation(summary = "Ambil realisasi renja pagu berdasarkan filter lengkap", description = "Mengambil data realisasi renja pagu berdasarkan kode OPD, tahun, bulan, jenis renja, kode renja, dan renja ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPagu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPagu> getRealisasiRenjaPaguByFilterLengkap(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan,
            @Parameter(description = "Jenis renja", example = "PROGRAM") @PathVariable String jenisRenja,
            @Parameter(description = "Kode renja", example = "001") @PathVariable String kodeRenja,
            @Parameter(description = "ID jenis renja", example = "RENJA-001") @PathVariable String jenisRenjaId) {
        return renjaPaguService.getRealisasiRenjaPaguByKodeOpdAndTahunAndBulanAndJenisRenjaAndKodeRenjaAndRenjaId(
                kodeOpd, tahun, bulan, jenisRenja, kodeRenja, jenisRenjaId);
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
                                    "    \"jenisRenjaId\": \"RENPAGU-001\",\n" +
                                    "    \"jenisRenja\": \"PROGRAM\",\n" +
                                    "    \"pagu\": 100000000,\n" +
                                    "    \"realisasi\": 70000000,\n" +
                                    "    \"satuan\": \"Rp\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"1\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\",\n" +
                                    "    \"kodeOpd\": \"OPD-001\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaPaguRequest> renjaPaguRequests) {
        return renjaPaguService.batchSubmitRealisasiRenjaPagu(renjaPaguRequests);
    }

    @DeleteMapping("/{jenisRenjaId}")
    @Operation(summary = "Hapus realisasi renja pagu berdasarkan renja ID (belum digunakan di endpoint realisasi)", description = "Menghapus data realisasi renja pagu berdasarkan renja ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Data berhasil dihapus", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Void> deleteRealisasiRenjaPaguByRenjaId(
            @Parameter(description = "ID jenis renja", example = "RENJA-001") @PathVariable String jenisRenjaId) {
        return renjaPaguService.deleteRealisasiRenjaPaguByRenjaId(jenisRenjaId);
    }
}
