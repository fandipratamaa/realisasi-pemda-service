package cc.kertaskerja.realisasi_individu_service.renja.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenjaIndividuProgramResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        String tahun,
        String bulan,
        String nip,

        @JsonProperty("nama_pegawai")
        String namaPegawai,

        @JsonProperty("kode_program")
        String kodeProgram,

        String program,

        @JsonProperty("kode_indikator")
        String kodeIndikator,

        String indikator,

        @JsonProperty("kode_target")
        String kodeTarget,

        Double target,

        Double realisasi,

        Double capaian,

        @JsonProperty("keterangan_capaian")
        String keteranganCapaian
) {}
