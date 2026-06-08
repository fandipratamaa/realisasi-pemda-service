package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdService;
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
@RequestMapping("sasaran_opd")
@Tag(name = "OPD - Sasaran", description = "Endpoint realisasi sasaran tingkat OPD. Role `level_1`, `level_2`, `level_3`, dan `level_4` hanya diizinkan mengakses endpoint `GET` pada resource ini.")
public class SasaranOpdController {
    private final SasaranOpdService sasaranOpdService;

    public SasaranOpdController(
            SasaranOpdService sasaranOpdService
    ) {
        this.sasaranOpdService = sasaranOpdService;
    }

//     @GetMapping("/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
//     @Operation(summary = "Cari realisasi sasaran OPD per tahun dan bulan", description = "Mengambil realisasi sasaran OPD berdasarkan kode OPD, tahun, dan bulan.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(schema = @Schema(implementation = SasaranOpdSubmitListResponse.class))),
//             @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
//             @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
//     })
//     public Mono<SasaranOpdSubmitListResponse> getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(
//             @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
//             @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
//             @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
//         return sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
//                 .collectList()
//                 .map(items -> new SasaranOpdSubmitListResponse(kodeOpd, Integer.parseInt(tahun), items));
//     }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi sasaran OPD", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi sasaran OPD berdasarkan kode OPD dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanSasaranOpdListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanSasaranOpdListResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return sasaranOpdService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang sasaran OPD", description = "Memperbarui hanya field faktor_penunjang pada record SasaranOpd yang cocok dengan composite key (kodeOpd, kodeSasaranOpd, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = SasaranOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sasaran OPD tidak ditemukan", content = @Content)
    })
    public Mono<SasaranOpd> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang sasaran OPD", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangSasaranOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangSasaranOpdRequest req) {
        return sasaranOpdService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat sasaran OPD", description = "Memperbarui hanya field faktor_penghambat pada record SasaranOpd yang cocok dengan composite key (kodeOpd, kodeSasaranOpd, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = SasaranOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sasaran OPD tidak ditemukan", content = @Content)
    })
    public Mono<SasaranOpd> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat sasaran OPD", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatSasaranOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatSasaranOpdRequest req) {
        return sasaranOpdService.updateFaktorPenghambat(req);
    }

}
