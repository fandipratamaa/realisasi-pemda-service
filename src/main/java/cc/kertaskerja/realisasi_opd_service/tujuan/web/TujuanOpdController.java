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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("tujuan_opd")
@Tag(name = "OPD - Tujuan", description = "Endpoint realisasi tujuan tingkat OPD")
public class TujuanOpdController {
    private final TujuanOpdService tujuanOpdService;

    public TujuanOpdController(TujuanOpdService tujuanOpdService) {
        this.tujuanOpdService = tujuanOpdService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi tujuan OPD", description = "Mengambil seluruh data realisasi tujuan OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getAllRealisasiTujuanOpd() {
        return tujuanOpdService.getAllRealisasiTujuanOpd();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Ambil realisasi tujuan OPD berdasarkan ID", description = "Mengambil satu data realisasi tujuan OPD berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan OPD ditemukan", content = @Content(schema = @Schema(implementation = TujuanOpd.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<TujuanOpd> getRealisasiTujuanOpd(
            @Parameter(description = "ID internal realisasi tujuan OPD", example = "1") @PathVariable("id") Long id) {
        return tujuanOpdService.getRealisasiTujuanOpdById(id);
    }

    @GetMapping("/by-tujuan/{tujuanId}")
    @Operation(summary = "Cari realisasi tujuan OPD berdasarkan ID tujuan", description = "Mengambil daftar realisasi tujuan OPD berdasarkan `tujuanId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getRealisasiTujuanOpdByTujuanId(
            @Parameter(description = "ID tujuan", example = "TUJ-123") @PathVariable String tujuanId) {
        return tujuanOpdService.getRealisasiTujuanOpdByTujuanId(tujuanId);
    }

    @GetMapping("/{kodeOpd}")
    @Operation(summary = "Cari realisasi tujuan OPD berdasarkan kode OPD", description = "Mengambil seluruh realisasi tujuan untuk satu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getRealisasiTujuanOpdByKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
        return tujuanOpdService.getRealisasiTujuanOpdByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi tujuan OPD per tahun", description = "Mengambil realisasi tujuan OPD berdasarkan kode OPD dan tahun, dapat difilter lagi dengan `tujuanId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID tujuan", example = "TUJ-123") @RequestParam(required = false) String tujuanId) {
        if (tujuanId != null && !tujuanId.isBlank()) {
            return tujuanOpdService.getRealisasiTujuanOpdByTahunAndTujuanIdAndKodeOpd(tahun, tujuanId, kodeOpd);
        }
        return tujuanOpdService.getRealisasiTujuanOpdByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi tujuan OPD periode RPJMD", description = "Mengambil realisasi tujuan OPD pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getRealisasiTujuanOpdByPeriodeRpjmd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return tujuanOpdService.getRealisasiTujuanOpdByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @GetMapping("/by-indikator/{indikatorId}")
    @Operation(summary = "Cari realisasi tujuan OPD berdasarkan indikator", description = "Mengambil realisasi tujuan OPD berdasarkan `indikatorId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> getRealisasiTujuanOpdByIndikatorId(
            @Parameter(description = "ID indikator", example = "IND-TUJ-123") @PathVariable String indikatorId) {
        return tujuanOpdService.getRealisasiTujuanOpdByIndikatorId(indikatorId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi tujuan OPD", description = "Menyimpan satu data realisasi tujuan OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan OPD tersimpan", content = @Content(schema = @Schema(implementation = TujuanOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<TujuanOpd> submitRealisasiTujuanOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi tujuan OPD", required = true,
                    content = @Content(schema = @Schema(implementation = TujuanOpdRequest.class)))
            @RequestBody @Valid TujuanOpdRequest tujuanOpdRequest) {
        return tujuanOpdService.submitRealisasiTujuanOpd(
                tujuanOpdRequest.tujuanId(),
                tujuanOpdRequest.indikatorId(),
                tujuanOpdRequest.targetId(),
                tujuanOpdRequest.target(),
                tujuanOpdRequest.realisasi(),
                tujuanOpdRequest.satuan(),
                tujuanOpdRequest.tahun(),
                tujuanOpdRequest.jenisRealisasi(),
                tujuanOpdRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi tujuan OPD", description = "Menyimpan beberapa data realisasi tujuan OPD dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<TujuanOpd> batchSubmitRealisasiTujuanOpd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi tujuan OPD", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TujuanOpdRequest.class))))
            @RequestBody @Valid List<TujuanOpdRequest> tujuanOpdRequests) {
        return tujuanOpdService.batchSubmitRealisasiTujuanOpd(tujuanOpdRequests);
    }
}
