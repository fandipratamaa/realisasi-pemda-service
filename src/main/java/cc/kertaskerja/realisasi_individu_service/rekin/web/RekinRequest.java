package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RekinRequest", description = "Payload untuk membuat/memperbarui realisasi rekin")
public record RekinRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID rekin tidak boleh kosong")
        @NotEmpty(message = "ID rekin tidak boleh kosong")
        @Schema(description = "ID rekin", example = "REKIN-001")
        String rekinId,

        @NotNull(message = "Rekin tidak boleh kosong")
        @NotEmpty(message = "Rekin tidak boleh kosong")
        @Schema(description = "Nama rekin", example = "Rekin Peningkatan Infrastruktur")
        String rekin,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "ID sasaran tidak boleh kosong")
        @NotEmpty(message = "ID sasaran tidak boleh kosong")
        @Schema(description = "ID sasaran", example = "SAS-001")
        String idSasaran,

        @NotNull(message = "Sasaran tidak boleh kosong")
        @NotEmpty(message = "Sasaran tidak boleh kosong")
        @Schema(description = "Nama sasaran", example = "Meningkatkan kualitas layanan")
        String sasaran,

        @NotNull(message = "Target harus terdefinisi")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target", example = "TAR-1")
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

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi
) {
}
