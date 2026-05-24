package cc.kertaskerja.realisasi_individu_service.renja.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenjaIndividuProgramRequest", description = "Payload untuk membuat/memperbarui realisasi renja individu tingkat PROGRAM")
public record RenjaIndividuProgramRequest(

        @NotNull @NotEmpty
        @Schema(example = "5.01.5.05.0.00.01.0000")
        @JsonProperty("kode_opd")
        String kodeOpd,

        @NotNull @NotEmpty
        @Schema(example = "2026")
        String tahun,

        @NotNull @NotEmpty
        @Schema(example = "1")
        String bulan,

        @NotNull @NotEmpty
        @Schema(example = "198001012010011000")
        String nip,

        @NotNull @NotEmpty
        @Schema(example = "John Doe")
        @JsonProperty("nama_pegawai")
        String namaPegawai,

        @NotNull @NotEmpty
        @Schema(example = "5.01.02")
        @JsonProperty("kode_program")
        String kodeProgram,

        @NotNull @NotEmpty
        @Schema(example = "IND-RENJA-PENETAPAN-5.01.02-5.01.5.05.0.00.01.0000-2026-001")
        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @NotNull @NotEmpty
        @Schema(example = "TGT-TRG-PENETAPAN-89455")
        @JsonProperty("kode_target")
        String kodeTarget,

        @NotNull @PositiveOrZero
        @Schema(example = "70")
        Double realisasi
) {}
