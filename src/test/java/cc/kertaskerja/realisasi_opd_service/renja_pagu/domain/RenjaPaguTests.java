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
                "Program A",
                JenisRenja.PROGRAM,
                100,
                50,
                "rupiah",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("50.00%", renjaPagu.capaian());
    }

    @Test
    void testCapaianRealisasiDibagiPaguDenganDesimal() {
        RenjaPagu renjaPagu = new RenjaPagu(
                2L,
                "RENJA-2",
                "Program B",
                JenisRenja.KEGIATAN,
                200,
                75,
                "rupiah",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("37.50%", renjaPagu.capaian());
    }

    @Test
    void testCapaianPaguNol() {
        RenjaPagu renjaPagu = new RenjaPagu(
                3L,
                "RENJA-3",
                "Program C",
                JenisRenja.SUBKEGIATAN,
                0,
                75,
                "rupiah",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("0.00%", renjaPagu.capaian());
    }

    @Test
    void testCapaianRealisasiLebihBesarDariPagu() {
        RenjaPagu renjaPagu = new RenjaPagu(
                4L,
                "RENJA-4",
                "Program D",
                JenisRenja.PROGRAM,
                100,
                150,
                "rupiah",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaPaguStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("150.00%", renjaPagu.capaian());
    }
}
