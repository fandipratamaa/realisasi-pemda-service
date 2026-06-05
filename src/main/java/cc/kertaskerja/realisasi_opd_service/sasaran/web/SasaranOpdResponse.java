package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SasaranOpdResponse(
        Long id,
        @JsonProperty("kode_opd")
        String kodeOpd,
        @JsonProperty("kode_sasaran_opd")
        String kodeSasaranOpd,
        @JsonProperty("sasaran_opd")
        String sasaranOpd,
        Integer tahun,
        Integer bulan,
        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,
        @JsonProperty("faktor_penghambat")
        String faktorPenghambat,
        List<IndikatorResponse> indikators
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
            List<TargetResponse> targets
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
