package cc.kertaskerja.realisasi_opd_service.renja.domain;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renja_program_opd")
public record RenjaProgramOpd(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String tahun,
        String bulan,

        @Column("kode_program")
        String kodeProgram,

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

        @Column("bukti_pendukung")
        String buktiPendukung,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public RenjaProgramOpd withFaktorPenunjang(String faktorPenunjang) {
        return new RenjaProgramOpd(
                id, kodeOpd, tahun, bulan, kodeProgram, kodeIndikator, kodeTarget, kodePagu,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }

    public RenjaProgramOpd withFaktorPenghambat(String faktorPenghambat) {
        return new RenjaProgramOpd(
                id, kodeOpd, tahun, bulan, kodeProgram, kodeIndikator, kodeTarget, kodePagu,
                realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }
}
