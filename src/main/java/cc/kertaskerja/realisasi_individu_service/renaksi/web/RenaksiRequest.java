package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenaksiRequest", description = "Payload untuk membuat/memperbarui realisasi renaksi")
public record RenaksiRequest(
        @Nullable
        @Schema(description = "ID internal data realisasi. Kosongkan saat create.", example = "1", nullable = true)
        Long targetRealisasiId,

        @NotNull(message = "ID renaksi tidak boleh kosong")
        @NotEmpty(message = "ID renaksi tidak boleh kosong")
        @Schema(description = "ID renaksi", example = "RENAKSI-001")
        String renaksiId,

        @NotNull(message = "Renaksi tidak boleh kosong")
        @NotEmpty(message = "Renaksi tidak boleh kosong")
        @Schema(description = "Nama renaksi", example = "Renaksi Peningkatan Infrastruktur")
        String renaksi,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "Nama pegawai tidak boleh kosong")
        @NotEmpty(message = "Nama pegawai tidak boleh kosong")
        @Schema(description = "Nama pegawai", example = "Budi Santoso")
        String namaPegawai,

        @NotNull(message = "ID Rekin tidak boleh kosong")
        @NotEmpty(message = "ID Rekin tidak boleh kosong")
        @Schema(description = "ID rekin", example = "REKIN-001")
        String rekinId,

        @NotNull(message = "Rekin tidak boleh kosong")
        @NotEmpty(message = "Rekin tidak boleh kosong")
        @Schema(description = "Nama Rekin", example = "Rekin Peningkatan Infrastruktur")
        String rekin,

        @NotNull(message = "ID target tidak boleh kosong")
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

        @NotNull(message = "Bulan harus terdefinisi")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "Januari")
        String bulan,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        @Schema(description = "Jenis perhitungan capaian", example = "NAIK", allowableValues = {"NAIK", "TURUN"})
        JenisRealisasi jenisRealisasi,

        @Nullable
        @Schema(description = "Kode OPD", example = "4.01.01.", nullable = true)
        String kodeOpd
) {
}
