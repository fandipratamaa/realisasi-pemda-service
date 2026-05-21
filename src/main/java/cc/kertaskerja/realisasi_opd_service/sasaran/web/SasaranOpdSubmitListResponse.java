package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SasaranOpdSubmitListResponse(
        @JsonProperty("kode_opd")
        String kodeOpd,

        @JsonProperty("tahun")
        Integer tahun,

        List<SasaranOpdResponse> sasaranOpds
) {
}
