package cc.kertaskerja.realisasi_opd_service.renja.web.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangTargetRenjaProgramOpdRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi target renja OPD tingkat PROGRAM")
public record FaktorPenunjangTargetRenjaProgramOpdRequest(

        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,

        @NotNull(message = "Kode program tidak boleh kosong")
        @NotEmpty(message = "Kode program tidak boleh kosong")
        @Schema(description = "Kode program", example = "5.01.02")
        String kodeProgram,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2026")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "1")
        String bulan,

        @NotNull(message = "Faktor penunjang tidak boleh kosong")
        @NotEmpty(message = "Faktor penunjang tidak boleh kosong")
        @JsonProperty("faktor_penunjang")
        @Schema(description = "Faktor penunjang target program", example = "Kerjasama tim yang baik")
        String faktorPenunjang
) {}
