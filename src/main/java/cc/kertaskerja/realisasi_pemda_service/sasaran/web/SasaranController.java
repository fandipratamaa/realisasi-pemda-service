package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.Sasaran;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.SasaranService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("sasarans")
@Tag(name = "Pemda - Sasaran", description = "Endpoint realisasi sasaran tingkat pemda")
public class SasaranController {
    private final SasaranService sasaranService;

    public SasaranController(SasaranService sasaranService) {
        this.sasaranService = sasaranService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Ambil realisasi sasaran berdasarkan ID", description = "Mengambil satu data realisasi sasaran untuk kebutuhan detail/edit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran ditemukan", content = @Content(schema = @Schema(implementation = Sasaran.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Sasaran> getSasaranById(
            @Parameter(description = "ID internal realisasi sasaran", example = "1") @PathVariable Long id) {
        return sasaranService.getSasaranById(id);
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi sasaran", description = "Mengambil seluruh data realisasi sasaran pemda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> getAllRealisasiSasaran() {
        return sasaranService.getAllRealisasiSasaran();
    }

    @GetMapping("/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi sasaran per tahun", description = "Mengambil realisasi sasaran berdasarkan tahun, bisa difilter lagi dengan query param `sasaranId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> getAllRealisasiSasaranByTahun(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID sasaran", example = "SAS-001") @RequestParam(required = false) String sasaranId) {
        if (sasaranId != null && !sasaranId.isEmpty()) {
            return sasaranService.getAllRealisasiSasaranByTahunAndSasaranId(tahun, sasaranId);
        }
return sasaranService.getAllRealisasiSasaranByTahun(tahun);
    }

@GetMapping("/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran per tahun dan bulan", description = "Mengambil realisasi sasaran berdasarkan tahun dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> getAllRealisasiSasaranByTahunAndBulan(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "Januari") @PathVariable String bulan) {
        return sasaranService.getAllRealisasiSasaranByTahunAndBulan(tahun, bulan);
    }

    @GetMapping("/by-sasaran/{sasaranId}")
    @Operation(summary = "Cari realisasi sasaran berdasarkan ID sasaran", description = "Mengambil daftar realisasi berdasarkan `sasaranId` dari sistem sumber.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> getAllRealisasiSasaranBySasaranId(
            @Parameter(description = "ID sasaran dari sistem sumber", example = "SAS-001") @PathVariable String sasaranId) {
        return sasaranService.getAllRealisasiSasaranBySasaranId(sasaranId);
    }

    @GetMapping("/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi sasaran periode RPJMD", description = "Mengambil realisasi sasaran pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> getRealisasiSasaranByPeriodeRpjmd(
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return sasaranService.getRealisasiSasaranByPeriodeRpjmd(tahunAwal, tahunAkhir);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi sasaran", description = "Menyimpan satu data realisasi sasaran.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran tersimpan", content = @Content(schema = @Schema(implementation = Sasaran.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
public Mono<Sasaran> submitRealisasiSasaran(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi sasaran", required = true,
                    content = @Content(schema = @Schema(implementation = SasaranRequest.class)))
            @RequestBody @Valid SasaranRequest sasaranRequest) {
        return sasaranService.submitRealisasiSasaran(
                sasaranRequest.sasaranId(),
                sasaranRequest.indikatorId(),
                sasaranRequest.targetId(),
                sasaranRequest.target(),
                sasaranRequest.realisasi(),
                sasaranRequest.satuan(),
                sasaranRequest.tahun(),
                sasaranRequest.bulan(),
                sasaranRequest.jenisRealisasi()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi sasaran", description = "Menyimpan beberapa data realisasi sasaran dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sasaran.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Sasaran> batchSubmitRealisasiSasaran(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi sasaran", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranRequest.class))))
            @RequestBody @Valid List<SasaranRequest> sasaranRequest) {
        return sasaranService.batchSubmitRealisasiSasaran(sasaranRequest);
    }
}
