package cc.kertaskerja.realisasi_pemda_service.iku.web;

import cc.kertaskerja.realisasi_pemda_service.iku.domain.JenisIku;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenunjangIkuRequest", description = "Payload untuk memperbarui faktor penunjang pada IKU pemda")
public record FaktorPenunjangIkuRequest(
        @NotNull(message = "Jenis IKU tidak boleh kosong")
        @Schema(description = "Jenis IKU (TUJUAN atau SASARAN)", example = "TUJUAN")
        JenisIku jenisIku,

        @NotNull(message = "ID jenis tidak boleh kosong")
        @NotEmpty(message = "ID jenis tidak boleh kosong")
        @Schema(description = "ID jenis (tujuanId untuk TUJUAN, sasaranId untuk SASARAN)", example = "TUJ-123")
        String jenisId,

        @NotNull(message = "ID indikator tidak boleh kosong")
        @NotEmpty(message = "ID indikator tidak boleh kosong")
        @Schema(description = "ID indikator", example = "IND-TUJ-123")
        String indikatorId,

        @NotNull(message = "ID target tidak boleh kosong")
        @NotEmpty(message = "ID target tidak boleh kosong")
        @Schema(description = "ID target indikator", example = "TAR-1")
        String targetId,

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
        @Schema(description = "Faktor penunjang", example = "Kerjasama antar daerah")
        String faktorPenunjang
) {}
