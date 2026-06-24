package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renja_program_individu")
public record RenjaProgramIndividu(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String nip,

        String tahun,
        String bulan,

        @Column("kode_program")
        String kodeProgram,

        String program,

        @Column("kode_indikator")
        String kodeIndikator,

        String indikator,

        @Column("kode_target")
        String kodeTarget,

        @Column("kode_pagu")
        String kodePagu,

        BigDecimal pagu,

        BigDecimal target,

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
    public RenjaProgramIndividu withFaktorPenunjang(String faktorPenunjang) {
        return new RenjaProgramIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeProgram, program, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, target, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }

    public RenjaProgramIndividu withFaktorPenghambat(String faktorPenghambat) {
        return new RenjaProgramIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeProgram, program, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, target, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }
}
