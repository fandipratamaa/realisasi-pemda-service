package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RenaksiOpdRequest(
        @Nullable Long targetRealisasiId,
        @NotNull @NotEmpty String renaksiId,
        @NotNull @NotEmpty String renaksi,
        @NotNull @NotEmpty String rekinId,
        @NotNull @NotEmpty String rekin,
        @NotNull @NotEmpty String targetId,
        @NotNull String target,
        @NotNull @PositiveOrZero Integer realisasi,
        @NotNull @NotEmpty String satuan,
        @NotNull @NotEmpty String bulan,
        @NotNull @NotEmpty String tahun,
        @NotNull JenisRealisasi jenisRealisasi,
        @Nullable String kodeOpd,
        @Nullable String faktorPenunjang,
        @Nullable String faktorPenghambat
) {
}
