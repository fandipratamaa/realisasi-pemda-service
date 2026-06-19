package cc.kertaskerja.realisasi_individu_service.renja.web.program;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenjaIndividuProgramResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        String tahun,
        String bulan,
        String nip,

        @JsonProperty("kode_program")
        String kodeProgram,

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
