package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renaksi_individu")
public record RenaksiIndividu(
        @Id Long id,

        @Column("kode_opd") String kodeOpd,
        String nip,

        @Column("kode_sasaran") String kodeSasaran,

        @Column("sasaran") String sasaran,

        @Column("kode_renaksi") String kodeRenaksi,

        @Column("renaksi") String renaksi,

        @Column("kode_indikator") String kodeIndikator,

        @Column("indikator") String indikator,

        @Column("kode_target") String kodeTarget,

        @Column("target") BigDecimal target,

        @Column("pagu_anggaran") BigDecimal paguAnggaran,
        BigDecimal realisasi,
        String tahun,
        String bulan,
        String satuan,
        RenaksiStatus status,

        @Column("jenis_realisasi") JenisRealisasi jenisRealisasi,

        @Column("faktor_penunjang") String faktorPenunjang,
        @Column("faktor_penghambat") String faktorPenghambat,

        @CreatedBy @Column("created_by") String createdBy,
        @LastModifiedBy @Column("last_modified_by") String lastModifiedBy,
        @CreatedDate @Column("created_date") Instant createdDate,
        @LastModifiedDate @Column("last_modified_date") Instant lastModifiedDate) {
    public static RenaksiIndividu of(
            String kodeOpd,
            String nip,
            String kodeSasaran,
            String sasaran,
            String kodeRenaksi,
            String renaksi,
            String kodeIndikator,
            String indikator,
            String kodeTarget,
            BigDecimal target,
            BigDecimal paguAnggaran,
            BigDecimal realisasi,
            String tahun,
            String bulan,
            String satuan,
            RenaksiStatus status,
            JenisRealisasi jenisRealisasi,
            String faktorPenunjang,
            String faktorPenghambat) {
        return new RenaksiIndividu(null, kodeOpd, nip, kodeSasaran, sasaran, kodeRenaksi, renaksi,
                kodeIndikator, indikator, kodeTarget, target, paguAnggaran, realisasi,
                tahun, bulan, satuan, status, jenisRealisasi,
                faktorPenunjang, faktorPenghambat,
                null, null, null, null);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianTarget();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianTarget();
        return calculatedCapaian > 100
                ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")"
                : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianTarget() {
        if (target == null || target.compareTo(BigDecimal.ZERO) == 0 || realisasi == null
                || realisasi.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        double realisasiVal = realisasi.doubleValue();
        double targetVal = target.doubleValue();
        return realisasiVal / targetVal * 100;
    }
}
