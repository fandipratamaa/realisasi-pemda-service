package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "RenjaPaguRequest", description = "Payload untuk membuat/memperbarui realisasi renja pagu OPD")
public record RenjaPaguRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID renja pagu tidak boleh kosong")
        @NotEmpty(message = "ID renja pagu tidak boleh kosong")
        @Schema(description = "ID renja pagu", example = "RENPAGU-001")
        String renjaPaguId,

        @NotNull(message = "Renja pagu tidak boleh kosong")
        @NotEmpty(message = "Renja pagu tidak boleh kosong")
        @Schema(description = "Nama renja pagu", example = "Program Pembangunan Jalan")
        String renjaPagu,

        @NotNull(message = "Pilih jenis renja pagu PROGRAM, KEGIATAN, SUBKEGIATAN")
        @Schema(description = "Jenis level renja", example = "PROGRAM", allowableValues = {"PROGRAM", "KEGIATAN", "SUBKEGIATAN"})
        JenisRenja jenisRenjaPagu,

        @NotNull(message = "Nilai pagu harus terdefinisi")
        @Schema(description = "Nilai pagu anggaran", example = "100000000")
        Integer pagu,

        @NotNull(message = "Realisasi harus terdefinisi")
        @Schema(description = "Nilai realisasi anggaran", example = "75000000")
        Integer realisasi,

        @NotEmpty(message = "Satuan tidak boleh kosong")
        @Schema(description = "Satuan nilai", example = "Rupiah")
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
