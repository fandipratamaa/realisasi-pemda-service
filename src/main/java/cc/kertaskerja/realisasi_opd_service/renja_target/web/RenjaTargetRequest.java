package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenjaTargetRequest", description = "Payload untuk membuat/memperbarui realisasi renja target OPD")
public record RenjaTargetRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID jenis renja tidak boleh kosong")
        @NotEmpty(message = "ID jenis renja tidak boleh kosong")
        @Schema(description = "ID jenis renja", example = "REN-001")
        String jenisRenjaId,

        @NotNull(message = "Pilih jenis renja PROGRAM, KEGIATAN, SUBKEGIATAN")
        @Schema(description = "Jenis level renja", example = "PROGRAM", allowableValues = {"PROGRAM", "KEGIATAN", "SUBKEGIATAN"})
        JenisRenja jenisRenja,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator renja", example = "IND-REN-123")
        String indikatorId,

        @NotNull(message = "Indikator tidak boleh kosong")
        @NotEmpty(message = "Indikator tidak boleh kosong")
        @Schema(description = "Nama indikator", example = "Persentase capaian program")
        String indikator,

        @NotNull(message = "Target harus terdefinisi")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target indikator", example = "TAR-1")
        String targetId,

        @NotNull(message = "Target harus terdefinisi")
        @Schema(description = "Nilai target yang ditetapkan", example = "100")
        String target,

        @NotNull(message = "Realisasi harus terdefinisi")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        @Schema(description = "Nilai realisasi aktual", example = "85", minimum = "0")
        Integer realisasi,

        @NotEmpty(message = "Satuan tidak boleh kosong")
        @Schema(description = "Satuan target/realisasi", example = "%")
        String satuan,

@NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi,

@NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "Kode renja tidak boleh kosong")
        @Schema(description = "Kode renja", example = "5")
        String kodeRenja
) {
}
