package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_tujuan_opd")
public record TujuanOpd(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String tahun,

        String bulan,

        @Column("kode_tujuan_opd")
        String kodeTujuanOpd,

        @Column("kode_indikator")
        String kodeIndikator,

        @Column("kode_target")
        String kodeTarget,

        BigDecimal realisasi,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,

        @Column("faktor_penghambat")
        String faktorPenghambat,

        @Column("bukti_pendukung")
        String buktiPendukung,

        @Column("keterangan_bukti_pendukung")
        String keteranganBuktiPendukung,

        @CreatedBy
        @Column("created_by")
        String createdBy,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy
) {
    public static TujuanOpd of(
            String kodeOpd,
            String tahun,
            String bulan,
            String kodeTujuanOpd,
            String kodeIndikator,
            String kodeTarget,
            BigDecimal realisasi,
            JenisRealisasi jenisRealisasi,
            String buktiPendukung,
            String keteranganBuktiPendukung
    ) {
        return new TujuanOpd(null, kodeOpd, tahun, bulan, kodeTujuanOpd, kodeIndikator, kodeTarget,
                realisasi, jenisRealisasi, "", "", buktiPendukung, keteranganBuktiPendukung,
                null, null, null, null);
    }

    public record CapaianResult(Double capaian, String keteranganCapaian) {}

    public static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (realisasi == null || target == null || target == 0 || realisasi == 0) {
            return new CapaianResult(null, null);
        }
        double calculatedCapaian = realisasi / target * 100;
        String keteranganCapaian = null;
        if (calculatedCapaian > 100) {
            keteranganCapaian = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculatedCapaian) + ")";
        }
        return new CapaianResult(Math.min(calculatedCapaian, 100), keteranganCapaian);
    }

    public TujuanOpd withFaktorPenunjang(String faktorPenunjang) {
        return new TujuanOpd(id, kodeOpd, tahun, bulan, kodeTujuanOpd, kodeIndikator, kodeTarget,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdBy, createdDate, lastModifiedDate, lastModifiedBy);
    }

    public TujuanOpd withFaktorPenghambat(String faktorPenghambat) {
        return new TujuanOpd(id, kodeOpd, tahun, bulan, kodeTujuanOpd, kodeIndikator, kodeTarget,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdBy, createdDate, lastModifiedDate, lastModifiedBy);
    }
}
