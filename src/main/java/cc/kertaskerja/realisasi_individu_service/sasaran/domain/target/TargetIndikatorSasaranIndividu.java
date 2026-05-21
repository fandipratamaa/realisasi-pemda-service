package cc.kertaskerja.realisasi_individu_service.sasaran.domain.target;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("target_indikator_sasaran_individu")
public record TargetIndikatorSasaranIndividu(
        @Id Long id,
        Long indikatorSasaranId,
        String kodeTarget,
        BigDecimal realisasi,
        String tahun,
        String bulan,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
}
