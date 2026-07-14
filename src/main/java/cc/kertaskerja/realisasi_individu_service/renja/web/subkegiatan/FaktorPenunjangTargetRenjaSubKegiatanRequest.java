package cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangTargetRenjaSubKegiatanRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi target renja individu tingkat SUBKEGIATAN")
public record FaktorPenunjangTargetRenjaSubKegiatanRequest(

        @NotNull @NotEmpty
        @Schema(example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,

        @NotNull @NotEmpty
        @Schema(example = "5.01.02.001.001")
        String kodeSubKegiatan,

        @NotNull @NotEmpty
        @Schema(example = "IND-RENJA-PENETAPAN-5.01.02.001.001-5.01.5.05.0.00.01.0000-2026-001")
        String kodeIndikator,

        @NotNull @NotEmpty
        @Schema(example = "TGT-TRG-PENETAPAN-89457")
        String kodeTarget,

        @NotNull @NotEmpty
        @Schema(example = "2026")
        String tahun,

        @NotNull @NotEmpty
        @Schema(example = "1")
        String bulan,

        @NotNull @NotEmpty
        @Schema(description = "Faktor penunjang target subkegiatan", example = "Kerjasama tim yang baik")
        String faktorPenunjang
) {}
