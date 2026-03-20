package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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

        @Column("renja_id")
        String renjaPaguId,
        @Column("renja")
        String renjaPagu,
        @Column("jenis_renja")
        JenisRenja jenisRenjaPagu,
        Integer pagu,
        Integer realisasi,
        String satuan,
        String tahun,
        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        @Column("kode_opd")
        String kodeOpd,
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

        @Version int version
) {
    public static RenjaPagu of (
            String renjaPaguId,
            String renjaPagu,
            JenisRenja jenisRenjaPagu,
            Integer pagu,
            Integer realisasi,
            String satuan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd,
            RenjaPaguStatus status
    ) {
        return new RenjaPagu(null, 
                renjaPaguId, renjaPagu, jenisRenjaPagu, pagu, realisasi, satuan, 
                tahun, jenisRealisasi, kodeOpd, status, 
                null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianRenjaPagu());
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
