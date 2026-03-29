package cc.kertaskerja.realisasi_pemda_service.iku.web;

import cc.kertaskerja.realisasi_pemda_service.iku.domain.Iku;
import cc.kertaskerja.realisasi_pemda_service.iku.domain.IkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("ikus")
@Tag(name = "Pemda - IKU", description = "Endpoint realisasi IKU tingkat pemda")
public class IkuController {
    private final IkuService ikuService;

    public IkuController(IkuService ikuService) {
        this.ikuService = ikuService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi IKU", description = "Mengambil seluruh realisasi IKU pemda.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi IKU", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Iku.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Iku> getAllRealisasiIku() {
        return ikuService.getAllIku();
    }

    @GetMapping("/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi IKU per tahun", description = "Mengambil realisasi IKU berdasarkan tahun.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi IKU", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Iku.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tahun tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Iku> getAllRealisasiIkuByTahun(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun) {
        return ikuService.getAllIkuByTahun(tahun);
    }
}
