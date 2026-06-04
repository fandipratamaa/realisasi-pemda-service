package cc.kertaskerja.realisasi_individu_service.rekin.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangRekinRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi rekin")
public record FaktorPenunjangRekinRequest(
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
        @Schema(description = "Bulan realisasi", example = "01")
        String bulan,

        @NotNull(message = "ID rekin tidak boleh kosong")
        @NotEmpty(message = "ID rekin tidak boleh kosong")
        @Schema(description = "ID rekin", example = "REKIN-001")
        String rekinId,

        @NotNull(message = "ID target tidak boleh kosong")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target", example = "TAR-1")
        String targetId,

        @NotNull(message = "Faktor penunjang tidak boleh kosong")
        @NotEmpty(message = "Faktor penunjang tidak boleh kosong")
        @Schema(description = "Faktor penunjang rekin", example = "Kerjasama tim")
        String faktorPenunjang
) {}
