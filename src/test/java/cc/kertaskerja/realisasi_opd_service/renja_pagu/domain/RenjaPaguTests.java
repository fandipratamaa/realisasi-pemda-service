package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class RenjaPaguTests {
    @Test
    void testCapaianRealisasiDibagiPagu() {
        RenjaPagu renjaPagu = new RenjaPagu(
                1L,
                "RENJA-1",
                JenisRenja.PROGRAM,
                100,
                50,
                "rupiah",
                "2025",
                "01",
                JenisRealisasi.NAIK,
                "001",
                "001",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("50.00%", renjaPagu.capaian());
    }

    @Test
    void testCapaianRealisasiDibagiPaguDenganDesimal() {
        RenjaPagu renjaPagu = new RenjaPagu(
                2L,
                "RENJA-2",
                JenisRenja.KEGIATAN,
                200,
                75,
                "rupiah",
                "2025",
                "02",
                JenisRealisasi.NAIK,
                "001",
                "002",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("37.50%", renjaPagu.capaian());
    }

    @Test
    void testCapaianPaguNol() {
        RenjaPagu renjaPagu = new RenjaPagu(
                3L,
                "RENJA-3",
                JenisRenja.SUBKEGIATAN,
                0,
                75,
                "rupiah",
                "2025",
                "03",
                JenisRealisasi.NAIK,
                "001",
                "003",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("0.00%", renjaPagu.capaian());
    }

    @Test
    void testCapaianRealisasiLebihBesarDariPagu() {
        RenjaPagu renjaPagu = new RenjaPagu(
                4L,
                "RENJA-4",
                JenisRenja.PROGRAM,
                100,
                150,
                "rupiah",
                "2025",
                "04",
                JenisRealisasi.NAIK,
                "001",
                "004",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("100.00%", renjaPagu.capaian());
        Assertions.assertEquals("nilai capaian lebih dari 100% (150.00%)", renjaPagu.keteranganCapaian());
    }

    @Test
    void testCapaianRealisasiEqualPagu() {
        RenjaPagu renjaPagu = new RenjaPagu(
                5L,
                "RENJA-5",
                JenisRenja.KEGIATAN,
                100,
                100,
                "rupiah",
                "2025",
                "05",
                JenisRealisasi.NAIK,
                "001",
                "005",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("100.00%", renjaPagu.capaian());
    }

    @Test
    void testCapaianRealisasiNolDanPaguNol() {
        RenjaPagu renjaPagu = new RenjaPagu(
                6L,
                "RENJA-6",
                JenisRenja.SUBKEGIATAN,
                0,
                0,
                "rupiah",
                "2025",
                "06",
                JenisRealisasi.NAIK,
                "001",
                "006",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                "system",
                1
        );

        Assertions.assertEquals("0.00%", renjaPagu.capaian());
    }
}
