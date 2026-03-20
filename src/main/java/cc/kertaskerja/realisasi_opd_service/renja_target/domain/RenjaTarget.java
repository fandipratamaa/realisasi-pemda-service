package cc.kertaskerja.realisasi_opd_service.renja_target.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_target")
public record RenjaTarget(
        @Id Long id,

        @Column("renja_id")
        String renjaTargetId,
        @Column("renja")
        String renjaTarget,
        @Column("jenis_renja")
        JenisRenja jenisRenjaTarget,
        @Column("indikator_id")
        String indikatorId,
        @Column("indikator")
        String indikator,
        @Column("target_id")
        String targetId,
        @Column("target")
        String target,
        Integer realisasi,
        String satuan,
        String tahun,
        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        @Column("kode_opd")
        String kodeOpd,
        RenjaTargetStatus status,

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
    public static RenjaTarget of(
            String renjaId,
            String renja,
            JenisRenja jenisRenja,
            String indikatorId,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd,
            RenjaTargetStatus status
    ) {
        return new RenjaTarget(null,
                renjaId, renja, jenisRenja, indikatorId, indikator, targetId, target,
                realisasi, satuan, tahun, jenisRealisasi, kodeOpd, status,
                null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianRenjaTarget());
    }

    public Double capaianRenjaTarget() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
