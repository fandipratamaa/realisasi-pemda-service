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

@Table("sasaran_individu")
public record SasaranIndividu(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,
        String nip,

        @Column("kode_sasaran")
        String kodeSasaran,
        String sasaran,
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
    public static SasaranIndividu of(
            String kodeOpd,
            String nip,
            String kodeSasaran,
            String sasaran,
            String tahun,
            String bulan,
            RenaksiStatus status
    ) {
        return new SasaranIndividu(null, kodeOpd, nip, kodeSasaran, sasaran, tahun, bulan, status,
                null, null, null, null, 0);
    }
}
