package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("sasaran_opd")
public record SasaranOpd(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,
        @Column("kode_sasaran_opd")
        String kodeSasaranOpd,
        String tahun,
        String bulan,
        @Column("faktor_penunjang")
        String faktorPenunjang,
        @Column("faktor_penghambat")
        String faktorPenghambat,

        @CreatedBy
        @Column("created_by")
        String createdBy,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy
) {
    public static SasaranOpd of(
            String kodeOpd,
            String kodeSasaranOpd,
            String tahun,
            String bulan
    ) {
        return new SasaranOpd(null,
                kodeOpd, kodeSasaranOpd, tahun, bulan,
                "", "",
                null, null, null, null);
    }

    public SasaranOpd withFaktorPenunjang(String faktorPenunjang) {
        return new SasaranOpd(id, kodeOpd, kodeSasaranOpd, tahun, bulan,
                faktorPenunjang, faktorPenghambat,
                createdBy, createdDate, lastModifiedDate, lastModifiedBy);
    }

    public SasaranOpd withFaktorPenghambat(String faktorPenghambat) {
        return new SasaranOpd(id, kodeOpd, kodeSasaranOpd, tahun, bulan,
                faktorPenunjang, faktorPenghambat,
                createdBy, createdDate, lastModifiedDate, lastModifiedBy);
    }

    public record CapaianResult(Double capaian, String keteranganCapaian) {}

    public static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (realisasi == null || target == null || target == 0) {
            return new CapaianResult(null, null);
        }
        double calculatedCapaian = realisasi / target * 100;
        String keteranganCapaian = null;
        if (calculatedCapaian > 100) {
            keteranganCapaian = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculatedCapaian) + ")";
        }
        return new CapaianResult(Math.min(calculatedCapaian, 100), keteranganCapaian);
    }
}
