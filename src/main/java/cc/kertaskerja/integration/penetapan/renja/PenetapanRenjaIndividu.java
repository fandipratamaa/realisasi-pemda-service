package cc.kertaskerja.integration.penetapan.renja;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanRenjaIndividu {

    public record RenjaIndividuData(
            @JsonProperty("pegawai_id") String pegawaiId,
            String nama,
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<RenjaData> renjas
    ) {
        public RenjaIndividuData {
            if (renjas == null) renjas = List.of();
        }
    }

    public record RenjaData(
            Long id,
            @JsonProperty("kode_pk") String kodePk,
            @JsonProperty("level_pk") Integer levelPk,
            @JsonProperty("pegawai_id") String pegawaiId,
            @JsonProperty("nama_pegawai") String namaPegawai,
            @JsonProperty("kode_program") String kodeProgram,
            @JsonProperty("nama_program") String namaProgram,
            @JsonProperty("kode_pagu_program") String kodePaguProgram,
            @JsonProperty("pagu_program") Long paguProgram,
            @JsonProperty("indikator_programs") List<IndikatorPenetapanData> indikatorPrograms,
            @JsonProperty("kode_kegiatan") String kodeKegiatan,
            @JsonProperty("nama_kegiatan") String namaKegiatan,
            @JsonProperty("kode_pagu_kegiatan") String kodePaguKegiatan,
            @JsonProperty("pagu_kegiatan") Long paguKegiatan,
            @JsonProperty("indikator_kegiatans") List<IndikatorPenetapanData> indikatorKegiatans,
            @JsonProperty("kode_subkegiatan") String kodeSubkegiatan,
            @JsonProperty("nama_subkegiatan") String namaSubkegiatan,
            @JsonProperty("kode_pagu_subkegiatan") String kodePaguSubkegiatan,
            @JsonProperty("pagu_subkegiatan") Long paguSubkegiatan,
            @JsonProperty("indikator_subkegiatans") List<IndikatorPenetapanData> indikatorSubkegiatans
    ) {
        public RenjaData {
            if (indikatorPrograms == null) indikatorPrograms = List.of();
            if (indikatorKegiatans == null) indikatorKegiatans = List.of();
            if (indikatorSubkegiatans == null) indikatorSubkegiatans = List.of();
        }
    }

    public record IndikatorPenetapanData(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            List<TargetPenetapanData> targets
    ) {
        public IndikatorPenetapanData {
            if (targets == null) targets = List.of();
        }
    }

    public record TargetPenetapanData(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            Integer tahun,
            Double target,
            String satuan
    ) {}
}
