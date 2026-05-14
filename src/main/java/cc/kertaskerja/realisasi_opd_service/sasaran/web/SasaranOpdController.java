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

@RestController
@RequestMapping("sasaran_opd")
@Tag(name = "OPD - Sasaran", description = "Endpoint realisasi sasaran tingkat OPD. Role `level_1`, `level_2`, `level_3`, dan `level_4` hanya diizinkan mengakses endpoint `GET` pada resource ini.")
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

    @GetMapping("/by-renja/{renjaId}")
    @Operation(summary = "Cari realisasi sasaran OPD berdasarkan ID renja", description = "Mengambil daftar realisasi sasaran OPD berdasarkan `renjaId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByRenjaId(
            @Parameter(description = "ID renja", example = "REN-001") @PathVariable String renjaId) {
        return sasaranOpdService.getRealisasiSasaranOpdByRenjaId(renjaId);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran OPD per tahun dan bulan", description = "Mengambil realisasi sasaran OPD berdasarkan kode OPD, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranOpd> getRealisasiSasaranOpdByTahunAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return sasaranOpdService.getRealisasiSasaranOpdByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
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
    @Operation(summary = "Simpan realisasi sasaran OPD", description = "Menyimpan satu data realisasi sasaran OPD. Role `level_1`, `level_2`, `level_3`, dan `level_4` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran OPD tersimpan", content = @Content(schema = @Schema(implementation = SasaranOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role level_1, level_2, level_3, dan level_4", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranOpd> submitRealisasiSasaranOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi sasaran OPD", required = true,
                    content = @Content(schema = @Schema(implementation = SasaranOpdRequest.class)))
            @RequestBody @Valid SasaranOpdRequest sasaranOpdRequest) {
return sasaranOpdService.submitRealisasiSasaranOpd(
                sasaranOpdRequest.renjaId(),
                sasaranOpdRequest.indikatorId(),
                sasaranOpdRequest.targetId(),
                sasaranOpdRequest.target(),
                sasaranOpdRequest.realisasi(),
                sasaranOpdRequest.satuan(),
                sasaranOpdRequest.tahun(),
                sasaranOpdRequest.bulan(),
                sasaranOpdRequest.jenisRealisasi(),
                sasaranOpdRequest.kodeOpd(),
                sasaranOpdRequest.rumusPerhitungan(),
                sasaranOpdRequest.sumberData(),
                sasaranOpdRequest.definisiOperational()
        );
    }

}
