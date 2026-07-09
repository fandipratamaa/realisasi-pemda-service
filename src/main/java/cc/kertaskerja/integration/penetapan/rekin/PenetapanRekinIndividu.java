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
    ) {
        public RekinIndividuData {
            if (rekins == null) rekins = List.of();
        }
    }

    public record RekinData(
            Long id,
            @JsonProperty("level_pk") Integer levelPk,
            @JsonProperty("kode_sasaran_opd") String kodeSasaranOpd,
            @JsonProperty("kode_pk") String kodePk,
            String rekin,
            @JsonProperty("nama_pemilik_pk") String namaPemilikPk,
            Integer versi,
            @JsonProperty("indikator_pk") List<IndikatorRekinData> indikatorPk
    ) {
        public RekinData {
            if (indikatorPk == null) indikatorPk = List.of();
        }
    }

    public record IndikatorRekinData(
            Long id,
            @JsonProperty("kode_indikator_pk") String kodeIndikatorPk,
            @JsonProperty("nama_indikator_pk") String namaIndikatorPk,
            @JsonProperty("target_pk") List<TargetRekinData> targetPk
    ) {
        public IndikatorRekinData {
            if (targetPk == null) targetPk = List.of();
        }
    }

    public record TargetRekinData(
            Long id,
            @JsonProperty("kode_target_pk") String kodeTargetPk,
            Integer tahun,
            Double target,
            String satuan
    ) {}
}
