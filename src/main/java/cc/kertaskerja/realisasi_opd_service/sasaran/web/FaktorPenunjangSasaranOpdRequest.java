package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangSasaranOpdRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi sasaran OPD")
public record FaktorPenunjangSasaranOpdRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "Kode sasaran OPD tidak boleh kosong")
        @NotEmpty(message = "Kode sasaran OPD tidak boleh kosong")
        @Schema(description = "Kode sasaran OPD", example = "KODE-SAS-OPD-001")
        String kodeSasaranOpd,

        @NotNull(message = "Kode indikator sasaran OPD tidak boleh kosong")
        @NotEmpty(message = "Kode indikator sasaran OPD tidak boleh kosong")
        @Schema(description = "Kode indikator sasaran OPD", example = "KODE-IND-SAS-OPD-001")
        String kodeIndikator,

        @NotNull(message = "Kode target tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target", example = "KODE-TAR-SAS-OPD-001")
        String kodeTarget,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "Faktor penunjang tidak boleh kosong")
        @NotEmpty(message = "Faktor penunjang tidak boleh kosong")
        @Schema(description = "Faktor penunjang sasaran OPD", example = "Kerjasama antar OPD")
        String faktorPenunjang
) {}
