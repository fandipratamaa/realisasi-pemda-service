package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpd;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.detail_bulanan_response.RenaksiOpdDetailBulananResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.renaksi_triwulan_response.RenaksiTriwulanRekapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renaksi_opd")
@Tag(name = "OPD - Renaksi", description = "Endpoint realisasi renaksi tingkat OPD")
public class RenaksiOpdController {
    private final RenaksiOpdService renaksiOpdService;

    public RenaksiOpdController(RenaksiOpdService renaksiOpdService) {
        this.renaksiOpdService = renaksiOpdService;
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/rekap-triwulan")
    @Operation(summary = "Rekap realisasi renaksi OPD per triwulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar rekap per triwulan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiTriwulanRekapResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenaksiTriwulanRekapResponse> getRekapTriwulanByNipAndTahun(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun) {
        if (kodeOpd == null || kodeOpd.isBlank() || nip == null || nip.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, nip dan tahun tidak boleh kosong");
        }
        return renaksiOpdService.getRekapTriwulanByNipAndTahun(kodeOpd, nip, tahun);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/by-triwulan/{triwulan}/by-renaksi-id/{renaksiId}/by-target-id/{targetId}/detail-bulanan")
    @Operation(summary = "Detail realisasi renaksi OPD per bulan (per triwulan)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detail bulanan", content = @Content(schema = @Schema(implementation = RenaksiOpdDetailBulananResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenaksiOpdDetailBulananResponse> getDetailBulanan(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Triwulan (1-4)") @PathVariable String triwulan,
            @Parameter(description = "ID renaksi") @PathVariable String renaksiId,
            @Parameter(description = "ID target") @PathVariable String targetId
    ) {
        if (kodeOpd == null || kodeOpd.isBlank()
                || nip == null || nip.isBlank()
                || tahun == null || tahun.isBlank()
                || triwulan == null || triwulan.isBlank()
                || renaksiId == null || renaksiId.isBlank()
                || targetId == null || targetId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Parameter kodeOpd, nip, tahun, triwulan, renaksiId, dan targetId tidak boleh kosong");
        }

        return renaksiOpdService.getDetailBulanan(kodeOpd, nip, tahun, triwulan, renaksiId, targetId)
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renaksi OPD")
    public Mono<RenaksiOpd> submitRealisasiRenaksi(@RequestBody @Valid RenaksiOpdRequest request) {
        return renaksiOpdService.submitRealisasiRenaksi(request);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renaksi OPD")
    public Flux<RenaksiOpd> batchSubmitRealisasiRenaksi(@RequestBody @Valid List<RenaksiOpdRequest> requests) {
        return renaksiOpdService.batchSubmitRealisasiRenaksi(requests);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus realisasi renaksi OPD")
    public Mono<Void> deleteRealisasiRenaksi(@PathVariable Long id) {
        return renaksiOpdService.deleteRealisasiRenaksi(id);
    }
}
