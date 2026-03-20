package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RenjaPaguRequest(
        @Nullable
        Long targetRealisasiId,

        @NotNull(message = "ID renja pagu tidak boleh kosong")
        @NotEmpty(message = "ID renja pagu tidak boleh kosong")
        String renjaPaguId,

        @NotNull(message = "Renja pagu tidak boleh kosong")
        @NotEmpty(message = "Renja pagu tidak boleh kosong")
        String renjaPagu,

        @NotNull(message = "Pilih jenis renja pagu PROGRAM, KEGIATAN, SUBKEGIATAN")
        JenisRenja jenisRenjaPagu,

        @NotNull(message = "Nilai pagu harus terdefinisi")
        Integer pagu,

        @NotNull(message = "Realisasi harus terdefinisi")
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
