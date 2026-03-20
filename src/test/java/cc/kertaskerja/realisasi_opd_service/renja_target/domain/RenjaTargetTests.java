package cc.kertaskerja.realisasi_opd_service.renja_target.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class RenjaTargetTests {
    @Test
    void testCapaianRealisasiDibagiTarget() {
        RenjaTarget renjaTarget = new RenjaTarget(
                1L,
                "RENJA-1",
                "Program A",
                JenisRenja.PROGRAM,
                "IND-1",
                "Indikator A",
                "T-1",
                "100",
                50,
                "unit",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaTargetStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("50.00%", renjaTarget.capaian());
    }

    @Test
    void testCapaianRealisasiDibagiTargetDenganDesimal() {
        RenjaTarget renjaTarget = new RenjaTarget(
                2L,
                "RENJA-2",
                "Program B",
                JenisRenja.KEGIATAN,
                "IND-2",
                "Indikator B",
                "T-2",
                "200",
                75,
                "unit",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaTargetStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("37.50%", renjaTarget.capaian());
    }

    @Test
    void testCapaianTargetNol() {
        RenjaTarget renjaTarget = new RenjaTarget(
                3L,
                "RENJA-3",
                "Program C",
                JenisRenja.SUBKEGIATAN,
                "IND-3",
                "Indikator C",
                "T-3",
                "0",
                75,
                "unit",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaTargetStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("0.00%", renjaTarget.capaian());
    }

    @Test
    void testCapaianRealisasiLebihBesarDariTarget() {
        RenjaTarget renjaTarget = new RenjaTarget(
                4L,
                "RENJA-4",
                "Program D",
                JenisRenja.PROGRAM,
                "IND-4",
                "Indikator D",
                "T-4",
                "100",
                150,
                "unit",
                "2025",
                JenisRealisasi.NAIK,
                "001",
                RenjaTargetStatus.UNCHECKED,
                "system",
                Instant.now(),
                Instant.now(),
                1
        );

        Assertions.assertEquals("150.00%", renjaTarget.capaian());
    }
}
