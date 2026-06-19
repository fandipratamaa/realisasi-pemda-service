package cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatTargetRenjaKegiatanRequest", description = "Payload untuk memperbarui faktor penghambat pada realisasi target renja individu tingkat KEGIATAN")
public record FaktorPenghambatTargetRenjaKegiatanRequest(

        @NotNull @NotEmpty
        @Schema(example = "5.01.5.05.0.00.01.0000")
        @JsonProperty("kode_opd")
        String kodeOpd,

        @NotNull @NotEmpty
        @Schema(example = "5.01.02.001")
        @JsonProperty("kode_kegiatan")
        String kodeKegiatan,

        @NotNull @NotEmpty
        @Schema(example = "IND-RENJA-PENETAPAN-5.01.02.001-5.01.5.05.0.00.01.0000-2026-001")
        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @NotNull @NotEmpty
        @Schema(example = "TGT-TRG-PENETAPAN-89456")
        @JsonProperty("kode_target")
        String kodeTarget,

        @NotNull @NotEmpty
        @Schema(example = "2026")
        String tahun,

        @NotNull @NotEmpty
        @Schema(example = "1")
        String bulan,

        @NotNull @NotEmpty
        @Schema(description = "Faktor penghambat target kegiatan", example = "Keterbatasan anggaran")
        String faktorPenghambat
) {}
