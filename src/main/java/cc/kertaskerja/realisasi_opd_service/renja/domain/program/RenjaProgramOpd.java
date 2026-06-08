package cc.kertaskerja.realisasi_opd_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("target_renja_program_opd")
public record RenjaProgramOpd(
        @Id Long id,

        @Column("indikator_renja_program_opd_id")
        Long indikatorRenjaProgramOpdId,

        @Column("kode_target")
        String kodeTarget,

        String tahun,
        String bulan,
        BigDecimal realisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,

        @Column("faktor_penghambat")
        String faktorPenghambat,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
