package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.Tujuan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanService;
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
@RequestMapping("tujuans")
@Tag(name = "Pemda - Tujuan", description = "Endpoint realisasi tujuan tingkat pemda")
public class TujuanController {
    private final TujuanService tujuanService;

    public TujuanController(TujuanService tujuanService) {
        this.tujuanService = tujuanService;
    }

    @GetMapping("/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi tujuan per tahun dan bulan", description = "Mengambil realisasi tujuan berdasarkan tahun dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realization tujuan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getRealisasiTujuanByTahunAndBulan(
            @Parameter(description = "Tahun realization", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realization", example = "Januari") @PathVariable String bulan) {
        return tujuanService.getRealisasiTujuanByTahunAndBulan(tahun, bulan);
    }

    @GetMapping("/laporan/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi tujuan per periode", description = "Mengambil total realisasi tujuan yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi tujuan", content = @Content(schema = @Schema(implementation = LaporanRealisasiTujuanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiTujuanResponse> getLaporanRealisasi(
            @Parameter(description = "Tahun laporan", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        return tujuanService.getLaporanRealisasi(tahun, jenisLaporan, bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi tujuan", description = "Menyimpan satu data realisasi tujuan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan tersimpan", content = @Content(schema = @Schema(implementation = Tujuan.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Tujuan> submitRealisasiTujuan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi tujuan", required = true,
                    content = @Content(schema = @Schema(implementation = TujuanRequest.class)))
            @RequestBody @Valid TujuanRequest tujuanRequest) {
        return tujuanService.submitRealisasiTujuan(tujuanRequest);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi tujuan", description = "Menyimpan beberapa data realisasi tujuan dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> batchSubmitRealisasiTujuan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi tujuan", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanRequest.class))))
            @RequestBody @Valid List<TujuanRequest> tujuanRequests) {
        return tujuanService.batchSubmitRealisasiTujuan(tujuanRequests);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang tujuan", description = "Memperbarui hanya field faktor_penunjang pada record Tujuan yang cocok dengan composite key (tujuanId, indikatorId, targetId, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Tujuan.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan tidak ditemukan", content = @Content)
    })
    public Mono<Tujuan> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRequest.class)))
            @RequestBody @Valid FaktorPenunjangRequest req) {
        return tujuanService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat tujuan", description = "Memperbarui hanya field faktor_penghambat pada record Tujuan yang cocok dengan composite key (tujuanId, indikatorId, targetId, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Tujuan.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan tidak ditemukan", content = @Content)
    })
    public Mono<Tujuan> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRequest.class)))
            @RequestBody @Valid FaktorPenghambatRequest req) {
        return tujuanService.updateFaktorPenghambat(req);
    }
}
