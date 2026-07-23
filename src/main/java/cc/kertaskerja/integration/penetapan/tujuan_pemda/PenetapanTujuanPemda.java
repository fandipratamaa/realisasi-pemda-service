package cc.kertaskerja.integration.penetapan.tujuan_pemda;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanTujuanPemda {
    public record PenetapanTujuanPemdaRoot(
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            @JsonProperty("tujuan_pemdas") List<TujuanPenetapanPemdaData> tujuanPemdas
    ) {
        public PenetapanTujuanPemdaRoot {
            if (tujuanPemdas == null) tujuanPemdas = List.of();
        }
    }

    public record TujuanPenetapanPemdaData(
            Long id,
            String visi,
            String misi,
            @JsonProperty("kode_tujuan_pemda") String kodeTujuanPemda,
            @JsonProperty("tujuan_pemda") String tujuanPemda,
            String periode,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorPenetapanPemdaData> indikators
    ) {
        public TujuanPenetapanPemdaData {
            if (indikators == null) indikators = List.of();
        }
    }

    public record IndikatorPenetapanPemdaData(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            @JsonProperty("rumus_perhitungan") String rumusPerhitungan,
            @JsonProperty("sumber_data") String sumberData,
            @JsonProperty("definisi_operasional") String definisiOperasional,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<TargetPenetapanPemdaData> targets
    ) {
        public IndikatorPenetapanPemdaData {
            if (targets == null) targets = List.of();
        }
    }

    public record TargetPenetapanPemdaData(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            String satuan,
            Integer tahun,
            Double target
    ) {}
}
