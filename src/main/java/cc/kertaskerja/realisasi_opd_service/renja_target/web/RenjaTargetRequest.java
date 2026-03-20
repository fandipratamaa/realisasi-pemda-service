package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RenjaTargetRequest(
        @Nullable
        Long targetRealisasiId,

        @NotNull(message = "ID renja tidak boleh kosong")
        @NotEmpty(message = "ID renja tidak boleh kosong")
        String renjaTargetId,

        @NotNull(message = "Renja tidak boleh kosong")
        @NotEmpty(message = "Renja tidak boleh kosong")
        String renjaTarget,

        @NotNull(message = "Pilih jenis renja PROGRAM, KEGIATAN, SUBKEGIATAN")
        JenisRenja jenisRenjaTarget,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        String indikatorId,

        @NotNull(message = "Indikator tidak boleh kosong")
        @NotEmpty(message = "Indikator tidak boleh kosong")
        String indikator,

        @NotNull(message = "Target harus terdefinisi")
        @NotEmpty(message = "ID target tidak boleh kosong")
        String targetId,

        @NotNull(message = "Target harus terdefinisi")
        String target,

        @NotNull(message = "Realisasi harus terdefinisi")
        @PositiveOrZero(message = "Realisasi tidak boleh negatif")
        Integer realisasi,

        @NotEmpty(message = "Satuan tidak boleh kosong")
        String satuan,

        @NotNull(message = "Tahun harus terdefinisi")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        String tahun,

        @NotNull(message = "Pilih jenis NAIK atau TURUN")
        JenisRealisasi jenisRealisasi,

        @NotEmpty(message = "Kode opd tidak boleh kosong")
        @NotNull(message = "Kode opd tidak boleh kosong")
        String kodeOpd
) {
}
