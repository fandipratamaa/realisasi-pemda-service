package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangTujuanOpdRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi tujuan OPD")
public record FaktorPenunjangTujuanOpdRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "Kode tujuan OPD tidak boleh kosong")
        @NotEmpty(message = "Kode tujuan OPD tidak boleh kosong")
        @Schema(description = "Kode tujuan OPD", example = "KODE-TUJ-OPD-001")
        String kodeTujuanOpd,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "Faktor penunjang tidak boleh kosong")
        @NotEmpty(message = "Faktor penunjang tidak boleh kosong")
        @Schema(description = "Faktor penunjang tujuan OPD", example = "Kerjasama antar OPD")
        String faktorPenunjang
) {}
