package cc.kertaskerja.integration.penetapan.sasaran_pemda;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PenetapanSasaranPemda {
    public record PenetapanSasaranPemdaRoot(
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            @JsonProperty("sasaran_pemdas") List<SasaranPenetapanPemdaData> sasaranPemdas
    ) {
        public PenetapanSasaranPemdaRoot {
            if (sasaranPemdas == null) sasaranPemdas = List.of();
        }
    }

    public record SasaranPenetapanPemdaData(
            Long id,
            @JsonProperty("kode_sasaran_pemda") String kodeSasaranPemda,
            @JsonProperty("sasaran_pemda") String sasaranPemda,
            String periode,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            Integer versi,
            @JsonProperty("is_locked") Boolean isLocked,
            List<IndikatorSasaranPemdaData> indikators
    ) {
        public SasaranPenetapanPemdaData {
            if (indikators == null) indikators = List.of();
        }
    }

    public record IndikatorSasaranPemdaData(
            Long id,
            @JsonProperty("kode_indikator") String kodeIndikator,
            String indikator,
            @JsonProperty("rumus_perhitungan") String rumusPerhitungan,
            @JsonProperty("sumber_data") String sumberData,
            @JsonProperty("definisi_operasional") String definisiOperasional,
            @JsonProperty("tahun_aktif") Integer tahunAktif,
            List<TargetSasaranPemdaData> targets
    ) {
        public IndikatorSasaranPemdaData {
            if (targets == null) targets = List.of();
        }
    }

    public record TargetSasaranPemdaData(
            Long id,
            @JsonProperty("kode_target") String kodeTarget,
            String satuan,
            Integer tahun,
            Double target
    ) {}
}
