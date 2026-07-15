package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinIndividu;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record RekinResponse(
        Long id,

        @JsonProperty("kode_opd")
        String kodeOpd,

        String nip,
        String tahun,
        String bulan,

        @JsonProperty("kode_pk_rekin")
        String kodePkRekin,

        @JsonProperty("kode_indikator_pk_rekin")
        String kodeIndikatorPkRekin,

        @JsonProperty("kode_target_pk_rekin")
        String kodeTargetPkRekin,

        @JsonProperty("kode_sasaran_opd")
        String kodeSasaranOpd,

        BigDecimal realisasi,

        @JsonProperty("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @JsonProperty("faktor_penunjang")
        String faktorPenunjang,

        @JsonProperty("faktor_penghambat")
        String faktorPenghambat,

        @JsonProperty("bukti_pendukung")
        String buktiPendukung,

        @JsonProperty("created_by")
        String createdBy,

        @JsonProperty("last_modified_by")
        String lastModifiedBy,

        @JsonProperty("created_date")
        Instant createdDate,

        @JsonProperty("last_modified_date")
        Instant lastModifiedDate,

        Double target,

        Double capaian,

        @JsonProperty("keterangan_capaian")
        String keteranganCapaian,

        @JsonProperty("keterangan_bukti_pendukung")
        String keteranganBuktiPendukung
) {
    public static RekinResponse from(RekinIndividu entity, Double target, Double capaian, String keteranganCapaian) {
        return new RekinResponse(
                entity.id(), entity.kodeOpd(), entity.nip(), entity.tahun(), entity.bulan(),
                entity.kodePkRekin(), entity.kodeIndikatorPkRekin(), entity.kodeTargetPkRekin(),
                entity.kodeSasaranOpd(),
                entity.realisasi(), entity.jenisRealisasi(),
                entity.faktorPenunjang(), entity.faktorPenghambat(), entity.buktiPendukung(),
                entity.createdBy(), entity.lastModifiedBy(),
                entity.createdDate(), entity.lastModifiedDate(),
                target, capaian, keteranganCapaian, entity.keteranganBuktiPendukung()
        );
    }
}
