package cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatTargetRenjaSubKegiatanRequest", description = "Payload untuk memperbarui faktor penghambat pada realisasi target renja individu tingkat SUBKEGIATAN")
public record FaktorPenghambatTargetRenjaSubKegiatanRequest(

        @NotNull @NotEmpty
        @Schema(example = "198001012010011000")
        String nip,

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
        @Schema(description = "Faktor penghambat target subkegiatan", example = "Keterbatasan anggaran")
        String faktorPenghambat
) {}
