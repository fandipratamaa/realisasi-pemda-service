package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "LaporanRealisasiRenaksiOpdResponse", description = "Response laporan realisasi renaksi OPD per periode")
public record LaporanRealisasiRenaksiOpdResponse(
        @Schema(description = "Tahun laporan", example = "2026")
        String tahun,

        @JsonProperty("kode_opd")
        @Schema(description = "Kode OPD", example = "8.01.0.00.0.00.01.0000")
        String kodeOpd,

        @JsonProperty("kode_rencana_aksi_opd")
        @Schema(description = "Kode rencana aksi OPD", example = "REN-OPD-1")
        String kodeRencanaAksiOpd,

        @JsonProperty("nama_renaksi")
        @Schema(description = "Nama renaksi OPD", example = "Terlaksananya Koordinasi antar Pimpinan Daerah")
        String namaRenaksi,

        @Schema(description = "Target laporan", example = "100.0")
        String target,

        @JsonProperty("jenis_laporan")
        @Schema(description = "Jenis periode laporan", example = "TAHUNAN")
        JenisLaporan jenisLaporan,

        @JsonProperty("list_data")
        @Schema(description = "Data realisasi per periode. Key = nomor periode (bulan/triwulan), Value = total realisasi",
                example = "{\"1\": 120.0, \"2\": 215.0}")
        Map<String, Double> listData,

        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        @Schema(description = "Total realisasi (hanya untuk TRIWULAN dan TAHUNAN)", example = "215.0")
        Double totalRealisasi
) {
}
