package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table("renja_pagu_individu")
public record RenjaPaguIndividu(
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

        Integer pagu,
        Integer realisasi,
        String satuan,
        String tahun,

        @Column("bulan")
        String bulan,

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        RenjaPaguIndividuStatus status,

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
    public static RenjaPaguIndividu of(
            String kodeRenja,
            JenisRenja jenisRenja,
            String nip,
            String idIndikator,
            String indikator,
            Integer pagu,
            Integer realisasi,
            String satuan,
            String tahun,
            String bulan,
            JenisRealisasi jenisRealisasi,
            RenjaPaguIndividuStatus status
    ) {
        return new RenjaPaguIndividu(
                null,
                kodeRenja,
                jenisRenja,
                nip,
                idIndikator,
                indikator,
                pagu,
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
        double calculatedCapaian = capaianRenjaPaguIndividu();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        double calculatedCapaian = capaianRenjaPaguIndividu();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianRenjaPaguIndividu() {
        if (pagu == null || pagu == 0 || realisasi == null) {
            return 0.0;
        }

        double paguValue = pagu.doubleValue();
        double realisasiValue = realisasi.doubleValue();
        return (realisasiValue / paguValue) * 100;
    }
}
