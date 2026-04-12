package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.Renaksi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renaksi")
@Tag(name = "Individu - Renaksi", description = "Endpoint realisasi renaksi tingkat individu")
public class RenaksiController {
    private final RenaksiService renaksiService;

    public RenaksiController(RenaksiService renaksiService) {
        this.renaksiService = renaksiService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renaksi", description = "Mengambil seluruh data realisasi renaksi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> getAllRealisasiRenaksi() {
        return renaksiService.getAllRealisasiRenaksi();
    }

    @GetMapping("/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi renaksi periode RPJMD", description = "Mengambil realisasi renaksi pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> getRealisasiRenaksiByPeriodeRpjmd(
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return renaksiService.getRealisasiRenaksiByPeriodeRpjmd(tahunAwal, tahunAkhir);
    }

    @GetMapping("/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi renaksi per tahun", description = "Mengambil realisasi renaksi berdasarkan tahun.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> getRealisasiRenaksiByTahun(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun) {
        if (tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter tahun tidak boleh kosong");
        }
        return renaksiService.getRealisasiRenaksiByTahun(tahun);
    }

    @GetMapping("/by-nip/{nip}/by-bulan/{bulan}/by-rekin/{rekinId}")
    @Operation(summary = "Cari realisasi renaksi berdasarkan NIP, bulan, dan rekin", description = "Mengambil satu data realisasi renaksi berdasarkan `nip`, `bulan`, dan `rekinId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renaksi ditemukan", content = @Content(schema = @Schema(implementation = Renaksi.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Renaksi> getRealisasiRenaksiByNipBulanRekin(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Bulan realisasi", example = "Januari") @PathVariable String bulan,
            @Parameter(description = "ID rekin", example = "REKIN-001") @PathVariable String rekinId) {
        if (nip == null || nip.isBlank() || bulan == null || bulan.isBlank() || rekinId == null || rekinId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, bulan, dan rekinId tidak boleh kosong");
        }
        return renaksiService.getRealisasiRenaksiByNipBulanRekin(nip, bulan, rekinId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renaksi", description = "Menyimpan satu data realisasi renaksi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renaksi tersimpan", content = @Content(schema = @Schema(implementation = Renaksi.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Renaksi> submitRealisasiRenaksi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = RenaksiRequest.class)))
            @RequestBody @Valid RenaksiRequest renaksiRequest) {
        return renaksiService.submitRealisasiRenaksi(
                renaksiRequest.renaksiId(),
                renaksiRequest.renaksi(),
                renaksiRequest.nip(),
                renaksiRequest.rekinId(),
                renaksiRequest.rekin(),
                renaksiRequest.targetId(),
                renaksiRequest.target(),
                renaksiRequest.realisasi(),
                renaksiRequest.satuan(),
                renaksiRequest.bulan(),
                renaksiRequest.tahun(),
                renaksiRequest.jenisRealisasi()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus realisasi renaksi", description = "Menghapus satu data realisasi renaksi berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renaksi terhapus", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Void> deleteRealisasiRenaksi(
            @Parameter(description = "ID internal realisasi renaksi", example = "1") @PathVariable Long id) {
        return renaksiService.deleteRealisasiRenaksi(id);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renaksi", description = "Menyimpan beberapa data realisasi renaksi dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> batchSubmitRealisasiRenaksi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renaksi", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenaksiRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": \"1\",\n" +
                                    "    \"renaksiId\": \"RENAKSI-001\",\n" +
                                    "    \"renaksi\": \"Renaksi Peningkatan Infrastruktur\",\n" +
                                    "    \"nip\": \"198012312005011001\",\n" +
                                    "    \"rekinId\": \"REKIN-001\",\n" +
                                    "    \"rekin\": \"Rekin Peningkatan Infrastruktur\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"bulan\": \"Januari\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenaksiRequest> renaksiRequests) {
        return renaksiService.batchSubmitRealisasiRenaksi(renaksiRequests);
    }
}
