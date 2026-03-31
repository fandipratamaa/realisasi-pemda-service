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
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_target_individu")
public record RenjaTargetIndividu(
        @Id Long id,

        @Column("renja_id")
        String renjaId,
        String renja,
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

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        RenjaTargetIndividuStatus status,

        @Column("keterangan_capaian")
        String keteranganCapaian,

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
    public static RenjaTargetIndividu of(
            String renjaId,
            String renja,
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
            JenisRealisasi jenisRealisasi,
            RenjaTargetIndividuStatus status,
            String keteranganCapaian
    ) {
        return new RenjaTargetIndividu(
                null,
                renjaId,
                renja,
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
                jenisRealisasi,
                status,
                keteranganCapaian,
                null,
                null,
                null,
                null
        );
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianRenjaTargetIndividu());
    }

    public Double capaianRenjaTargetIndividu() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
