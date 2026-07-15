package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("realisasi_target_renja_kegiatan_individu")
public record RenjaKegiatanIndividu(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        String nip,

        String tahun,
        String bulan,

        @Column("kode_kegiatan")
        String kodeKegiatan,

        String kegiatan,

        @Column("kode_indikator")
        String kodeIndikator,

        String indikator,

        @Column("kode_target")
        String kodeTarget,

        @Column("kode_pagu")
        String kodePagu,

        BigDecimal pagu,

        BigDecimal target,

        BigDecimal realisasi,

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
    public RenjaKegiatanIndividu withFaktorPenunjang(String faktorPenunjang) {
        return new RenjaKegiatanIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeKegiatan, kegiatan, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, target, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }

    public RenjaKegiatanIndividu withFaktorPenghambat(String faktorPenghambat) {
        return new RenjaKegiatanIndividu(
                id, kodeOpd, nip, tahun, bulan, kodeKegiatan, kegiatan, kodeIndikator, indikator, kodeTarget, kodePagu,
                pagu, target, realisasi, jenisRealisasi, faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung,
                createdDate, lastModifiedDate, createdBy, lastModifiedBy
        );
    }
}
