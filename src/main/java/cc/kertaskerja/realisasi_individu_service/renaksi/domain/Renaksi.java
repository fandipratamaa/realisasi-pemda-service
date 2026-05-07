package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

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

@Table("renaksi")
public record Renaksi(
        @Id Long id,

        @Column("renaksi_id")
        String renaksiId,
        String renaksi,

        @Column("nip")
        String nip,

        @Column("rekin_id")
        String rekinId,
        @Column("rekin")
        String rekin,

        @Column("target_id")
        String targetId,
        String target,
        Integer realisasi,
        String satuan,
        String bulan,
        String tahun,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,

        @Column("kode_opd")
        String kodeOpd,

        RenaksiStatus status,

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
    public static Renaksi of(
            String renaksiId,
            String renaksi,
            String nip,
            String rekin_id,
            String rekin,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String bulan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            String kodeOpd,
            RenaksiStatus status
    ) {
        return new Renaksi(
                null,
                renaksiId,
                renaksi,
                nip,
                rekin_id,
                rekin,
                targetId,
                target,
                realisasi,
                satuan,
                bulan,
                tahun,
                jenisRealisasi,
                kodeOpd,
                status,
                null,
                null,
                null,
                null,
                0
        );
    }

    @JsonProperty("capaian")
    public String capaian() {
        double calculatedCapaian = capaianRenaksi();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianRenaksi();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianRenaksi() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
