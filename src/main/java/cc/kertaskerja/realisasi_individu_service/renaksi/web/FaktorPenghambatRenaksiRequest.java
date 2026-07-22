package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FaktorPenghambatRenaksiRequest", description = "Payload untuk memperbarui faktor penghambat pada target indikator renaksi")
public record FaktorPenghambatRenaksiRequest(
        @NotNull(message = "Kode OPD tidak boleh kosong")
        @NotEmpty(message = "Kode OPD tidak boleh kosong")
        @Schema(description = "Kode OPD", example = "1234")
        String kodeOpd,

        @NotNull(message = "NIP tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        @Schema(description = "NIP pelaksana", example = "198012312005011001")
        String nip,

        @NotNull(message = "Tahun tidak boleh kosong")
        @NotEmpty(message = "Tahun tidak boleh kosong")
        @Schema(description = "Tahun realisasi", example = "2025")
        String tahun,

        @NotNull(message = "Bulan tidak boleh kosong")
        @NotEmpty(message = "Bulan tidak boleh kosong")
        @Schema(description = "Bulan realisasi", example = "Januari")
        String bulan,

        @NotNull(message = "Kode rekin tidak boleh kosong")
        @NotEmpty(message = "Kode rekin tidak boleh kosong")
        @Schema(description = "Kode rekin", example = "REKIN-001")
        String kodeRekin,

        @NotNull(message = "Kode renaksi tidak boleh kosong")
        @NotEmpty(message = "Kode renaksi tidak boleh kosong")
        @Schema(description = "Kode renaksi", example = "RENAKSI-001")
        String kodeRenaksi,



        @NotNull(message = "Kode pelaksanaan tidak boleh kosong")
        @NotEmpty(message = "Kode pelaksanaan tidak boleh kosong")
        @Schema(description = "Kode pelaksanaan", example = "PEL-1")
        String kodePelaksanaan,

        @NotNull(message = "Faktor penghambat tidak boleh kosong")
        @NotEmpty(message = "Faktor penghambat tidak boleh kosong")
        @Schema(description = "Faktor penghambat renaksi", example = "Perubahan prioritas")
        String faktorPenghambat
) {}
