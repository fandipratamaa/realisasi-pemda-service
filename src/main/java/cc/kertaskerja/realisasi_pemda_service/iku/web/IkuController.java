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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang IKU", description = "Memperbarui faktor penunjang pada Tujuan atau Sasaran berdasarkan jenis IKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Iku.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Iku> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang IKU", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangIkuRequest.class)))
            @RequestBody @Valid FaktorPenunjangIkuRequest req) {
        return ikuService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat IKU", description = "Memperbarui faktor penghambat pada Tujuan atau Sasaran berdasarkan jenis IKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Iku.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Iku> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat IKU", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatIkuRequest.class)))
            @RequestBody @Valid FaktorPenghambatIkuRequest req) {
        return ikuService.updateFaktorPenghambat(req);
    }
}
