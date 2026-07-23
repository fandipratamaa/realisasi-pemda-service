package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Pemda - Tujuan", description = "Endpoint realisasi tujuan tingkat pemda")
public class TujuanController {
    private final TujuanService tujuanService;

    public TujuanController(TujuanService tujuanService) {
        this.tujuanService = tujuanService;
    }

    @GetMapping("/tujuans/by-tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi tujuan", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi tujuan berdasarkan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanTujuanPemdaListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanTujuanPemdaListResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return tujuanService.getPenetapanWithRealisasi(Integer.parseInt(tahun), bulan);
    }

    @PostMapping("/pemda/tujuan/sync")
    @Operation(summary = "Sinkronisasi tujuan", description = "Memicu sinkronisasi data tujuan dari service penetapan dan langsung mengembalikan data terbarunya.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan ter-sinkronisasi dan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanTujuanPemdaListResponse.class))),
            @ApiResponse(responseCode = "422", description = "Tidak ada data penetapan yang siap disinkronkan", content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<ResponseEntity<Object>> syncTujuanPemda(
            @Parameter(description = "Tahun", example = "2026") @RequestParam String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return tujuanService.syncPenetapanTujuanPemda(Integer.parseInt(tahun))
                .then(tujuanService.getPenetapanWithRealisasi(Integer.parseInt(tahun), bulan))
                .map(res -> ResponseEntity.ok().body((Object) res))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.unprocessableEntity().body(
                                (Object) java.util.Map.of("error", "tidak ada data penetapan yang siap disinkronkan")
                        )
                ));
    }

    @GetMapping("/tujuans/laporan/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
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

    @PostMapping(value = "/tujuans", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi tujuan", description = "Menyimpan satu data realisasi tujuan via JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan tersimpan", content = @Content(schema = @Schema(implementation = TujuanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<TujuanResponse> submitRealisasiTujuan(
            @RequestBody @Valid TujuanRequest tujuanRequest) {
        return tujuanService.submitRealisasiTujuan(tujuanRequest);
    }

    @PostMapping(value = "/tujuans/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file bukti pendukung", description = "Mengunggah file dan mengembalikan string URL.")
    public Mono<java.util.Map<String, String>> uploadFile(
            @Parameter(description = "File yang akan diupload", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") FilePart file) {
        return tujuanService.uploadFile(file)
                .map(url -> java.util.Map.of("url", url));
    }

    @PostMapping("/tujuans/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang tujuan", description = "Memperbarui hanya field faktor_penunjang pada record Tujuan yang cocok dengan composite key (kodeTujuanPemda, kodeIndikator, kodeTarget, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TujuanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan tidak ditemukan", content = @Content)
    })
    public Mono<TujuanResponse> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRequest.class)))
            @RequestBody @Valid FaktorPenunjangRequest req) {
        return tujuanService.updateFaktorPenunjang(req);
    }

    @PostMapping("/tujuans/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat tujuan", description = "Memperbarui hanya field faktor_penghambat pada record Tujuan yang cocok dengan composite key (kodeTujuanPemda, kodeIndikator, kodeTarget, tahun, bulan).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TujuanResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tujuan tidak ditemukan", content = @Content)
    })
    public Mono<TujuanResponse> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRequest.class)))
            @RequestBody @Valid FaktorPenghambatRequest req) {
        return tujuanService.updateFaktorPenghambat(req);
    }
}
