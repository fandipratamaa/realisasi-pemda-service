package cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenjaIndividuKegiatanResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        String tahun,
        String bulan,
        String nip,

        @JsonProperty("kode_kegiatan")
        String kodeKegiatan,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        @JsonProperty("kode_pagu")
        String kodePagu,

        Double realisasi,

        @JsonProperty("jenis_realisasi")
        String jenisRealisasi,

        Double capaian,

        @JsonProperty("keterangan_capaian")
        String keteranganCapaian,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat
) {}
