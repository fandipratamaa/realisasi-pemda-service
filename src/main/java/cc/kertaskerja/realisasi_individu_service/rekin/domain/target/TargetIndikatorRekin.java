package cc.kertaskerja.realisasi_individu_service.rekin.domain.target;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("target_indikator_rekin")
public record TargetIndikatorRekin(
        @Id Long id,

        @Column("indikator_rekin_id")
        Long indikatorRekinId,

        @Column("kode_target")
        String kodeTarget,

        @Column("kode_opd")
        String kodeOpd,
        String nip,
        String tahun,
        String bulan,
        BigDecimal target,
        BigDecimal realisasi,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,
        @Column("faktor_penghambat")
        String faktorPenghambat,

        @CreatedBy
        @Column("created_by")
        String createdBy,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy,
        @CreatedDate
        @Column("created_date")
        Instant createdDate,
        @LastModifiedDate
        @Column("last_modified_date")
        Instant lastModifiedDate
) {
    public static TargetIndikatorRekin of(
            Long indikatorRekinId,
            String kodeTarget,
            String kodeOpd,
            String nip,
            String tahun,
            String bulan,
            BigDecimal target,
            BigDecimal realisasi,
            JenisRealisasi jenisRealisasi,
            String faktorPenunjang,
            String faktorPenghambat
    ) {
        return new TargetIndikatorRekin(null, indikatorRekinId, kodeTarget, kodeOpd, nip, tahun, bulan,
                target, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat,
                null, null, null, null);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianTarget();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("satuan")
    public String satuan() {
        return "%";
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianTarget();
        return calculatedCapaian > 100
                ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")"
                : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianTarget() {
        if (realisasi == null || target == null) {
            return 0.0;
        }
        Capaian capaian = new Capaian(realisasi.doubleValue(), target.toString(), jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
