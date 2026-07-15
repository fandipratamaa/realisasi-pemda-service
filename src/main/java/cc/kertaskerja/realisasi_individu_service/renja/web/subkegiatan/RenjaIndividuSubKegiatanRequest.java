package cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenjaIndividuSubKegiatanRequest", description = "Payload untuk membuat/memperbarui realisasi renja individu tingkat SUBKEGIATAN")
public record RenjaIndividuSubKegiatanRequest(

        @NotNull @NotEmpty
        @Schema(example = "5.01.5.05.0.00.01.0000")
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
        @Schema(example = "5.01.02.001.001")
        String kodeSubKegiatan,

        @NotNull @NotEmpty
        @Schema(example = "IND-RENJA-PENETAPAN-5.01.02.001.001-5.01.5.05.0.00.01.0000-2026-001")
        String kodeIndikator,

        @NotNull @NotEmpty
        @Schema(example = "TGT-TRG-PENETAPAN-89457")
        String kodeTarget,

        @Schema(example = "PAGU-001")
        String kodePagu,

        @NotNull @PositiveOrZero
        @JsonProperty("target_realisasi")
        @Schema(example = "100")
        Double targetRealisasi,

        @NotNull @PositiveOrZero
        @JsonProperty("pagu")
        @Schema(example = "50000000")
        Double pagu,

        @NotNull @PositiveOrZero
        @JsonProperty("realisasi_target")
        @Schema(example = "70")
        Double realisasiTarget,

        @NotNull @PositiveOrZero
        @JsonProperty("realisasi_pagu")
        @Schema(example = "10000000")
        Double realisasiPagu,

        @Schema(hidden = true)
        String jenisRealisasi,

        @Schema(description = "URL bukti pendukung realisasi", example = "https://example.com/bukti.pdf")
        String buktiPendukung,

        @Schema(description = "Keterangan dari bukti pendukung", example = "Dokumen pendukung")
        String keteranganBuktiPendukung
) {}
