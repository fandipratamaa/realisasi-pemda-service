package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("tujuan_opd")
public record TujuanOpd(
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
        String kodeOpd,
        String rumusPerhitungan,
        String sumberData,
        TujuanOpdStatus status,

        @CreatedBy
        @Column("created_by")
        String createdBy,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy,

        @Version int version
) {
    public static TujuanOpd of(
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
            String kodeOpd,
            String rumusPerhitungan,
            String sumberData,
            TujuanOpdStatus status
    ) {
        return new TujuanOpd(null,
                tujuanId, tujuan, indikatorId, indikator,
                targetId, target, realisasi, satuan, tahun, bulan,
                jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData, status,
                null, null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianTujuanOpd();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianTujuanOpd();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianTujuanOpd() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
