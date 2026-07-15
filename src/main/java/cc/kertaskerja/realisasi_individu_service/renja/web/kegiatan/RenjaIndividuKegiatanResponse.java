package cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan;

public record RenjaIndividuKegiatanResponse(
        Long id,
        String kodeOpd,
        String tahun,
        String bulan,
        String nip,
        String kodeKegiatan,
        String kegiatan,
        String kodeIndikator,
        String indikator,
        String kodeTarget,
        String kodePagu,
        Double pagu,
        Double target,
        Double realisasi,
        String jenisRealisasi,
        Double capaian,
        String keteranganCapaian,
        String faktorPenunjang,
        String faktorPenghambat,
        String buktiPendukung,
        String createdBy,
        String lastModifiedBy
) {}
