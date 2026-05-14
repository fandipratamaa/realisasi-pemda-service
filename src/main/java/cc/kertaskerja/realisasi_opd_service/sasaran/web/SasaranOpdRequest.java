package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "SasaranOpdRequest", description = "Payload untuk membuat/memperbarui realisasi sasaran OPD")
public record SasaranOpdRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID renja tidak boleh kosong")
        @NotEmpty(message = "ID renja tidak boleh kosong")
        @Schema(description = "ID renja dari sistem sumber", example = "REN-001")
        String renjaId,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator sasaran", example = "IND-SAS-123")
        String indikatorId,

        @NotNull(message = "Target harus terdefinisi")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target indikator", example = "TAR-1")
        String targetId,

        @NotNull(message = "Target harus terdefinisi")
        @Schema(description = "Nilai target yang ditetapkan", example = "100.0")
        String target,

        @NotNull(message = "Realisasi harus terdefinisi")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        @Schema(description = "Nilai realisasi aktual", example = "75.5", minimum = "0")
        Double realisasi,

        @NotEmpty(message = "Satuan tidak boleh kosong")
        @Schema(description = "Satuan target/realisasi", example = "%")
        String satuan,

@NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1", allowableValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
        String bulan,

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi,

        @NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd,

        @NotEmpty(message = "Rumus perhitungan tidak boleh kosong")
        @NotNull(message = "Rumus perhitungan tidak boleh kosong")
        @Schema(description = "Rumus perhitungan indikator", example = "(realisasi/target)*100")
        String rumusPerhitungan,

        @NotEmpty(message = "Sumber data tidak boleh kosong")
        @NotNull(message = "Sumber data tidak boleh kosong")
        @Schema(description = "Sumber data realisasi", example = "SIMDA")
        String sumberData,

        @NotEmpty(message = "Definisi operational tidak boleh kosong")
        @NotNull(message = "Definisi operational tidak boleh kosong")
        @Schema(description = "Definisi operasional indikator", example = "Persentase layanan yang selesai tepat waktu")
        String definisiOperational
) {
}
