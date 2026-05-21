package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PenetapanSasaranIndividuListResponse(
        @JsonProperty("kode_opd")
        String kodeOpd,

        @JsonProperty("tahun")
        Integer tahun,

        List<SasaranIndividuPenetapanResponse> sasaranIndividus
) {
}
