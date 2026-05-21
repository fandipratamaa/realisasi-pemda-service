package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "SasaranIndividuSubmitRequest", description = "Payload untuk membuat/memperbarui realisasi sasaran individu")
public record SasaranIndividuSubmitRequest(

        @NotNull(message = "Kode sasaran OPD tidak boleh kosong")
        @NotEmpty(message = "Kode sasaran OPD tidak boleh kosong")
        @Schema(description = "Kode sasaran OPD", example = "KODE-SAS-OPD-001")
        String kodeSasaranOpd,

        @NotNull(message = "Kode indikator tidak boleh kosong")
        @NotEmpty(message = "Kode indikator tidak boleh kosong")
        @Schema(description = "Kode indikator", example = "KODE-IND-001")
        String kodeIndikator,

        @NotNull(message = "Kode target tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target", example = "KODE-TAR-001")
        String kodeTarget,

        @NotNull(message = "Realisasi harus terdefinisi")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        @Schema(description = "Nilai realisasi aktual", example = "75.5", minimum = "0")
        Double realisasi,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1", allowableValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
        String bulan,

        @NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd
) {
}
