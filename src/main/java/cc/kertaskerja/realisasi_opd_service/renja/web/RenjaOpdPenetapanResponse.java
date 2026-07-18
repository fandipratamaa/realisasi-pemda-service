package cc.kertaskerja.realisasi_opd_service.renja.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RenjaOpdPenetapanResponse(
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
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") List<PaguAnggaranPenetapan> paguAnggaran
    ) {}

    public record KegiatanPenetapan(
            Long id,
            @JsonProperty("kode_kegiatan") String kodeKegiatan,
            String kegiatan,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") List<PaguAnggaranPenetapan> paguAnggaran
    ) {}

    public record SubkegiatanPenetapan(
            Long id,
            @JsonProperty("kode_subkegiatan") String kodeSubkegiatan,
            String subkegiatan,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapan> indikators,
            @JsonProperty("pagu_anggaran") List<PaguAnggaranPenetapan> paguAnggaran
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
            Integer bulan,
            Double target,
            Double realisasi,
            String satuan,
            Double capaian,
            @JsonProperty("keterangan_capaian") String keteranganCapaian,
            @JsonProperty("faktor_penunjang") String faktorPenunjang,
            @JsonProperty("faktor_penghambat") String faktorPenghambat,
            @JsonProperty("bukti_pendukung") String buktiPendukung
    ) {}

    public record PaguAnggaranPenetapan(
            Long id,
            @JsonProperty("kode_pagu") String kodePagu,
            Double pagu,
            @JsonProperty("jenis_pagu") String jenisPagu
    ) {}
}
