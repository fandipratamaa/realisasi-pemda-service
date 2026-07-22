package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record RenaksiResponse(
        Long id,
        @JsonProperty("kode_opd") String kodeOpd,
        String nip,
        @JsonProperty("kode_rekin") String kodeRekin,
        @JsonProperty("kode_renaksi") String kodeRenaksi,
        @JsonProperty("kode_pelaksanaan") String kodePelaksanaan,
        BigDecimal realisasi,
        @JsonProperty("bobot_pelaksanaan") Double bobot,
        Double capaian,
        @JsonProperty("keterangan_capaian") String keteranganCapaian,
        String tahun,
        String bulan,
        String satuan,
        RenaksiStatus status,
        @JsonProperty("jenis_realisasi") JenisRealisasi jenisRealisasi,
        @JsonProperty("faktor_penunjang") String faktorPenunjang,
        @JsonProperty("faktor_penghambat") String faktorPenghambat,
        @JsonProperty("bukti_pendukung") String buktiPendukung,
        @JsonProperty("keterangan_bukti_pendukung") String keteranganBuktiPendukung,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("last_modified_by") String lastModifiedBy,
        @JsonProperty("created_date") Instant createdDate,
        @JsonProperty("last_modified_date") Instant lastModifiedDate
) {
    public static RenaksiResponse from(RenaksiIndividu entity, Double bobot, Double capaian, String keteranganCapaian) {
        return new RenaksiResponse(
                entity.id(),
                entity.kodeOpd(),
                entity.nip(),
                entity.kodeRekin(),
                entity.kodeRenaksi(),
                entity.kodePelaksanaan(),
                entity.realisasi(),
                bobot,
                capaian,
                keteranganCapaian,
                entity.tahun(),
                entity.bulan(),
                entity.satuan(),
                entity.status(),
                entity.jenisRealisasi(),
                entity.faktorPenunjang(),
                entity.faktorPenghambat(),
                entity.buktiPendukung(),
                entity.keteranganBuktiPendukung(),
                entity.createdBy(),
                entity.lastModifiedBy(),
                entity.createdDate(),
                entity.lastModifiedDate()
        );
    }
}
