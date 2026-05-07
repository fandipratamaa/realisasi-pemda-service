package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpd;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.renaksi_triwulan_response.RenaksiTriwulanRekapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renaksi_opd")
@Tag(name = "OPD - Renaksi", description = "Endpoint realisasi renaksi tingkat OPD")
public class RenaksiOpdController {
    private final RenaksiOpdService renaksiOpdService;

    public RenaksiOpdController(RenaksiOpdService renaksiOpdService) {
        this.renaksiOpdService = renaksiOpdService;
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/rekap-triwulan")
    @Operation(summary = "Rekap realisasi renaksi OPD per triwulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar rekap per triwulan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiTriwulanRekapResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenaksiTriwulanRekapResponse> getRekapTriwulanByTahun(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renaksiOpdService.getRekapTriwulanByTahun(kodeOpd, tahun);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renaksi OPD berdasarkan kode OPD, tahun, dan bulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenaksiOpd> getRealisasiRenaksiByKodeOpdTahunBulan(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiOpdService.getRealisasiRenaksiByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus realisasi renaksi OPD")
    public Mono<Void> deleteRealisasiRenaksi(@PathVariable Long id) {
        return renaksiOpdService.deleteRealisasiRenaksi(id);
    }
}
