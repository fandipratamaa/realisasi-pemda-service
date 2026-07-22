package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RenaksiIndividuTest {

    @Test
    void testCapaian_Normal_NaiK() {
        var r = RenaksiIndividu.hitungCapaian(75.0, 100.0);
        assertEquals(75.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_FullRealisasi_NaiK() {
        var r = RenaksiIndividu.hitungCapaian(100.0, 100.0);
        assertEquals(100.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_OverRealisasi_NaiK_CappedAt100() {
        var r = RenaksiIndividu.hitungCapaian(120.0, 100.0);
        assertEquals(100.0, r.capaian(), 0.001);
    }

    @Test
    void testKeteranganCapaian_Over100() {
        var r = RenaksiIndividu.hitungCapaian(120.0, 100.0);
        assertNotNull(r.keteranganCapaian());
        assertTrue(r.keteranganCapaian().contains("120.00%"));
    }

    @Test
    void testKeteranganCapaian_Under100_ShouldBeNull() {
        var r = RenaksiIndividu.hitungCapaian(75.0, 100.0);
        assertNull(r.keteranganCapaian());
    }

    @Test
    void testCapaian_TargetIsZero_ShouldReturnZero() {
        var r = RenaksiIndividu.hitungCapaian(75.0, 0.0);
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_TargetIsNull_ShouldReturnZero() {
        var r = RenaksiIndividu.hitungCapaian(75.0, null);
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_RealisasiIsZero_ShouldReturnZero() {
        var r = RenaksiIndividu.hitungCapaian(0.0, 100.0);
        assertEquals(0.0, r.capaian(), 0.001);
    }

    @Test
    void testCapaian_RealisasiIsNull_ShouldReturnZero() {
        var r = RenaksiIndividu.hitungCapaian(null, 100.0);
        assertEquals(0.0, r.capaian(), 0.001);
    }
}
