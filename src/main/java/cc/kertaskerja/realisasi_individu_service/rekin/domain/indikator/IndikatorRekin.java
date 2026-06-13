package cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_rekin")
public record IndikatorRekin(
        @Id Long id,

        @Column("rekin_id")
        Long rekinId,

        @Column("kode_indikator")
        String kodeIndikator,
        String indikator,

        @Column("kode_opd")
        String kodeOpd,
        String nip,
        String tahun,
        String bulan,

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
    public static IndikatorRekin of(
            Long rekinId,
            String kodeIndikator,
            String indikator,
            String kodeOpd,
            String nip,
            String tahun,
            String bulan
    ) {
        return new IndikatorRekin(null, rekinId, kodeIndikator, indikator, kodeOpd, nip, tahun, bulan,
                null, null, null, null);
    }
}
