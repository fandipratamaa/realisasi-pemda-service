package cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenjaIndividuSubKegiatanResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        String tahun,
        String bulan,
        String nip,

        @JsonProperty("nama_pegawai")
        String namaPegawai,

        @JsonProperty("kode_subkegiatan")
        String kodeSubKegiatan,

        String subkegiatan,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        String indikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        Double target,

        Double realisasi,

        Double capaian,

        @JsonProperty("keterangan_capaian")
        String keteranganCapaian,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat
) {}
