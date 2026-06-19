package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renja_subkegiatan_opd")
public record RenjaSubKegiatanOpd(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String nip,

        String tahun,
        String bulan,

        @Column("kode_subkegiatan")
        String kodeSubKegiatan,

        @Column("kode_indikator")
        String kodeIndikator,

        @Column("kode_target")
        String kodeTarget,

        @Column("kode_pagu")
        String kodePagu,

        BigDecimal realisasi,

        @Column("jenis_realisasi")
        String jenisRealisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,

        @Column("faktor_penghambat")
        String faktorPenghambat,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public RenjaSubKegiatanOpd withFaktorPenunjang(String faktorPenunjang) {
        return new RenjaSubKegiatanOpd(
                id, kodeOpd, nip, tahun, bulan, kodeSubKegiatan, kodeIndikator, kodeTarget, kodePagu,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }

    public RenjaSubKegiatanOpd withFaktorPenghambat(String faktorPenghambat) {
        return new RenjaSubKegiatanOpd(
                id, kodeOpd, nip, tahun, bulan, kodeSubKegiatan, kodeIndikator, kodeTarget, kodePagu,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }
}
