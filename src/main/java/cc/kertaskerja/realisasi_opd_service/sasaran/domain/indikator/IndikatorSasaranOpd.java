package cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("indikator_sasaran_opd")
public record IndikatorSasaranOpd(
        @Id Long id,
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
    public static IndikatorSasaranOpd of(
            Long sasaranOpdId,
            String kodeIndikator,
            String kodeOpd,
            String tahun,
            String bulan
    ) {
        return new IndikatorSasaranOpd(
                null, sasaranOpdId, kodeIndikator, kodeOpd, tahun, bulan,
                null, null, null, null);
    }
}
