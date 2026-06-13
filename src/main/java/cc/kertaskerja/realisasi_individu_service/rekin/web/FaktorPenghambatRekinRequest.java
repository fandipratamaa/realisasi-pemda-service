package cc.kertaskerja.realisasi_individu_service.rekin.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatRekinRequest", description = "Payload untuk memperbarui faktor penghambat pada realisasi rekin")
public record FaktorPenghambatRekinRequest(
        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1234")
        String kodeOpd,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "Januari")
        String bulan,

        @NotNull(message = "Kode rekin tidak boleh kosong")
        @NotEmpty(message = "Kode rekin tidak boleh kosong")
        @Schema(description = "Kode rekin", example = "REKIN-001")
        String kodeRekin,

        @NotNull(message = "Kode indikator tidak boleh kosong")
        @NotEmpty(message = "Kode indikator tidak boleh kosong")
        @Schema(description = "Kode indikator", example = "RENAKSI-001")
        String kodeIndikator,

        @NotNull(message = "Kode target tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target", example = "TAR-1")
        String kodeTarget,

        @NotNull(message = "Faktor penghambat tidak boleh kosong")
        @NotEmpty(message = "Faktor penghambat tidak boleh kosong")
        @Schema(description = "Faktor penghambat rekin", example = "Perubahan prioritas")
        String faktorPenghambat
) {}
