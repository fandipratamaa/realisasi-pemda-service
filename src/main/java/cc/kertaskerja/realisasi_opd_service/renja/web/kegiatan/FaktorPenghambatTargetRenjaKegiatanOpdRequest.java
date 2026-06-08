package cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatTargetRenjaKegiatanOpdRequest", description = "Payload untuk memperbarui faktor penghambat pada realisasi target renja OPD tingkat KEGIATAN")
public record FaktorPenghambatTargetRenjaKegiatanOpdRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "Kode kegiatan tidak boleh kosong")
        @NotEmpty(message = "Kode kegiatan tidak boleh kosong")
        @Schema(description = "Kode kegiatan", example = "5.01.02.001")
        String kodeKegiatan,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "Faktor penghambat tidak boleh kosong")
        @NotEmpty(message = "Faktor penghambat tidak boleh kosong")
        @JsonProperty("faktor_penghambat")
        @Schema(description = "Faktor penghambat target kegiatan", example = "Keterbatasan anggaran")
        String faktorPenghambat
) {}
