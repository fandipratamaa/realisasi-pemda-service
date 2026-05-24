package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_renja_subkegiatan_opd")
public record IndikatorRenjaSubKegiatanOpd(
        @Id Long id,

        @Column("renja_subkegiatan_opd_id")
        Long renjaSubKegiatanOpdId,

        @Column("kode_indikator")
        String kodeIndikator,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
