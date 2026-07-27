package cc.kertaskerja.realisasi_individu_service.renja.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanRenjaIndividuResponse(
        @JsonProperty("pegawai_id") String pegawaiId,
        String nama,
        @JsonProperty("kode_opd") String kodeOpd,
        @JsonProperty("tahun_aktif") Integer tahunAktif,
        Integer bulan,
        List<RenjaPenetapanResponse> renjas
) {
    public record RenjaPenetapanResponse(
            Long id,
            @JsonProperty("kode_pk") String kodePk,
            @JsonProperty("level_pk") Integer levelPk,
            @JsonProperty("pegawai_id") String pegawaiId,
            @JsonProperty("nama_pegawai") String namaPegawai,
            @JsonProperty("kode_program") String kodeProgram,
            @JsonProperty("nama_program") String namaProgram,
            @JsonProperty("kode_pagu_program") String kodePaguProgram,
            @JsonProperty("pagu_program") Long paguProgram,
            @JsonProperty("indikator_programs") List<IndikatorPenetapanResponse> indikatorPrograms,
            @JsonProperty("kode_kegiatan") String kodeKegiatan,
            @JsonProperty("nama_kegiatan") String namaKegiatan,
            @JsonProperty("kode_pagu_kegiatan") String kodePaguKegiatan,
            @JsonProperty("pagu_kegiatan") Long paguKegiatan,
            @JsonProperty("indikator_kegiatans") List<IndikatorPenetapanResponse> indikatorKegiatans,
            @JsonProperty("kode_subkegiatan") String kodeSubkegiatan,
            @JsonProperty("nama_subkegiatan") String namaSubkegiatan,
            @JsonProperty("kode_pagu_subkegiatan") String kodePaguSubkegiatan,
            @JsonProperty("pagu_subkegiatan") Long paguSubkegiatan,
            @JsonProperty("indikator_subkegiatans") List<IndikatorPenetapanResponse> indikatorSubkegiatans
    ) {}

    public record IndikatorPenetapanResponse(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            List<TargetPenetapanResponse> targets
    ) {}

    public record TargetPenetapanResponse(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            Integer tahun,
            Double target,
            String satuan,
            @JsonProperty("realisasi_target") Double realisasiTarget,
            @JsonProperty("realisasi_pagu") Double realisasiPagu,
            @JsonProperty("capaian_target") Double capaianTarget,
            @JsonProperty("keterangan_capaian_target") String keteranganCapaianTarget,
            @JsonProperty("capaian_pagu") Double capaianPagu,
            @JsonProperty("keterangan_capaian_pagu") String keteranganCapaianPagu,
            @JsonProperty("faktor_penunjang") String faktorPenunjang,
            @JsonProperty("faktor_penghambat") String faktorPenghambat,
            @JsonProperty("bukti_pendukung") String buktiPendukung,
            @JsonProperty("keterangan_bukti_pendukung") String keteranganBuktiPendukung,
            @JsonProperty("jenis_realisasi") String jenisRealisasi
    ) {}
}
