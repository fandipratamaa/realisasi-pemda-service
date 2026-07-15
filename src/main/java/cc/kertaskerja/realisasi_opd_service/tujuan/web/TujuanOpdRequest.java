package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import io.swagger.v3.oas.annotations.media.Schema;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "TujuanOpdRequest", description = "Payload untuk membuat/memperbarui realisasi tujuan OPD")
public record TujuanOpdRequest(

        @NotNull(message = "Kode tujuan opd tidak boleh kosong")
        @NotEmpty(message = "Kode tujuan opd tidak boleh kosong")
        @Schema(description = "Kode tujuan OPD", example = "KODE-TUJ-OPD-001")
        String kodeTujuanOpd,

        @NotNull(message = "Kode indikator tidak boleh kosong")
        @NotEmpty(message = "Kode indikator tidak boleh kosong")
        @Schema(description = "Kode indikator tujuan OPD", example = "KODE-IND-TUJ-OPD-001")
        String kodeIndikator,

        @NotNull(message = "Kode target tidak boleh kosong")
        @NotEmpty(message = "Kode target tidak boleh kosong")
        @Schema(description = "Kode target tujuan OPD", example = "KODE-TAR-TUJ-OPD-001")
        String kodeTarget,

        @NotNull(message = "Realisasi harus terdefinisi")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        @Schema(description = "Nilai realisasi aktual", example = "75.5", minimum = "0")
        Double realisasi,

        JenisRealisasi jenisRealisasi,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1", allowableValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
        String bulan,

        @NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd,

        @Schema(description = "URL bukti pendukung realisasi", example = "https://example.com/bukti.pdf")
        String buktiPendukung,

        @Schema(description = "Keterangan dari bukti pendukung", example = "Dokumen pendukung berupa laporan tahunan BPS")
        String keteranganBuktiPendukung
) {
}
