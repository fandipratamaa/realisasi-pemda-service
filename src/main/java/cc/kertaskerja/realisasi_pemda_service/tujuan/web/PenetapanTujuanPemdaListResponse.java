package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanTujuanPemdaListResponse(
        @JsonProperty("tahun")
        Integer tahun,

        @JsonProperty("bulan")
        Integer bulan,

        List<TujuanPemdaPenetapanResponse> tujuanPemdas
) {
}
