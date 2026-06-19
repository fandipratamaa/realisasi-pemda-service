package cc.kertaskerja.realisasi_individu_service.renja.web;

import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RenjaIndividuListResponse(
        @JsonProperty("program")
        List<RenjaIndividuProgramResponse> program,

        @JsonProperty("kegiatan")
        List<RenjaIndividuKegiatanResponse> kegiatan,

        @JsonProperty("subkegiatan")
        List<RenjaIndividuSubKegiatanResponse> subkegiatan
) {}
