package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.capaian.domain.Capaian;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
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

        @Column("kode_sasaran_pemda")
        String kodeSasaranPemda,
        
        @Column("kode_indikator")
        String kodeIndikator,
        
        @Column("kode_target")
        String kodeTarget,
        
        Double realisasi,
        String satuan,
        String tahun,
        String bulan,
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
            String kodeSasaranPemda,
            String kodeIndikator,
            String kodeTarget,
            Double realisasi,
            String satuan,
            String tahun,
            String bulan,
            String faktorPenunjang,
            String faktorPenghambat,
            JenisRealisasi jenisRealisasi,
            SasaranStatus status,
            String buktiPendukung,
            String keteranganBuktiPendukung
    ) {
        return new Sasaran(null,
                kodeSasaranPemda, kodeIndikator, kodeTarget,
                realisasi, satuan, tahun, bulan, faktorPenunjang, faktorPenghambat, jenisRealisasi, status, buktiPendukung, keteranganBuktiPendukung,
                null, null, null, null);
    }

    public Double hitungCapaian(Double targetPenetapan) {
        if (realisasi == null || targetPenetapan == null || targetPenetapan == 0 || realisasi == 0) {
            return null;
        }
        Capaian capaianObj = new Capaian(realisasi, String.valueOf(targetPenetapan), jenisRealisasi);
        Double calculatedCapaian = capaianObj.hasilCapaian();
        return calculatedCapaian > 100 ? 100.0 : calculatedCapaian;
    }

    public String keteranganCapaian(Double targetPenetapan) {
        if (realisasi == null || targetPenetapan == null || targetPenetapan == 0 || realisasi == 0) {
            return null;
        }
        Capaian capaianObj = new Capaian(realisasi, String.valueOf(targetPenetapan), jenisRealisasi);
        Double calculatedCapaian = capaianObj.hasilCapaian();
        return calculatedCapaian > 100 ? "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculatedCapaian) + ")" : null;
    }
}
