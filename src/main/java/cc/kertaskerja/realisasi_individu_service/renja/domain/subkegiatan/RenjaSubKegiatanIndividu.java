package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renja_subkegiatan_individu")
public record RenjaSubKegiatanIndividu(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String nip,

        String tahun,
        String bulan,

        @Column("kode_subkegiatan")
        String kodeSubKegiatan,

        String subkegiatan,

        @Column("kode_indikator")
        String kodeIndikator,

        String indikator,

        @Column("kode_target")
        String kodeTarget,

        @Column("kode_pagu")
        String kodePagu,

        BigDecimal pagu,

        @Column("target_realisasi")
        BigDecimal targetRealisasi,

        @Column("realisasi_target")
        BigDecimal realisasiTarget,

        @Column("realisasi_pagu")
        BigDecimal realisasiPagu,

        @Column("jenis_realisasi")
        String jenisRealisasi,

        @Column("faktor_penunjang")
        String faktorPenunjang,

        @Column("faktor_penghambat")
        String faktorPenghambat,

        @Column("bukti_pendukung")
        String buktiPendukung,

        @Column("keterangan_bukti_pendukung")
        String keteranganBuktiPendukung,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public RenjaSubKegiatanIndividu withFaktorPenunjang(String faktorPenunjang) {
        return new RenjaSubKegiatanIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeSubKegiatan, subkegiatan, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, targetRealisasi, realisasiTarget, realisasiPagu, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }

    public RenjaSubKegiatanIndividu withFaktorPenghambat(String faktorPenghambat) {
        return new RenjaSubKegiatanIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeSubKegiatan, subkegiatan, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, targetRealisasi, realisasiTarget, realisasiPagu, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }
}
