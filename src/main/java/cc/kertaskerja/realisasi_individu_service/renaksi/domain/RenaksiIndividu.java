package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renaksi_individu")
public record RenaksiIndividu(
        @Id Long id,

        @Column("sasaran_id")
        Long sasaranId,

        @Column("kode_opd")
        String kodeOpd,
        String nip,

        @Column("kode_renaksi")
        String kodeRenaksi,
        String renaksi,
        String tahun,
        String bulan,
        RenaksiStatus status,

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
        Instant lastModifiedDate,

        @Version int version
) {
    public static RenaksiIndividu of(
            Long sasaranId,
            String kodeOpd,
            String nip,
            String kodeRenaksi,
            String renaksi,
            String tahun,
            String bulan,
            RenaksiStatus status
    ) {
        return new RenaksiIndividu(null, sasaranId, kodeOpd, nip, kodeRenaksi, renaksi, tahun, bulan, status,
                null, null, null, null, 0);
    }
}
