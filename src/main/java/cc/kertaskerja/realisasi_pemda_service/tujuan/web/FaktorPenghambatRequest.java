package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatRequest", description = "Payload untuk memperbarui faktor penghambat pada realisasi tujuan pemda")
public record FaktorPenghambatRequest(
        @NotNull(message = "ID tujuan tidak boleh kosong")
        @NotEmpty(message = "ID tujuan tidak boleh kosong")
        @Schema(description = "ID tujuan dari sistem sumber", example = "TUJ-123")
        String tujuanId,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator tujuan", example = "IND-TUJ-123")
        String indikatorId,

        @NotNull(message = "ID target tidak boleh kosong")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target indikator", example = "TAR-1")
        String targetId,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "01")
        String bulan,

        @NotNull(message = "Faktor penghambat tidak boleh kosong")
        @NotEmpty(message = "Faktor penghambat tidak boleh kosong")
        @Schema(description = "Faktor penghambat tujuan pemda", example = "Keterbatasan anggaran")
        String faktorPenghambat
) {}
