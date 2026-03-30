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

        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        RekinStatus status,

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
        Instant lastModifiedDate,

        @Version int version
) {
    public static Rekin of(
            String rekinId,
            String rekin,
            String nip,
            String idSasaran,
            String sasaran,
            String targetId,
            String target,
            Integer realisasi,
            String satuan,
            String tahun,
            JenisRealisasi jenisRealisasi,
            RekinStatus status,
            String keteranganCapaian
    ) {
        return new Rekin(null,
                rekinId,
                rekin,
                nip,
                idSasaran,
                sasaran,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                jenisRealisasi,
                status,
                keteranganCapaian,
                null, null, null, null, 0);
    }

    @JsonProperty("capaian")
    public String capaian() {
        return String.format("%.2f%%", capaianRekin());
    }

    public Double capaianRekin() {
        if (realisasi == null) {
            return 0.0;
        }

        Capaian capaian = new Capaian(realisasi.doubleValue(), target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
