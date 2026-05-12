package cc.kertaskerja.realisasi_opd_service.renja.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RenjaOpdHierarkiResponse(List<DataItem> data) {

    public record DataItem(
            @JsonProperty("kode_opd") String kodeOpd,
            String tahun,
            String bulan,
            @JsonProperty("pagu_total_realisasi") long paguTotalRealisasi,
            @JsonProperty("id_renja") String idRenja,
            List<RenjaItem> program
    ) {
    }

    public record RenjaItem(
            @JsonProperty("kode_renja") String kodeRenja,
            @JsonProperty("nama_renja") String namaRenja,
            @JsonProperty("jenis_renja") String jenisRenja,
            @JsonInclude(JsonInclude.Include.NON_NULL) List<TargetItem> target,
            @JsonInclude(JsonInclude.Include.NON_NULL) List<PaguItem> pagu,
            List<IndikatorItem> indikator,
            @JsonInclude(JsonInclude.Include.NON_NULL) List<RenjaItem> program,
            @JsonInclude(JsonInclude.Include.NON_NULL) List<RenjaItem> kegiatan,
            @JsonInclude(JsonInclude.Include.NON_NULL) List<RenjaItem> subkegiatan
    ) {
    }

    public record TargetItem(
            Long targetRealisasiId,
            @JsonProperty("id_target") String idTarget,
            String target,
            String realisasi,
            String satuan,
            String jenisRealisasi,
            String status,
            String createdBy,
            String lastModifiedBy,
            String capaian,
            String keteranganCapaian
    ) {
    }

    public record PaguItem(
            Long paguRealisasiId,
            String realisasi,
            Integer pagu,
            String status,
            String createdBy,
            String lastModifiedBy,
            String capaian,
            String keteranganCapaian
    ) {
    }

    public record IndikatorItem(
            @JsonProperty("id_indikator") String idIndikator,
            String indikator
    ) {
    }
}
