package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SasaranIndividuPenetapanResponse(
        Long id,

        @JsonProperty("kode_sasaran_opd")
        String kodeSasaranOpd,

        @JsonProperty("sasaran_opd")
        String sasaranOpd,

        String nip,

        @JsonProperty("nama_pegawai")
        String namaPegawai,

        List<SasaranIndividuPenetapanResponse.IndikatorPenetapan> indikators
) {
    public record IndikatorPenetapan(
            @JsonProperty("kode_indikator")
            String kodeIndikator,

            String indikator,

            @JsonProperty("rumus_perhitungan")
            String rumusPerhitungan,

            @JsonProperty("sumber_data")
            String sumberData,

            @JsonProperty("definisi_operasional")
            String definisiOperasional,

            List<SasaranIndividuPenetapanResponse.TargetPenetapan> targets
    ) {
    }

    public record TargetPenetapan(
            @JsonProperty("kode_target")
            String kodeTarget,

            String satuan,

            Double target,

            Double realisasi,

            Double capaian,

            @JsonProperty("keterangan_capaian")
            String keteranganCapaian
    ) {
    }
}
