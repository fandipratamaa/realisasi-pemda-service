package cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangTargetRenjaKegiatanRequest", description = "Payload untuk memperbarui faktor penunjang pada realisasi target renja individu tingkat KEGIATAN")
public record FaktorPenunjangTargetRenjaKegiatanRequest(

        @NotNull @NotEmpty
        @Schema(example = "198001012010011000")
        String nip,

        @NotNull @NotEmpty
        @Schema(example = "5.01.02.001")
        String kodeKegiatan,

        @NotNull @NotEmpty
        @Schema(example = "2026")
        String tahun,

        @NotNull @NotEmpty
        @Schema(example = "1")
        String bulan,

        @NotNull @NotEmpty
        @Schema(description = "Faktor penunjang target kegiatan", example = "Kerjasama tim yang baik")
        String faktorPenunjang
) {}
