package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangRenaksiOpdRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi renaksi OPD")
public record FaktorPenunjangRenaksiOpdRequest(
        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "4.01.01.")
        String kodeOpd,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "ID rekin tidak boleh kosong")
        @NotEmpty(message = "ID rekin tidak boleh kosong")
        @Schema(description = "ID rekin", example = "REKIN-001")
        String rekinId,

        @NotNull(message = "ID renaksi tidak boleh kosong")
        @NotEmpty(message = "ID renaksi tidak boleh kosong")
        @Schema(description = "ID renaksi", example = "RENAKSI-001")
        String renaksiId,

        @NotNull(message = "ID target tidak boleh kosong")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target", example = "TAR-1")
        String targetId,

        @NotNull(message = "Faktor penunjang tidak boleh kosong")
        @NotEmpty(message = "Faktor penunjang tidak boleh kosong")
        @Schema(description = "Faktor penunjang renaksi", example = "Kerjasama tim")
        String faktorPenunjang
) {}
