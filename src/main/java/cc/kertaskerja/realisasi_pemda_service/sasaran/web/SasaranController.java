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
        return sasaranService.submitRealisasiSasaran(sasaranRequest);
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

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang sasaran", description = "Memperbarui hanya field faktor_penunjang pada record Sasaran yang cocok dengan composite key (sasaranId, indikatorId, targetId, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Sasaran.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sasaran tidak ditemukan", content = @Content)
    })
    public Mono<Sasaran> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangSasaranRequest.class)))
            @RequestBody @Valid FaktorPenunjangSasaranRequest req) {
        return sasaranService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat sasaran", description = "Memperbarui hanya field faktor_penghambat pada record Sasaran yang cocok dengan composite key (sasaranId, indikatorId, targetId, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Sasaran.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sasaran tidak ditemukan", content = @Content)
    })
    public Mono<Sasaran> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatSasaranRequest.class)))
            @RequestBody @Valid FaktorPenghambatSasaranRequest req) {
        return sasaranService.updateFaktorPenghambat(req);
    }
}
