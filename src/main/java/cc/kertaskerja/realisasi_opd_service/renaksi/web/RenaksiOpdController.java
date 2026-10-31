package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpd;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renaksi OPD", description = "Menggabungkan data penetapan renaksi OPD (dari external service) dengan realisasi yang tersimpan di service ini, berdasarkan kode OPD dan tahun. Objek `realisasi` selalu dikembalikan pada setiap item, bahkan ketika realisasinya masih kosong. Parameter bulan bersifat opsional; jika tidak dikirim, objek `realisasi` tetap dikembalikan dengan `bulan` null dan `realisasi` 0. Ketika bulan diisi, nilai realisasi diambil dari bulan tersebut. `target` dihitung dari penjumlahan bobot tw1+tw2+tw3+tw4, dan `capaian` dihitung dari realisasi terhadap target tersebut. Nilai `realisasi` terisi otomatis dari penjumlahan realisasi renaksi individu yang memiliki `kode_pk`, `pegawai_id` (NIP), `kode_opd`, `tahun`, dan `bulan` yang sama, sedangkan `faktor_penunjang` dan `faktor_penghambat` dikelola terpisah di halaman OPD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenaksiOpdListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenaksiOpdListResponse> getPenetapanWithRealisasi(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        validateBulan(bulan);
        return renaksiOpdService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @PostMapping("/{kodeOpd}/tahun/{tahun}/sync/penetapan")
    @Operation(summary = "Sinkronisasi renaksi OPD", description = "Memicu sinkronisasi data renaksi OPD dari service penetapan dan langsung mengembalikan data penetapan beserta realisasi terbaru. Parameter bulan bersifat opsional; jika tidak dikirim, hanya data penetapan tanpa realisasi yang dikembalikan. Kegagalan sinkronisasi tidak menggagalkan response; data penetapan tetap dikembalikan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan ter-sinkronisasi dan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = PenetapanRenaksiOpdListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<PenetapanRenaksiOpdListResponse> syncRenaksiOpd(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        validateBulan(bulan);
        return renaksiOpdService.syncPenetapanRenaksiOpd(kodeOpd, Integer.parseInt(tahun))
                .then(renaksiOpdService.getPenetapanWithRealisasi(kodeOpd, Integer.parseInt(tahun), bulan));
    }

    @GetMapping("/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renaksi OPD per periode", description = "Mengambil total realisasi renaksi OPD yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN). Data diagregasi per kode rencana aksi OPD dan diperkaya dengan nama serta target dari data penetapan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renaksi OPD", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenaksiOpdResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenaksiOpdResponse> getLaporanRealisasi(
            @Parameter(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan", example = "TAHUNAN") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN", example = "3") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        validateBulan(bulan);
        return renaksiOpdService.getLaporanRealisasi(kodeOpd, tahun, jenisLaporan, bulan);
    }

    private void validateBulan(String bulan) {
        if (bulan == null || bulan.isBlank()) return;
        try {
            int nilai = Integer.parseInt(bulan.trim());
            if (nilai < 1 || nilai > 12) {
                throw new NumberFormatException("di luar rentang");
            }
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan harus berupa angka 1-12");
        }
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
    @Operation(summary = "Hapus realisasi renaksi OPD (belum digunakan di endpoint realisasi)")
    public Mono<Void> deleteRealisasiRenaksi(@PathVariable Long id) {
        return renaksiOpdService.deleteRealisasiRenaksi(id);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang renaksi OPD", description = "Memperbarui hanya field faktor_penunjang pada record RenaksiOpd yang cocok dengan composite key (kodeOpd, tahun, bulan, kodeRencanaAksiOpd).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Renaksi OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiOpd> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRenaksiOpdRequest.class)))
            @RequestBody @Valid FaktorPenunjangRenaksiOpdRequest req) {
        return renaksiOpdService.updateFaktorPenunjang(req);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat renaksi OPD", description = "Memperbarui hanya field faktor_penghambat pada record RenaksiOpd yang cocok dengan composite key (kodeOpd, tahun, bulan, kodeRencanaAksiOpd).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiOpd.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Renaksi OPD tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiOpd> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRenaksiOpdRequest.class)))
            @RequestBody @Valid FaktorPenghambatRenaksiOpdRequest req) {
        return renaksiOpdService.updateFaktorPenghambat(req);
    }
}
