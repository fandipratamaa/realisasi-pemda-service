package cc.kertaskerja.realisasi_pemda_service.iku.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;

// IKU Take realisasi tujuan and sasaran
public record Iku(
        String id,
        String indikatorId,
        String indikator,
        String targetId,
        String target,
        Double realisasi,
        String satuan,
        String capaian,
        String tahun,
        String faktorPenunjang,
        String faktorPenghambat,
        JenisRealisasi jenisRealisasi,
        JenisIku jenisIku,
        String rumusPerhitungan,
        String sumberData,
        String bulan
) {
    public static Iku of(String indikatorId, String indikator, String targetId, String target, Double realisasi, String satuan, String capaian, String tahun, String faktorPenunjang, String faktorPenghambat, JenisRealisasi jenisRealisasi, JenisIku jenisIku, String rumusPerhitungan, String sumberData, String bulan) {
        return new Iku(
                "IKU-" + jenisIku + "-" + indikatorId,
                indikatorId, indikator, targetId,
                target, realisasi, satuan, capaian, tahun,
                faktorPenunjang, faktorPenghambat,
                jenisRealisasi, jenisIku,
                rumusPerhitungan, sumberData, bulan);
    }
}
