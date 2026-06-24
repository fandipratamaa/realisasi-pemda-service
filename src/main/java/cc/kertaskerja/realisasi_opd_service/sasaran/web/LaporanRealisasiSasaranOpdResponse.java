package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "LaporanRealisasiSasaranOpdResponse", description = "Response laporan realisasi sasaran OPD per periode")
public record LaporanRealisasiSasaranOpdResponse(
        @Schema(description = "Tahun laporan", example = "2026")
        String tahun,

        @JsonProperty("kode_opd")
        @Schema(description = "Kode OPD", example = "5.01.5.05.0.00.01.0000")
        String kodeOpd,

        @JsonProperty("jenis_laporan")
        @Schema(description = "Jenis periode laporan", example = "TAHUNAN")
        JenisLaporan jenisLaporan,

        @JsonProperty("list_data")
        @Schema(description = "Data realisasi per periode. Key = nomor periode (bulan/triwulan), Value = total realisasi",
                example = "{\"1\": 120.0, \"2\": 95.0}")
        Map<String, Double> listData
) {
}
