package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "LaporanRealisasiOpdResponse", description = "Response laporan realisasi tujuan OPD per periode")
public record LaporanRealisasiTujuanOpdResponse(
        @Schema(description = "Tahun laporan", example = "2026")
        String tahun,

        @JsonProperty("kode_opd")
        @Schema(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,
        
        @Schema(description = "Indikator laporan", example = "Indikator A")
        String indikator,

        @Schema(description = "Target laporan", example = "100")
        String target,

        @JsonProperty("jenis_laporan")
        @Schema(description = "Jenis periode laporan", example = "TAHUNAN")
        JenisLaporan jenisLaporan,

        @JsonProperty("list_data")
        @Schema(description = "Data realisasi per periode. Key = nomor periode (bulan/triwulan), Value = total realisasi",
                example = "{\"1\": 120.0, \"2\": 95.0}")
        Map<String, Double> listData,


        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        @Schema(description = "Total realisasi (hanya untuk TRIWULAN dan TAHUNAN)", example = "215.0")
        Double totalRealisasi
) {
}
