package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_renja_kegiatan_individu")
public record IndikatorRenjaKegiatanIndividu(
        @Id Long id,

        @Column("renja_kegiatan_individu_id")
        Long renjaKegiatanIndividuId,

        @Column("kode_indikator")
        String kodeIndikator,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
