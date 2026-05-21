package cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_sasaran_individu")
public record IndikatorSasaranIndividu(
        @Id Long id,
        @Column("sasaran_individu_id")
        Long sasaranOpdId,
        String kodeIndikator,
        String kodeOpd,
        String tahun,
        String bulan,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
}
