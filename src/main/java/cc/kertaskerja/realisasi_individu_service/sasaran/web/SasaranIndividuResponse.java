package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SasaranIndividuResponse(
        Long id,
        @JsonProperty("kode_opd")
        String kodeOpd,
        @JsonProperty("kode_sasaran_opd")
        String kodeSasaranOpd,
        String nip,
        @JsonProperty("nama_pegawai")
        String namaPegawai,
        @JsonProperty("sasaran_opd")
        String sasaranOpd,
        Integer tahun,
        Integer bulan,
        List<SasaranIndividuResponse.IndikatorResponse> indikators
) {
    public record IndikatorResponse(
            Long id,
            @JsonProperty("kode_indikator")
            String kodeIndikator,
            String indikator,
            @JsonProperty("rumus_perhitungan")
            String rumusPerhitungan,
            @JsonProperty("sumber_data")
            String sumberData,
            @JsonProperty("definisi_operasional")
            String definisiOperasional,
            Integer tahun,
            Integer bulan,
            List<SasaranIndividuResponse.TargetResponse> targets
    ) {
    }

    public record TargetResponse(
            Long id,
            @JsonProperty("kode_target")
            String kodeTarget,
            Double target,
            String satuan,
            Integer tahun,
            Integer bulan,
            Double realisasi,
            Double capaian,
            @JsonProperty("keterangan_capaian")
            String keteranganCapaian
    ) {
    }
}
