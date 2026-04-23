package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

@Schema(name = "SasaranRequest", description = "Payload untuk membuat/memperbarui realisasi sasaran pemda")
public record SasaranRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID sasaran tidak boleh kosong")
        @NotEmpty(message = "ID sasaran tidak boleh kosong")
        @Schema(description = "ID sasaran dari sistem sumber", example = "SAS-001")
        String sasaranId,

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
        @Schema(description = "Nilai realisasi aktual", example = "80.2", minimum = "0")
        Double realisasi,

        @NotEmpty(message = "Satuan tidak boleh kosong")
        @Schema(description = "Satuan target/realisasi", example = "%")
        String satuan,

@NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "01")
        String bulan,

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi
) {}
