package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("sasaran_opd")
public record SasaranOpd(
        @Id Long id,

        String sasaranId,
        String sasaran,
        String indikatorId,
        String indikator,
        String targetId,
        String target,
        Double realisasi,
        String satuan,
        String tahun,
        String bulan,
        JenisRealisasi jenisRealisasi,
        String kodeOpd,
        SasaranOpdStatus status,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,

        @Version int version
) {
    public static SasaranOpd of(
            String sasaranId,
            String sasaran,
            String indikatorId,
            String indikator,
            String targetId,
            String target,
            Double realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi,
            String kodeOpd,
            SasaranOpdStatus status
    ) {
        return new SasaranOpd(null,
                sasaranId, sasaran, indikatorId, indikator,
                targetId, target, realisasi, satuan, tahun,
                bulan, jenisRealisasi, kodeOpd, status,
                null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianSasaranOpd());
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        return capaianSasaranOpd() > 100 ? "nilai capaian lebih dari 100%" : null;
    }

    public Double capaianSasaranOpd() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
