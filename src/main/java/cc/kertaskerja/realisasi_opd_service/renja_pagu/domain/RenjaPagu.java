package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table("renja_pagu")
public record RenjaPagu(
        @Id Long id,

        @Column("jenis_renja_id")
        String jenisRenjaId,
        @Column("jenis_renja")
        JenisRenja jenisRenjaPagu,
        Integer pagu,
        Integer realisasi,
        String satuan,
        String tahun,
        String bulan,
        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        @Column("kode_opd")
        String kodeOpd,
        @Column("kode_renja")
        String kodeRenja,
        RenjaPaguStatus status,

        // sementara override nilai kolom created_by di DataConfig.java
        @CreatedBy
        @Column("created_by")
        String createdBy,
        @CreatedDate
        @Column("created_date")
        Instant createdDate,
        @LastModifiedDate
        @Column("last_modified_date")
        Instant lastModifiedDate,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy,

        @Version int version
) {
    public static RenjaPagu of (
            String jenisRenjaId,
            JenisRenja jenisRenjaPagu,
            Integer pagu,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi,
            String kodeOpd,
            String kodeRenja,
            RenjaPaguStatus status
    ) {
        return new RenjaPagu(null,
                jenisRenjaId, jenisRenjaPagu, pagu, realisasi, satuan,
                tahun, bulan, jenisRealisasi, kodeOpd, kodeRenja, status,
                null, null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianRenjaPagu();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianRenjaPagu();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianRenjaPagu() {
        if (pagu == null || pagu == 0 || realisasi == null) {
            return 0.0;
        }

        double paguValue = pagu.doubleValue();
        double realisasiValue = realisasi.doubleValue();
        return (realisasiValue / paguValue) * 100;
    }
}
