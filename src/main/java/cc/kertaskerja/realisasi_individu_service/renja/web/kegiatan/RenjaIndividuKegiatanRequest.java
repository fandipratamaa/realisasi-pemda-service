package cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "RenjaIndividuKegiatanRequest", description = "Payload untuk membuat/memperbarui realisasi renja individu tingkat KEGIATAN")
public record RenjaIndividuKegiatanRequest(

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
        @Schema(example = "5.01.02.001")
        String kodeKegiatan,

        @NotNull @NotEmpty
        @Schema(example = "IND-RENJA-PENETAPAN-5.01.02.001-5.01.5.05.0.00.01.0000-2026-001")
        String kodeIndikator,

        @NotNull @NotEmpty
        @Schema(example = "TGT-TRG-PENETAPAN-89456")
        String kodeTarget,

        @Schema(example = "PAGU-001")
        String kodePagu,

        @NotNull @PositiveOrZero
        @Schema(example = "100")
        Double target,

        @NotNull @PositiveOrZero
        @Schema(example = "70")
        Double realisasi,

        @Schema(hidden = true)
        String jenisRealisasi,

        @Schema(description = "URL bukti pendukung realisasi", example = "https://example.com/bukti.pdf")
        String buktiPendukung,

        @Schema(description = "Keterangan dari bukti pendukung", example = "Dokumen pendukung")
        String keteranganBuktiPendukung
) {}
