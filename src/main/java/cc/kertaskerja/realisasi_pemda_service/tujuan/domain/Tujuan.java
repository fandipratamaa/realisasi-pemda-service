package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("tujuans")
public record Tujuan(
        @Id Long id,

        String tujuanId,
        String tujuan,
        String indikatorId,
        String indikator,
        String targetId,
        String target,
        Double realisasi,
        String satuan,
        String tahun,
        String bulan,
        JenisRealisasi jenisRealisasi,
        TujuanStatus status,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,

        @Version int version
) {
    public static Tujuan of(
            String tujuanId,
            String tujuan,
            String indikatorId,
            String indikator,
            String targetId,
            String target,
            Double realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi,
            TujuanStatus status
    ) {
        return new Tujuan(null,
                tujuanId, tujuan, indikatorId, indikator,
                targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, status,
                null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianTujuan());
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        return capaianTujuan() > 100 ? "nilai capaian lebih dari 100%" : null;
    }

    public Double capaianTujuan() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
