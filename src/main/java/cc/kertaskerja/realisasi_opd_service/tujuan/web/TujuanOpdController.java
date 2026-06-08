package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpdService;
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
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("tujuan_opd")
@Tag(name = "OPD - Tujuan", description = "Endpoint realisasi tujuan tingkat OPD. Role `level_1`, `level_2`, `level_3`, dan `level_4` hanya diizinkan mengakses endpoint `GET` pada resource ini.")
public class TujuanOpdController {
    private final TujuanOpdService tujuanOpdService;

    public TujuanOpdController(
            TujuanOpdService tujuanOpdService
    ) {
        this.tujuanOpdService = tujuanOpdService;
    }

//     @GetMapping("/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
//     @Operation(summary = "Cari realisasi tujuan OPD per tahun dan bulan", description = "Mengambil realisasi tujuan OPD berdasarkan kode OPD, tahun, dan bulan.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(schema = @Schema(implementation = TujuanOpdSubmitListResponse.class))),
//             @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
//             @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
//     })
//     public Mono<TujuanOpdSubmitListResponse> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(
//             @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
//             @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
//             @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
//         return tujuanOpdService.getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan)
//                 .collectList()
//                 .map(items -> new TujuanOpdSubmitListResponse(kodeOpd, Integer.parseInt(tahun), items));
//     }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi tujuan OPD", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi tujuan OPD berdasarkan kode OPD dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanTujuanOpdListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanTujuanOpdListResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return tujuanOpdService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi tujuan OPD", description = "Menyimpan satu data realisasi tujuan OPD. Role `level_1`, `level_2`, `level_3`, dan `level_4` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan OPD tersimpan", content = @Content(schema = @Schema(implementation = TujuanOpdSubmitListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role level_1, level_2, level_3, dan level_4", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<TujuanOpdSubmitListResponse> submitRealisasiTujuanOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi tujuan OPD", required = true,
                    content = @Content(schema = @Schema(implementation = TujuanOpdRequest.class)))
            @RequestBody @Valid TujuanOpdRequest tujuanOpdRequest) {
        return tujuanOpdService.submitRealisasiTujuanOpd(tujuanOpdRequest)
                .map(response -> new TujuanOpdSubmitListResponse(
                        response.kodeOpd(),
                        response.tahun(),
                        List.of(response)
                ));
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang tujuan OPD", description = "Memperbarui hanya field faktor_penunjang pada record TujuanOpd yang cocok dengan composite key (kodeOpd, kodeTujuanOpd, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TujuanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan OPD tidak ditemukan", content = @Content)
    })
    public Mono<TujuanOpd> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang tujuan OPD", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangTujuanOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangTujuanOpdRequest req) {
        return tujuanOpdService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat tujuan OPD", description = "Memperbarui hanya field faktor_penghambat pada record TujuanOpd yang cocok dengan composite key (kodeOpd, kodeTujuanOpd, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TujuanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan OPD tidak ditemukan", content = @Content)
    })
    public Mono<TujuanOpd> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat tujuan OPD", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatTujuanOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatTujuanOpdRequest req) {
        return tujuanOpdService.updateFaktorPenghambat(req);
    }

    @PostMapping("/create/batch")
    @Operation(summary = "Simpan batch realisasi tujuan OPD", description = "Menyimpan beberapa data realisasi tujuan OPD dalam satu request. Role `level_1`, `level_2`, `level_3`, dan `level_4` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(schema = @Schema(implementation = TujuanOpdSubmitListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role level_1, level_2, level_3, dan level_4", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<TujuanOpdSubmitListResponse> batchSubmitRealisasiTujuanOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi tujuan OPD", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpdRequest.class))))
            @RequestBody @Valid List<TujuanOpdRequest> tujuanOpdRequests) {
        return tujuanOpdService.batchSubmitRealisasiTujuanOpd(tujuanOpdRequests)
                .collectList()
                .map(items -> {
                    String kodeOpd = items.isEmpty() ? null : items.getFirst().kodeOpd();
                    Integer tahun = items.isEmpty() ? null : items.getFirst().tahun();
                    return new TujuanOpdSubmitListResponse(kodeOpd, tahun, items);
                });
    }

}
