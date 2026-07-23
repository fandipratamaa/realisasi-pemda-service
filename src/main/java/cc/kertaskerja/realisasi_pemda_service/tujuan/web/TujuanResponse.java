package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TujuanResponse(
        Long id,

        @JsonProperty("kode_tujuan_pemda")
        String kodeTujuanPemda,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        Double realisasi,

        String satuan,

        String tahun,

        String bulan,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat,

        @JsonProperty("tujuan_pemda")
        String tujuanPemda,

        String indikator,

        @JsonProperty("rumus_perhitungan")
        String rumusPerhitungan,

        @JsonProperty("sumber_data")
        String sumberData,

        @JsonProperty("definisi_operasional")
        String definisiOperasional,

        Double target,

        Double capaian,

        @JsonProperty("hasil_capaian")
        String hasilCapaian,

        @JsonProperty("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @JsonProperty("created_by")
        String createdBy,

        @JsonProperty("last_modified_by")
        String lastModifiedBy,

        @JsonProperty("bukti_pendukung")
        String buktiPendukung,

        @JsonProperty("keterangan_bukti_pendukung")
        String keteranganBuktiPendukung
) {
}
