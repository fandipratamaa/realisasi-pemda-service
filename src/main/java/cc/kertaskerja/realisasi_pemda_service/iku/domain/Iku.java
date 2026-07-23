package cc.kertaskerja.realisasi_pemda_service.iku.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import com.fasterxml.jackson.annotation.JsonProperty;

// IKU Take realisasi tujuan and sasaran
public record Iku(
        String id,
        @JsonProperty("kode_indikator") String indikatorId,
        String indikator,
        @JsonProperty("kode_target") String targetId,
        String target,
        Double realisasi,
        String satuan,
        String capaian,
        String keteranganCapaian,
        String tahun,
        String faktorPenunjang,
        String faktorPenghambat,
        JenisRealisasi jenisRealisasi,
        JenisIku jenisIku,
        @JsonProperty("rumus_perhitungan") String rumusPerhitungan,
        @JsonProperty("sumber_data") String sumberData,
        String bulan
    ) {
    public static Iku of(String indikatorId, String indikator, String targetId, String target, Double realisasi,
            String satuan, String capaian, String tahun, String faktorPenunjang, String faktorPenghambat,
            JenisRealisasi jenisRealisasi, JenisIku jenisIku, String rumusPerhitungan, String sumberData,
            String bulan) {
        String calculatedCapaian = capaian;
        String ketCapaian = null;
        if ((calculatedCapaian == null || calculatedCapaian.isEmpty()) && realisasi != null && target != null
                && !target.isEmpty()) {
            try {
                Double targetVal = Double.parseDouble(target);
                if (targetVal != 0 && realisasi != 0) {
                    cc.kertaskerja.capaian.domain.Capaian capaianObj = new cc.kertaskerja.capaian.domain.Capaian(
                            realisasi, target, jenisRealisasi);
                    Double calculated = capaianObj.hasilCapaian();
                    if (calculated != null) {
                        if (calculated > 100) {
                            ketCapaian = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculated) + ")";
                            calculated = 100.0;
                        }
                        calculatedCapaian = String.valueOf(calculated);
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return new Iku(
                "IKU-" + jenisIku + "-" + indikatorId,
                indikatorId, indikator, targetId,
                target, realisasi, satuan, calculatedCapaian, ketCapaian, tahun,
                faktorPenunjang, faktorPenghambat,
                jenisRealisasi, jenisIku,
                rumusPerhitungan, sumberData, bulan);
    }
}
