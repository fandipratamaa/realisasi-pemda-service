package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividuService;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdResponse;
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
@RequestMapping("sasaran_individu")
@Tag(name = "Individu - Sasaran", description = "Endpoint realisasi sasaran tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class SasaranIndividuController {
    private final SasaranIndividuService sasaranIndividuService;

    public SasaranIndividuController(
            SasaranIndividuService sasaranIndividuService
    ) {
        this.sasaranIndividuService = sasaranIndividuService;
    }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran Individu per tahun dan bulan", description = "Mengambil realisasi sasaran Individu berdasarkan kode OPD, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran Individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranOpdResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividuResponse> getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi sasaran individu", description = "Menyimpan satu data realisasi sasaran individu. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran individu tersimpan", content = @Content(schema = @Schema(implementation = SasaranIndividuSubmitListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranIndividuSubmitListResponse> submitRealisasiSasaranIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi sasaran individu", required = true,
                    content = @Content(schema = @Schema(implementation = SasaranIndividuSubmitRequest.class)))
            @RequestBody @Valid SasaranIndividuSubmitRequest request) {
        return sasaranIndividuService.submitRealisasiSasaranIndividu(request)
                .map(response -> new SasaranIndividuSubmitListResponse(
                        response.kodeOpd(),
                        response.tahun(),
                        List.of(response)
                ));
    }

    @PostMapping("/create/batch")
    @Operation(summary = "Simpan batch realisasi sasaran individu", description = "Menyimpan beberapa data realisasi sasaran individu dalam satu request. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(schema = @Schema(implementation = SasaranIndividuSubmitListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranIndividuSubmitListResponse> batchSubmitRealisasiSasaranIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi sasaran individu", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividuSubmitRequest.class))))
            @RequestBody @Valid List<SasaranIndividuSubmitRequest> requests) {
        return sasaranIndividuService.batchSubmitRealisasiSasaranIndividu(requests)
                .collectList()
                .map(items -> {
                    String kodeOpd = items.isEmpty() ? null : items.getFirst().kodeOpd();
                    Integer tahun = items.isEmpty() ? null : items.getFirst().tahun();
                    return new SasaranIndividuSubmitListResponse(kodeOpd, tahun, items);
                });
    }

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi sasaran Individu", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi sasaran Individu berdasarkan kode OPD dan tahun. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanSasaranIndividuListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanSasaranIndividuListResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        return sasaranIndividuService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }
}
