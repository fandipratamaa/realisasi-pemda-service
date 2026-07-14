package cc.kertaskerja.realisasi_individu_service.renja.web.program;

public record RenjaIndividuProgramResponse(
        Long id,
        String kodeOpd,
        String tahun,
        String bulan,
        String nip,
        String kodeProgram,
        String program,
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
