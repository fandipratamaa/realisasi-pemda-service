package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("rekin")
public record Rekin(
        @Id Long id,

        @Column("rekin_id")
        String rekinId,
        String rekin,

        @Column("indikator_id")
        String indikatorId,
        @Column("indikator")
        String indikator,

        @Column("nip")
        String nip,
        @Column("id_sasaran")
        String idSasaran,
        @Column("sasaran")
        String sasaran,

        @Column("target_id")
        String targetId,
        String target,
        Integer realisasi,
        String satuan,
        String tahun,
        String bulan,

        @Column("kode_opd")
        String kodeOpd,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        RekinStatus status,

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
        Instant lastModifiedDate,

        @Version int version
) {
    public static Rekin of(
            String rekinId,
            String rekin,
            String indikatorId,
            String indikator,
            String nip,
            String idSasaran,
            String sasaran,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            String kodeOpd,
            JenisRealisasi jenisRealisasi,
            RekinStatus status
    ) {
        return new Rekin(null,
                rekinId,
                rekin,
                indikatorId,
                indikator,
                nip,
                idSasaran,
                sasaran,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                kodeOpd,
                jenisRealisasi,
                status,
                null, null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianRekin();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianRekin();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianRekin() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
