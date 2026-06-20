package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(name = "RenaksiIndividuRequest", description = "Payload untuk membuat/memperbarui realisasi target renaksi individu")
public record RenaksiIndividuRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "Kode sasaran tidak boleh kosong")
        @NotEmpty(message = "Kode sasaran tidak boleh kosong")
        @Schema(description = "Kode sasaran", example = "SASARAN-001")
        String kodeSasaran,

        @NotNull(message = "Kode renaksi tidak boleh kosong")
        @NotEmpty(message = "Kode renaksi tidak boleh kosong")
        @Schema(description = "Kode renaksi", example = "RENAKSI-001")
        String kodeRenaksi,

        @NotNull(message = "Kode indikator tidak boleh kosong")
        @NotEmpty(message = "Kode indikator tidak boleh kosong")
        @Schema(description = "Kode indikator", example = "IND-RENAKSI-001")
        String kodeIndikator,

        @NotNull(message = "Kode target tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target", example = "TAR-1")
        String kodeTarget,

        @NotNull(message = "Realisasi tidak boleh kosong")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        @Schema(description = "Nilai realisasi aktual", example = "75.5", minimum = "0")
        BigDecimal realisasi,

        @NotNull(message = "Pagu anggaran tidak boleh kosong")
        @Schema(description = "Pagu anggaran", example = "50000000.00")
        BigDecimal paguAnggaran,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan
) {
}
