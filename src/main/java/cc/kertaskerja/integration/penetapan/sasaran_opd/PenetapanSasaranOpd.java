package cc.kertaskerja.integration.penetapan.sasaran_opd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanSasaranOpd {
    public record PenetapanSasaranOpdRoot(
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            @JsonProperty("sasaran_opds") List<SasaranPenetapanData> sasaranOpds
    ) {}

    public record SasaranPenetapanData(
            Long id,
            @JsonProperty("kode_sasaran_opd") String kodeSasaranOpd,
            @JsonProperty("sasaran_opd") String sasaranOpd,
            String periode,
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanData> indikators
    ) {}

    public record IndikatorPenetapanData(
            Long id,
            @JsonProperty("id_sasaran_opd") Long idSasaranOpd,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            @JsonProperty("rumus_perhitungan") String rumusPerhitungan,
            @JsonProperty("sumber_data") String sumberData,
            @JsonProperty("definisi_operasional") String definisiOperasional,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<TargetPenetapanData> targets
    ) {}

    public record TargetPenetapanData(
            Long id,
            @JsonProperty("indikator_id") Long indikatorId,
            @JsonProperty("kode_target") String kodeTarget,
            String satuan,
            Integer tahun,
            Double target
    ) {}
}
