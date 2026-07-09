package cc.kertaskerja.integration.penetapan.tujuan_opd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanTujuanOpd {
    public record PenetapanTujuanOpdRoot(
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            @JsonProperty("tujuan_opds") List<TujuanPenetapanData> tujuanOpds
    ) {
        public PenetapanTujuanOpdRoot {
            if (tujuanOpds == null) tujuanOpds = List.of();
        }
    }

    public record TujuanPenetapanData(
            Long id,
            @JsonProperty("kode_tujuan_opd") String kodeTujuanOpd,
            @JsonProperty("tujuan_opd") String tujuanOpd,
            String periode,
            @JsonProperty("kode_opd") String kodeOpd,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanData> indikators
    ) {
        public TujuanPenetapanData {
            if (indikators == null) indikators = List.of();
        }
    }

    public record IndikatorPenetapanData(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            @JsonProperty("rumus_perhitungan") String rumusPerhitungan,
            @JsonProperty("sumber_data") String sumberData,
            @JsonProperty("definisi_operasional") String definisiOperasional,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<TargetPenetapanData> targets
    ) {
        public IndikatorPenetapanData {
            if (targets == null) targets = List.of();
        }
    }

    public record TargetPenetapanData(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            String satuan,
            Integer tahun,
            Double target
    ) {}
}
