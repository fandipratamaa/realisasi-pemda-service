package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("sasarans")
public record Sasaran(
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
        SasaranStatus status,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @Version int version
) {
    public static Sasaran of(
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
            SasaranStatus status
    ) {
        return new Sasaran(null,
                sasaranId, sasaran, indikatorId, indikator,
                targetId, target,
                realisasi, satuan, tahun, bulan, jenisRealisasi, status,
                null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianSasaran());
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        return capaianSasaran() > 100 ? "nilai capaian lebih dari 100%" : null;
    }

    public Double capaianSasaran() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
