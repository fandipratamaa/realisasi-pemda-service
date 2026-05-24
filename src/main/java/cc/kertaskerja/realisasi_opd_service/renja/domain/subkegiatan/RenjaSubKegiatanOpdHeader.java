package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_subkegiatan_opd")
public record RenjaSubKegiatanOpdHeader(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        @Column("kode_kegiatan")
        String kodeKegiatan,

        @Column("kode_subkegiatan")
        String kodeSubKegiatan,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public static RenjaSubKegiatanOpdHeader of(String kodeOpd, String kodeKegiatan, String kodeSubKegiatan, String tahun, String bulan) {
        return new RenjaSubKegiatanOpdHeader(null, kodeOpd, kodeKegiatan, kodeSubKegiatan, tahun, bulan,
                null, null, null, null);
    }
}
