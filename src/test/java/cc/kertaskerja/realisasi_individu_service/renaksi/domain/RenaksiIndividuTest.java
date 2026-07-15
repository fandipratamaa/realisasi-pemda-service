package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RenaksiIndividuTest {

    private RenaksiIndividu createRenaksiIndividu(
            BigDecimal target, BigDecimal paguAnggaran, BigDecimal realisasi,
            String tahun, String bulan, String satuan, JenisRealisasi jenisRealisasi
    ) {
        return RenaksiIndividu.of(
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "SASARAN-001",
                "Realisasi Sasaran SASARAN-001",
                "RENAKSI-001",
                "Realisasi Renaksi RENAKSI-001",
                "IND-001",
                "Realisasi Indikator IND-001",
                "TAR-1",
                target,
                paguAnggaran,
                realisasi,
                tahun,
                bulan,
                satuan,
                RenaksiStatus.UNCHECKED,
                jenisRealisasi,
                "",
                "",
                "",
                ""
        );
    }

    @Test
    void testCapaian_Normal_NaiK() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(75), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(75.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_FullRealisasi_NaiK() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(100), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(100.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_OverRealisasi_NaiK_CappedAt100() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(120), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(100.0, r.capaian(), 0.001);
    }

    @Test
    void testKeteranganCapaian_Over100() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(120), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertNotNull(r.keteranganCapaian());
        assertTrue(r.keteranganCapaian().contains("120.00%"));
    }

    @Test
    void testKeteranganCapaian_Under100_ShouldBeNull() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(75), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertNull(r.keteranganCapaian());
    }

    @Test
    void testCapaian_TargetIsZero_ShouldReturnZero() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.ZERO, BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(75), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_TargetIsNull_ShouldReturnZero() {
        RenaksiIndividu r = createRenaksiIndividu(
                null, BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(75), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_RealisasiIsZero_ShouldReturnZero() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.ZERO, "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_RealisasiIsNull_ShouldReturnZero() {
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                null, "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_NotUsingPaguAnggaran() {
        // Verify that paguAnggaran is NOT used in calculation
        // If paguAnggaran were used, capaian would be ~0.00015%
        // With target=100 and realisasi=50, capaian should be 50%
        RenaksiIndividu r = createRenaksiIndividu(
                BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(50), "2026", "1", "%",
                JenisRealisasi.NAIK
        );
        assertEquals(50.0, r.capaian(), 0.001);
    }
}
