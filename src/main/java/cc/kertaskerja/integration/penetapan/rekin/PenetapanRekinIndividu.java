package cc.kertaskerja.integration.penetapan.rekin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanRekinIndividu {

    public record RekinIndividuData(
            @JsonProperty("pegawai_id") String pegawaiId,
            String nama,
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<RekinData> rekins
    ) {}

    public record RekinData(
            Long id,
            @JsonProperty("level_pk") Integer levelPk,
            @JsonProperty("kode_pk") String kodePk,
            String rekin,
            @JsonProperty("nama_pemilik_pk") String namaPemilikPk,
            Integer versi,
            @JsonProperty("indikator_pk") List<IndikatorRekinData> indikatorPk
    ) {}

    public record IndikatorRekinData(
            Long id,
            @JsonProperty("kode_indikator_pk") String kodeIndikatorPk,
            @JsonProperty("nama_indikator_pk") String namaIndikatorPk,
            @JsonProperty("target_pk") List<TargetRekinData> targetPk
    ) {}

    public record TargetRekinData(
            Long id,
            @JsonProperty("kode_target_pk") String kodeTargetPk,
            Integer tahun,
            Double target,
            String satuan
    ) {}
}