package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanSasaranPemdaListResponse(
        @JsonProperty("tahun_aktif") Integer tahunAktif,
        Integer bulan,
        List<SasaranPemdaPenetapanResponse> data
) {
}
