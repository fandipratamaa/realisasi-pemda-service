package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanTujuanOpdListResponse(
        @JsonProperty("kode_opd")
        String kodeOpd,

        @JsonProperty("tahun")
        Integer tahun,

        @JsonProperty("bulan")
        Integer bulan,

        List<TujuanOpdPenetapanResponse> tujuanOpds
) {
}
