package cc.kertaskerja.realisasi_individu_service.renja.web.program;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SearchRenjaProgramResponse(
        @JsonProperty("pegawai_id") String pegawaiId,
        String nama,
        @JsonProperty("kode_opd") String kodeOpd,
        @JsonProperty("tahun_aktif") Integer tahunAktif,
        Integer bulan,
        List<RenjaIndividuProgramResponse> programs
) {}
