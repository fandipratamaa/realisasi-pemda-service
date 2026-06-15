package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("rekin")
public record Rekin(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,
        String nip,

        @Column("kode_pk_rekin")
        String kodePkRekin,

        @Column("kode_sasaran_opd")
        String kodeSasaranOpd,
        String rekin,
        String tahun,
        String bulan,
        RekinStatus status,

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
    public static Rekin of(
            String kodeOpd,
            String nip,
            String kodePkRekin,
            String kodeSasaranOpd,
            Integer levelPk,
            String namaPemilikPk,
            String rekin,
            String tahun,
            String bulan,
            RekinStatus status
    ) {
        return new Rekin(null, kodeOpd, nip, kodePkRekin, kodeSasaranOpd, rekin, tahun, bulan, status,
                null, null, null, null);
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
