package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("realisasi_target_tujuan_pemda")
public record Tujuan(
        @Id Long id,

        @Column("kode_tujuan_pemda")
        String kodeTujuanPemda,
        
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
        @Column("jenis_realisasi")
        JenisRealisasi jenisRealisasi,
        TujuanStatus status,
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
    public static Tujuan of(
            String kodeTujuanPemda,
            String kodeIndikator,
            String kodeTarget,
            Double realisasi,
            String satuan,
            String tahun,
            String bulan,
            String faktorPenunjang,
            String faktorPenghambat,
            JenisRealisasi jenisRealisasi,
            TujuanStatus status,
            String buktiPendukung,
            String keteranganBuktiPendukung
    ) {
        return new Tujuan(null,
                kodeTujuanPemda, kodeIndikator, kodeTarget,
                realisasi, satuan, tahun, bulan, faktorPenunjang, faktorPenghambat, jenisRealisasi, status, buktiPendukung, keteranganBuktiPendukung,
                null, null, null, null);
    }
}
