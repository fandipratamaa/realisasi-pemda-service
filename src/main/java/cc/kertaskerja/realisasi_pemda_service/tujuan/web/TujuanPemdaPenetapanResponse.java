package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TujuanPemdaPenetapanResponse(
        Long id,

        @JsonProperty("kode_tujuan_pemda")
        String kodeTujuanPemda,

        @JsonProperty("tujuan_pemda")
        String tujuanPemda,

        List<IndikatorPenetapan> indikators
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

            List<TargetPenetapan> targets
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
            String keteranganCapaian,

            @JsonProperty("faktor_penunjang")
            String faktorPenunjang,

            @JsonProperty("faktor_penghambat")
            String faktorPenghambat,

            @JsonProperty("bukti_pendukung")
            String buktiPendukung,

            @JsonProperty("keterangan_bukti_pendukung")
            String keteranganBuktiPendukung
    ) {
    }
}
