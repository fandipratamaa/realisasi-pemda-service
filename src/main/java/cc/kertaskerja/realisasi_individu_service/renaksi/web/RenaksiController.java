package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.SasaranWithDetails;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.target.TargetIndikatorRenaksiIndividu;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renaksi")
@Tag(name = "Individu - Renaksi", description = "Endpoint realisasi renaksi tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class RenaksiController {
    private final RenaksiService renaksiService;

    public RenaksiController(RenaksiService renaksiService) {
        this.renaksiService = renaksiService;
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari sasaran renaksi berdasarkan NIP, tahun, dan bulan (dengan renaksi, indikator & target)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar sasaran dengan detail renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranWithDetails.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranWithDetails> getSasaranByNipAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        if (nip == null || nip.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getSasaranWithDetailsByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari sasaran renaksi berdasarkan kode OPD, tahun, dan bulan (dengan renaksi, indikator & target)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar sasaran dengan detail renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranWithDetails.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranWithDetails> getSasaranByKodeOpdAndTahunAndBulan(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getSasaranWithDetailsByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @PostMapping
    @Operation(summary = "Buat sasaran renaksi baru (dengan upsert)", description = "Menyimpan satu data sasaran beserta renaksi, indikator, dan target. Jika `id` disertakan akan memperbarui data yang sudah ada (upsert).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sasaran tersimpan dengan renaksi, indikator, dan target", content = @Content(schema = @Schema(implementation = SasaranWithDetails.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranWithDetails> createSasaran(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload sasaran dengan renaksi, indikator, dan target", required = true,
                    content = @Content(schema = @Schema(implementation = RenaksiIndividuRequest.class)))
            @RequestBody @Valid RenaksiIndividuRequest request) {
        return renaksiService.createSasaran(request);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang renaksi", description = "Memperbarui hanya field faktor_penunjang pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetIndikatorRenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<TargetIndikatorRenaksiIndividu> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenunjangRenaksiRequest request) {
        return renaksiService.updateFaktorPenunjang(request);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat renaksi", description = "Memperbarui hanya field faktor_penghambat pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetIndikatorRenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<TargetIndikatorRenaksiIndividu> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenghambatRenaksiRequest request) {
        return renaksiService.updateFaktorPenghambat(request);
    }
}
