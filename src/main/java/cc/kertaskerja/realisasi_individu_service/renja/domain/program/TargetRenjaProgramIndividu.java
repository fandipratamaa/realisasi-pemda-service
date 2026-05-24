package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("target_renja_program_individu")
public record TargetRenjaProgramIndividu(
        @Id Long id,

        @Column("indikator_renja_program_individu_id")
        Long indikatorRenjaProgramIndividuId,

        @Column("kode_target")
        String kodeTarget,

        String tahun,
        String bulan,
        BigDecimal realisasi,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
