package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SasaranOpdResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        Integer tahun,

        Integer bulan,

        @JsonProperty("kode_sasaran_opd")
        String kodeSasaranOpd,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        Double realisasi,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat,

        @JsonProperty("sasaran_opd")
        String sasaranOpd,

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
        String buktiPendukung
) {
}
