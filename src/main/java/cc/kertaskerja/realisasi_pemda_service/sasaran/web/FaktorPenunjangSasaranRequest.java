package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangSasaranRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi sasaran pemda")
public record FaktorPenunjangSasaranRequest(
        @NotNull(message = "ID sasaran tidak boleh kosong")
        @NotEmpty(message = "ID sasaran tidak boleh kosong")
        @Schema(description = "ID sasaran dari sistem sumber", example = "SAS-001")
        String sasaranId,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator sasaran", example = "IND-SAS-123")
        String indikatorId,

        @NotNull(message = "ID target tidak boleh kosong")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target indikator", example = "TAR-1")
        String targetId,

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
        @Schema(description = "Faktor penunjang sasaran pemda", example = "Kerjasama antar daerah")
        String faktorPenunjang
) {}
