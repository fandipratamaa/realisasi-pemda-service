package cc.kertaskerja.realisasi_individu_service.rekin.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanRekinIndividuResponse(
        @JsonProperty("pegawai_id") String pegawaiId,
        String nama,
        @JsonProperty("kode_opd") String kodeOpd,
        @JsonProperty("tahun_aktif") Integer tahunAktif,
        Integer bulan,
        List<RekinPenetapanResponse> rekins
) {
    public record RekinPenetapanResponse(
            Long id,
            @JsonProperty("kode_sasaran_opd") String kodeSasaranOpd,
            @JsonProperty("kode_pk") String kodePk,
            String rekin,
            Integer versi,
            @JsonProperty("indikator_pk") List<IndikatorPenetapanResponse> indikatorPk
    ) {}

    public record IndikatorPenetapanResponse(
            Long id,
            @JsonProperty("kode_indikator_pk") String kodeIndikatorPk,
            @JsonProperty("nama_indikator_pk") String namaIndikatorPk,
            @JsonProperty("target_pk") List<TargetPenetapanResponse> targetPk
    ) {}

    public record TargetPenetapanResponse(
            Long id,
            @JsonProperty("kode_target_pk") String kodeTargetPk,
            Integer tahun,
            Double target,
            String satuan,
            Double realisasi,
            Double capaian,
            @JsonProperty("keterangan_capaian") String keteranganCapaian,
            @JsonProperty("faktor_penunjang") String faktorPenunjang,
            @JsonProperty("faktor_penghambat") String faktorPenghambat,
            @JsonProperty("bukti_pendukung") String buktiPendukung,
            @JsonProperty("keterangan_bukti_pendukung") String keteranganBuktiPendukung,
            @JsonProperty("jenis_realisasi") String jenisRealisasi
    ) {}
}
