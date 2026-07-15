package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
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
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi") @PathVariable String bulan) {
        return sasaranService.getAllRealisasiSasaranByTahunAndBulan(tahun, bulan);
    }

    @GetMapping("/laporan/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi sasaran per periode", description = "Mengambil total realisasi sasaran yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi sasaran", content = @Content(schema = @Schema(implementation = LaporanRealisasiSasaranResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiSasaranResponse> getLaporanRealisasi(
            @Parameter(description = "Tahun laporan", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        return sasaranService.getLaporanRealisasi(tahun, jenisLaporan, bulan);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi sasaran", description = "Menyimpan satu data realisasi sasaran via JSON.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran tersimpan", content = @Content(schema = @Schema(implementation = Sasaran.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Sasaran> submitRealisasiSasaran(
            @RequestBody @Valid SasaranRequest sasaranRequest) {
        return sasaranService.submitRealisasiSasaran(sasaranRequest);
    }

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file bukti pendukung", description = "Mengunggah file dan mengembalikan string URL.")
    public Mono<java.util.Map<String, String>> uploadFile(
            @Parameter(description = "File yang akan diupload", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") FilePart file) {
        return sasaranService.uploadFile(file)
                .map(url -> java.util.Map.of("url", url));
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
