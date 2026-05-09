package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
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
        @Column("visi_misi")
        String visiMisi,
        @Column("rumus_perhitungan")
        String rumusPerhitungan,
        @Column("sumber_data")
        String sumberData,
        JenisRealisasi jenisRealisasi,
        TujuanStatus status,

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
            String visiMisi,
            String rumusPerhitungan,
            String sumberData,
            JenisRealisasi jenisRealisasi,
            TujuanStatus status
    ) {
        return new Tujuan(null,
                tujuanId, tujuan, indikatorId, indikator,
                targetId, target, realisasi, satuan, tahun, bulan, visiMisi, rumusPerhitungan, sumberData, jenisRealisasi, status,
                null, null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianTujuan();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianTujuan();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianTujuan() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
