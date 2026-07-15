package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(name = "RekinRequest", description = "Payload untuk membuat/memperbarui realisasi target rekin individu")
public record RekinRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "196909212007012018")
        String nip,

        @NotNull(message = "Kode rekin PK tidak boleh kosong")
        @NotEmpty(message = "Kode rekin PK tidak boleh kosong")
        @Schema(description = "Kode PK rekin", example = "REKIN-PEG-2026-33475")
        String kodePkRekin,

        @Schema(description = "Kode sasaran OPD (digunakan untuk sync ke sasaran OPD)", example = "SAS-001")
        String kodeSasaranOpd,

        @NotNull(message = "Kode indikator PK rekin tidak boleh kosong")
        @NotEmpty(message = "Kode indikator PK rekin tidak boleh kosong")
        @Schema(description = "Kode indikator pk rekin", example = "IND-REKIN-87169")
        String kodeIndikatorPKrekin,

        @NotNull(message = "Kode target PK rekin tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target", example = "TRGT-IND-REKIN-66602")
        String kodeTargetPKrekin,

        @NotNull(message = "Realisasi harus terdefinisi")
        @Schema(description = "Nilai realisasi aktual", example = "70")
        BigDecimal realisasi,

        @Schema(hidden = true)
        JenisRealisasi jenisRealisasi,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @Schema(description = "URL bukti pendukung realisasi", example = "https://example.com/bukti.pdf")
        String buktiPendukung,

        @Schema(description = "Keterangan dari bukti pendukung", example = "Dokumen pendukung")
        String keteranganBuktiPendukung
) {
}
