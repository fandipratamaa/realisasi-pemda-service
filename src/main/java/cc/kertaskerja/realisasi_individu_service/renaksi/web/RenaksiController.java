package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renaksi_individu")
@Tag(name = "Individu - Renaksi", description = "Endpoint realisasi renaksi tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class RenaksiController {
    private final RenaksiService renaksiService;

    public RenaksiController(RenaksiService renaksiService) {
        this.renaksiService = renaksiService;
    }

    @GetMapping("/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi renaksi individu berdasarkan NIP, kode OPD, tahun, dan bulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenaksiIndividu> getRealisasiByNipAndKodeOpdAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getAllByNipAndKodeOpdAndTahunAndBulan(nip, kodeOpd, tahun, bulan);
    }

    @GetMapping("/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi renaksi individu berdasarkan kode OPD, tahun, dan bulan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<RenaksiIndividu> getRealisasiByKodeOpdAndTahunAndBulan(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @GetMapping("/laporan/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renaksi individu per periode", description = "Mengambil total realisasi renaksi individu yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renaksi individu", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenaksiIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasi(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renaksiService.getLaporanRealisasi(nip, kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renaksi individu per periode (OPD)", description = "Mengambil total realisasi renaksi individu yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk seluruh OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renaksi individu", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenaksiIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasiByOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renaksiService.getLaporanRealisasiByOpd(kodeOpd, tahun, jenisLaporan, bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi target renaksi individu (upsert)", description = "Menyimpan realisasi target renaksi individu. Jika data dengan composite key yang sama sudah ada, akan diperbarui.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Realisasi tersimpan", content = @Content(schema = @Schema(implementation = RenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenaksiIndividu> submitRealisasi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renaksi individu", required = true,
                    content = @Content(schema = @Schema(implementation = RenaksiIndividuRequest.class)))
            @RequestBody @Valid RenaksiIndividuRequest request) {
        return renaksiService.submitRealisasiTarget(request);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renaksi individu", description = "Menyimpan beberapa data realisasi renaksi individu dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenaksiIndividu> batchSubmitRealisasi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renaksi individu", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiIndividuRequest.class))))
            @RequestBody @Valid List<RenaksiIndividuRequest> requests) {
        return renaksiService.batchSubmitRealisasiTarget(requests);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang renaksi", description = "Memperbarui hanya field faktor_penunjang pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiIndividu> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenunjangRenaksiRequest request) {
        return renaksiService.updateFaktorPenunjang(request);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat renaksi", description = "Memperbarui hanya field faktor_penghambat pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiIndividu> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenghambatRenaksiRequest request) {
        return renaksiService.updateFaktorPenghambat(request);
    }
}
