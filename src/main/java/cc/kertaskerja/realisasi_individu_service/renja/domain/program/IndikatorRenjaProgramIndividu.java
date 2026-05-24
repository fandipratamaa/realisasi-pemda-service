package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_renja_program_individu")
public record IndikatorRenjaProgramIndividu(
        @Id Long id,

        @Column("renja_program_individu_id")
        Long renjaProgramIndividuId,

        @Column("kode_indikator")
        String kodeIndikator,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
