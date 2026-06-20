package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.capaian.domain.Capaian;
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

        @Column("kode_opd")
        String kodeOpd,
        String nip,

        @Column("kode_sasaran")
        String kodeSasaran,

        @Column("kode_renaksi")
        String kodeRenaksi,

        @Column("kode_indikator")
        String kodeIndikator,

        @Column("kode_target")
        String kodeTarget,

        @Column("pagu_anggaran")
        BigDecimal paguAnggaran,
        BigDecimal realisasi,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,
        @Column("faktor_penghambat")
        String faktorPenghambat,

        @CreatedBy
        @Column("created_by")
        String createdBy,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy,
        @CreatedDate
        @Column("created_date")
        Instant createdDate,
        @LastModifiedDate
        @Column("last_modified_date")
        Instant lastModifiedDate
) {
    public static RenaksiIndividu of(
            String kodeOpd,
            String nip,
            String kodeSasaran,
            String kodeRenaksi,
            String kodeIndikator,
            String kodeTarget,
            BigDecimal paguAnggaran,
            BigDecimal realisasi,
            JenisRealisasi jenisRealisasi,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        return new RenaksiIndividu(null, kodeOpd, nip, kodeSasaran, kodeRenaksi, kodeIndikator, kodeTarget,
                paguAnggaran, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                null, null, null, null);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianTarget();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("satuan")
    public String satuan() {
        return "%";
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
        if (paguAnggaran == null || paguAnggaran.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        Capaian capaian = new Capaian(realisasi.doubleValue(), paguAnggaran.toString(), jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
