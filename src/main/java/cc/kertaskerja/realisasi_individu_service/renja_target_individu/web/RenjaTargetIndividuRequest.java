package cc.kertaskerja.realisasi_individu_service.renja_target_individu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenjaTargetIndividuRequest", description = "Payload untuk membuat/memperbarui realisasi renja target individu")
public record RenjaTargetIndividuRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID renja tidak boleh kosong")
        @NotEmpty(message = "ID renja tidak boleh kosong")
        @Schema(description = "ID renja", example = "RENJA-001")
        String renjaId,

        @NotNull(message = "Renja tidak boleh kosong")
        @NotEmpty(message = "Renja tidak boleh kosong")
        @Schema(description = "Nama renja", example = "Program Pembangunan Jalan")
        String renja,

        @NotNull(message = "Kode renja tidak boleh kosong")
        @NotEmpty(message = "Kode renja tidak boleh kosong")
        @Schema(description = "Kode renja berdasarkan level", example = "1.02.01")
        String kodeRenja,

        @NotNull(message = "Pilih jenis renja PROGRAM, KEGIATAN, SUBKEGIATAN")
        @Schema(description = "Jenis level renja", example = "PROGRAM", allowableValues = {"PROGRAM", "KEGIATAN", "SUBKEGIATAN"})
        JenisRenja jenisRenja,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator renja", example = "IND-REN-123")
        String idIndikator,

        @NotNull(message = "Indikator tidak boleh kosong")
        @NotEmpty(message = "Indikator tidak boleh kosong")
        @Schema(description = "Nama indikator", example = "Persentase capaian renja")
        String indikator,

        @NotNull(message = "ID target tidak boleh kosong")
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

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi
) {
}
