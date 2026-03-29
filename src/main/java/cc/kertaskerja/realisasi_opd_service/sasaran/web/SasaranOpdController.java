package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdService;
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
@RequestMapping("sasaran_opd")
@Tag(name = "OPD - Sasaran", description = "Endpoint realisasi sasaran tingkat OPD")
public class SasaranOpdController {
    private final SasaranOpdService sasaranOpdService;

    public SasaranOpdController(SasaranOpdService sasaranOpdService) {
        this.sasaranOpdService = sasaranOpdService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi sasaran OPD", description = "Mengambil seluruh data realisasi sasaran OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getAllRealisasiSasaranOpd() {
        return sasaranOpdService.getAllRealisasiSasaranOpd();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Ambil realisasi sasaran OPD berdasarkan ID", description = "Mengambil satu data realisasi sasaran OPD berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran OPD ditemukan", content = @Content(schema = @Schema(implementation = SasaranOpd.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<SasaranOpd> getRealisasiSasaranOpd(
            @Parameter(description = "ID internal realisasi sasaran OPD", example = "1") @PathVariable("id") Long id) {
        return sasaranOpdService.getRealisasiSasaranOpdById(id);
    }

    @GetMapping("/by-sasaran/{sasaranId}")
    @Operation(summary = "Cari realisasi sasaran OPD berdasarkan ID sasaran", description = "Mengambil daftar realisasi sasaran OPD berdasarkan `sasaranId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdBySasaranOpdId(
            @Parameter(description = "ID sasaran", example = "SAS-001") @PathVariable String sasaranId) {
        return sasaranOpdService.getRealisasiSasaranOpdBySasaranId(sasaranId);
    }

    @GetMapping("/{kodeOpd}")
    @Operation(summary = "Cari realisasi sasaran OPD berdasarkan kode OPD", description = "Mengambil seluruh realisasi sasaran untuk satu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
        return sasaranOpdService.getRealisasiSasaranOpdByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi sasaran OPD per tahun", description = "Mengambil realisasi sasaran OPD berdasarkan kode OPD dan tahun, dapat difilter lagi dengan `sasaranId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByTahunAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID sasaran", example = "SAS-001") @RequestParam(required = false) String sasaranId) {
        if (sasaranId != null && !sasaranId.isBlank()) {
            return sasaranOpdService.getRealisasiSasaranOpdByTahunAndSasaranIdAndKodeOpd(tahun, sasaranId, kodeOpd);
        }
        return sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi sasaran OPD periode RPJMD", description = "Mengambil realisasi sasaran OPD pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByPeriodeRpjmd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return sasaranOpdService.getRealisasiSasaranOpdByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @GetMapping("/by-indikator/{indikatorId}")
    @Operation(summary = "Cari realisasi sasaran OPD berdasarkan indikator", description = "Mengambil realisasi sasaran OPD berdasarkan `indikatorId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByIndikatorId(
            @Parameter(description = "ID indikator", example = "IND-SAS-123") @PathVariable String indikatorId) {
        return sasaranOpdService.getRealisasiSasaranOpdByIndikatorId(indikatorId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi sasaran OPD", description = "Menyimpan satu data realisasi sasaran OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran OPD tersimpan", content = @Content(schema = @Schema(implementation = SasaranOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranOpd> submitRealisasiSasaranOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi sasaran OPD", required = true,
                    content = @Content(schema = @Schema(implementation = SasaranOpdRequest.class)))
            @RequestBody @Valid SasaranOpdRequest sasaranOpdRequest) {
        return sasaranOpdService.submitRealisasiSasaranOpd(
                sasaranOpdRequest.sasaranId(),
                sasaranOpdRequest.indikatorId(),
                sasaranOpdRequest.targetId(),
                sasaranOpdRequest.target(),
                sasaranOpdRequest.realisasi(),
                sasaranOpdRequest.satuan(),
                sasaranOpdRequest.tahun(),
                sasaranOpdRequest.jenisRealisasi(),
                sasaranOpdRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi sasaran OPD", description = "Menyimpan beberapa data realisasi sasaran OPD dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> batchSubmitRealisasiSasaranOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi sasaran OPD", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpdRequest.class))))
            @RequestBody @Valid List<SasaranOpdRequest> sasaranOpdRequests) {
        return sasaranOpdService.batchSubmitRealisasiSasaranOpd(sasaranOpdRequests);
    }
}
