package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "LaporanRealisasiResponse", description = "Response laporan realisasi tujuan pemda per periode")
public record LaporanRealisasiTujuanResponse(
        @Schema(description = "Tahun laporan", example = "2026")
        String tahun,

        @Schema(description = "Jenis periode laporan", example = "TAHUNAN")
        JenisLaporan jenisLaporan,

        @Schema(description = "Data realisasi per periode. Key = nomor periode (bulan/triwulan), Value = total realisasi",
                example = "{\"1\": 120.0, \"2\": 95.0}")
        Map<String, Double> listData
) {}
