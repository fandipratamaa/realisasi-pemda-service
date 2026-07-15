package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TujuanOpdResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        Integer tahun,

        Integer bulan,

        @JsonProperty("kode_tujuan_opd")
        String kodeTujuanOpd,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        Double realisasi,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat,

        @JsonProperty("tujuan_opd")
        String tujuanOpd,

        String indikator,

        @JsonProperty("rumus_perhitungan")
        String rumusPerhitungan,

        @JsonProperty("sumber_data")
        String sumberData,

        @JsonProperty("definisi_operasional")
        String definisiOperasional,

        Double target,

        String satuan,

        Double capaian,

        @JsonProperty("keterangan_capaian")
        String keteranganCapaian,

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
