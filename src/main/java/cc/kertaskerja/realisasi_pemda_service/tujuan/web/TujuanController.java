package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

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

    @GetMapping("{id}")
    @Operation(summary = "Ambil realisasi tujuan berdasarkan ID", description = "Mengambil satu data realisasi tujuan untuk kebutuhan detail/edit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi tujuan ditemukan", content = @Content(schema = @Schema(implementation = Tujuan.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Tujuan> getRealisasiTujuanById(
            @Parameter(description = "ID internal realisasi tujuan", example = "1") @PathVariable Long id) {
        return tujuanService.getRealisasiTujuanById(id);
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi tujuan", description = "Mengambil seluruh data realisasi tujuan pemda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getAllRealisasiTujuan() {
        return tujuanService.getAllRealisasiTujuan();
    }

    @GetMapping("/by-tujuan/{tujuanId}")
    @Operation(summary = "Cari realisasi tujuan berdasarkan ID tujuan", description = "Mengambil daftar realisasi berdasarkan `tujuanId` dari sistem perencanaan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getRealisasiTujuanByTujuanId(
            @Parameter(description = "ID tujuan dari sistem sumber", example = "TUJ-123") @PathVariable String tujuanId) {
        return tujuanService.getRealisasiTujuanByTujuanId(tujuanId);
    }

    @GetMapping("/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi tujuan per tahun", description = "Mengambil realisasi tujuan berdasarkan tahun, bisa difilter lagi dengan query param `tujuanId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getRealisasiTujuanByTahunAndOptionalTujuanId(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID tujuan", example = "TUJ-123") @RequestParam(required = false) String tujuanId) {
        if (tujuanId != null && !tujuanId.isBlank()) {
            return tujuanService.getRealisasiTujuanByTahunAndTujuanId(tahun, tujuanId);
        }
        return tujuanService.getRealisasiTujuanByTahun(tahun);
    }

    @GetMapping("/by-indikator/{indikatorId}")
    @Operation(summary = "Cari realisasi tujuan berdasarkan indikator", description = "Mengambil realisasi tujuan berdasarkan `indikatorId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getRealisasiTujuanByIndikatorId(
            @Parameter(description = "ID indikator", example = "IND-TUJ-123") @PathVariable String indikatorId) {
        return tujuanService.getRealisasiTujuanByIndikatorId(indikatorId);
    }

    @GetMapping("/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi tujuan periode RPJMD", description = "Mengambil realisasi tujuan pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi tujuan periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tujuan.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Tujuan> getRealisasiTujuanByPeriodeRpjmd(
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return tujuanService.getRealisasiTujuanByPeriodeRpjmd(tahunAwal, tahunAkhir);
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
        return tujuanService.submitRealisasiTujuan(
                tujuanRequest.tujuanId(),
                tujuanRequest.indikatorId(),
                tujuanRequest.targetId(),
                tujuanRequest.target(),
                tujuanRequest.realisasi(),
                tujuanRequest.satuan(),
                tujuanRequest.tahun(),
                tujuanRequest.jenisRealisasi()
        );
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
}
