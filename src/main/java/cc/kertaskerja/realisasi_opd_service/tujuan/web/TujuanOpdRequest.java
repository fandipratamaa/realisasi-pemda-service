package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "TujuanOpdRequest", description = "Payload untuk membuat/memperbarui realisasi tujuan OPD")
public record TujuanOpdRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID tujuan tidak boleh kosong")
        @NotEmpty(message = "ID tujuan tidak boleh kosong")
        @Schema(description = "ID tujuan dari sistem sumber", example = "TUJ-123")
        String tujuanId,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator tujuan", example = "IND-TUJ-123")
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

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi,

        @NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000")
        String kodeOpd
) {
}
