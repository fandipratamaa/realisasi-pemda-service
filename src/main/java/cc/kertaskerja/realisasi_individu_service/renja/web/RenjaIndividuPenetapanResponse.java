package cc.kertaskerja.realisasi_individu_service.renja.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

public record RenjaIndividuPenetapanResponse(
        @JsonProperty("kode_opd")
        String kodeOpd,

        Integer tahun,
        Integer bulan,

        List<ProgramPenetapan> programs,
        List<KegiatanPenetapan> kegiatans,
        List<SubkegiatanPenetapan> subkegiatans
) {
    public record ProgramPenetapan(
            Long id,
            @JsonProperty("kode_program") String kodeProgram,
            String program,
            String nip,
            @JsonProperty("nama_pegawai") String namaPegawai,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record KegiatanPenetapan(
            Long id,
            @JsonProperty("kode_kegiatan") String kodeKegiatan,
            String kegiatan,
            String nip,
            @JsonProperty("nama_pegawai") String namaPegawai,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record SubkegiatanPenetapan(
            Long id,
            @JsonProperty("kode_subkegiatan") String kodeSubKegiatan,
            String subkegiatan,
            String nip,
            @JsonProperty("nama_pegawai") String namaPegawai,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") Double paguAnggaran
    ) {}

    public record IndikatorPenetapan(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            List<TargetPenetapan> targets
    ) {}

    public record TargetPenetapan(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            Integer tahun,
            Double target,
            Double realisasi,
            String satuan,
            Double capaian,
            @JsonProperty("keterangan_capaian") String keteranganCapaian
    ) {}
}
