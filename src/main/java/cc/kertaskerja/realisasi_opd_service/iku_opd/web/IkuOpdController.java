package cc.kertaskerja.realisasi_opd_service.iku_opd.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kertaskerja.realisasi_opd_service.iku_opd.domain.IkuOpd;
import cc.kertaskerja.realisasi_opd_service.iku_opd.domain.IkuOpdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("iku_opd")
@Tag(name = "OPD - IKU", description = "Endpoint realisasi IKU tingkat OPD")
public class IkuOpdController {
   private final IkuOpdService ikuOpdService;

    public IkuOpdController(IkuOpdService ikuOpdService) {
        this.ikuOpdService = ikuOpdService;
    }

    @GetMapping("/{kodeOpd}")
    @Operation(summary = "Ambil realisasi IKU OPD berdasarkan kode OPD", description = "Mengambil seluruh realisasi IKU untuk satu OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi IKU OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = IkuOpd.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<IkuOpd> getAllRealisasiIkuByKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
       return ikuOpdService.getAllIkuOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi IKU OPD per tahun", description = "Mengambil realisasi IKU berdasarkan kode OPD dan tahun.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi IKU OPD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = IkuOpd.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<IkuOpd> getAllRealisasiIkuByTahunAndKodeOpd(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd) {
        return ikuOpdService.getAllIkuOpdByTahunAndKodeOpd(tahun, kodeOpd);
    }
}
