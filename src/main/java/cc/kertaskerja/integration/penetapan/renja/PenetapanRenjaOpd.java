package cc.kertaskerja.integration.penetapan.renja;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanRenjaOpd {
    public record PenetapanRenjaOpdRoot(
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            List<ProgramPenetapanData> programs,
            List<KegiatanPenetapanData> kegiatans,
            List<SubkegiatanPenetapanData> subkegiatans
    ) {}

    public record ProgramPenetapanData(
            Long id,
            @JsonProperty("kode_program") String kodeProgram,
            String program,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanData> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record KegiatanPenetapanData(
            Long id,
            @JsonProperty("kode_kegiatan") String kodeKegiatan,
            String kegiatan,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanData> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record SubkegiatanPenetapanData(
            Long id,
            @JsonProperty("kode_subkegiatan") String kodeSubkegiatan,
            String subkegiatan,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanData> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record IndikatorPenetapanData(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            List<TargetPenetapanData> targets
    ) {}

    public record TargetPenetapanData(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            Integer tahun,
            Double target,
            String satuan
    ) {}
}
