package cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
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

@Table("renja_target_individu")
public record RenjaTargetIndividu(
        @Id Long id,

        @Column("kode_renja")
        String kodeRenja,
        @Column("jenis_renja")
        JenisRenja jenisRenja,

        @Column("nip")
        String nip,
        @Column("id_indikator")
        String idIndikator,
        String indikator,

        @Column("target_id")
        String targetId,
        String target,
        Integer realisasi,
        String satuan,
        String tahun,

        @Column("bulan")
        String bulan,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        RenjaTargetIndividuStatus status,

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
public static RenjaTargetIndividu of(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi,
            RenjaTargetIndividuStatus status
    ) {
        return new RenjaTargetIndividu(
                null,
                kodeRenja,
                jenisRenja,
                nip,
                idIndikator,
                indikator,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
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
        double calculatedCapaian = capaianRenjaTargetIndividu();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianRenjaTargetIndividu();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianRenjaTargetIndividu() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
