package cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_kegiatan_opd")
public record RenjaKegiatanOpdHeader(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        @Column("kode_program")
        String kodeProgram,

        @Column("kode_kegiatan")
        String kodeKegiatan,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public static RenjaKegiatanOpdHeader of(String kodeOpd, String kodeProgram, String kodeKegiatan, String tahun, String bulan) {
        return new RenjaKegiatanOpdHeader(null, kodeOpd, kodeProgram, kodeKegiatan, tahun, bulan,
                null, null, null, null);
    }
}
