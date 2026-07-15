package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("realisasi_target_sasaran_pemda")
public record Sasaran(
        @Id Long id,

        String sasaranId,
        String sasaran,
        String indikatorId,
        String indikator,
        String targetId,
        String target,
        Double realisasi,
        String satuan,
        String tahun,
        String bulan,
        @Column("rumus_perhitungan")
        String rumusPerhitungan,
        @Column("sumber_data")
        String sumberData,
        @Column("faktor_penunjang")
        String faktorPenunjang,
        @Column("faktor_penghambat")
        String faktorPenghambat,
        JenisRealisasi jenisRealisasi,
        SasaranStatus status,
        @Column("bukti_pendukung")
        String buktiPendukung,
        @Column("keterangan_bukti_pendukung")
        String keteranganBuktiPendukung,


        @CreatedBy
        @Column("created_by")
        String createdBy,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @LastModifiedBy
        @Column("last_modified_by")
        String lastModifiedBy
) {
    public static Sasaran of(
            String sasaranId,
            String sasaran,
            String indikatorId,
            String indikator,
            String targetId,
            String target,
            Double realisasi,
            String satuan,
            String tahun,
            String bulan,
            String rumusPerhitungan,
            String sumberData,
            String faktorPenunjang,
            String faktorPenghambat,
            JenisRealisasi jenisRealisasi,
            SasaranStatus status,
            String buktiPendukung,
            String keteranganBuktiPendukung
    ) {
        return new Sasaran(null,
                sasaranId, sasaran, indikatorId, indikator,
                targetId, target,
                realisasi, satuan, tahun, bulan, rumusPerhitungan, sumberData, faktorPenunjang, faktorPenghambat, jenisRealisasi, status, buktiPendukung, keteranganBuktiPendukung,
                null, null, null, null);
    }

    @JsonProperty("capaian")
    public String capaian() {
        if (realisasi == null || target == null || target.equals("0") || realisasi == 0) {
            return null;
        }
        double calculatedCapaian = capaianSasaran();
        return formatCapaian(Math.min(calculatedCapaian, 100));
    }

    @JsonProperty("keteranganCapaian")
    public String keteranganCapaian() {
        if (realisasi == null || target == null || target.equals("0") || realisasi == 0) {
            return null;
        }
        double calculatedCapaian = capaianSasaran();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + formatCapaian(calculatedCapaian) + ")" : null;
    }

    private String formatCapaian(double value) {
        return String.format("%.2f%%", value);
    }

    public Double capaianSasaran() {
        Capaian capaian = new Capaian(realisasi, target, jenisRealisasi);
        return capaian.hasilCapaian();
    }
}
