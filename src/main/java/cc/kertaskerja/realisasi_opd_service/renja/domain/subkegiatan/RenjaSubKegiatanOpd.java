package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("target_renja_subkegiatan_opd")
public record RenjaSubKegiatanOpd(
        @Id Long id,

        @Column("indikator_renja_subkegiatan_opd_id")
        Long indikatorRenjaSubKegiatanOpdId,

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
