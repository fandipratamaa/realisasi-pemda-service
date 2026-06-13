package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinWithDetails;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekin;
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
@RequestMapping("rekin")
@Tag(name = "Individu - Rekin", description = "Endpoint rekin tingkat individu.")
public class RekinController {
    private final RekinService rekinService;

    public RekinController(RekinService rekinService) {
        this.rekinService = rekinService;
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari rekin berdasarkan NIP, tahun, dan bulan (dengan indikator & target)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar rekin", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RekinWithDetails.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RekinWithDetails> getRekinByNipAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        if (nip == null || nip.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, tahun, dan bulan tidak boleh kosong");
        }
        return rekinService.getRekinWithDetailsByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari rekin berdasarkan kode OPD, tahun, dan bulan (dengan indikator & target)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar rekin", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RekinWithDetails.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RekinWithDetails> getRekinByKodeOpdAndTahunAndBulan(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return rekinService.getRekinWithDetailsByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @PostMapping
    @Operation(summary = "Buat rekin baru (dengan upsert)", description = "Menyimpan satu data rekin beserta indikator dan target. Jika `id` disertakan akan memperbarui data yang sudah ada (upsert).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rekin tersimpan dengan indikator dan target", content = @Content(schema = @Schema(implementation = RekinWithDetails.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RekinWithDetails> createRekin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload rekin dengan indikator dan target", required = true,
                    content = @Content(schema = @Schema(implementation = RekinRequest.class)))
            @RequestBody @Valid RekinRequest request) {
        return rekinService.createRekin(request);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang rekin", description = "Memperbarui hanya field faktor_penunjang pada target indikator rekin yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetIndikatorRekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<TargetIndikatorRekin> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang rekin", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRekinRequest.class)))
            @RequestBody @Valid FaktorPenunjangRekinRequest request) {
        return rekinService.updateFaktorPenunjang(request);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat rekin", description = "Memperbarui hanya field faktor_penghambat pada target indikator rekin yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = TargetIndikatorRekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<TargetIndikatorRekin> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat rekin", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRekinRequest.class)))
            @RequestBody @Valid FaktorPenghambatRekinRequest request) {
        return rekinService.updateFaktorPenghambat(request);
    }
}
