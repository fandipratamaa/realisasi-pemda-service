package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;


import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class TujuanTests {
    @Test
    void testCapaianSingleTarget() {
        Tujuan t = new Tujuan(304L, "T1", "TUJ-1",
                "I1", "IND-1",
                "TAR-1", "10.0", 10.0, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Assertions.assertEquals("100.00%", t.capaian());
    }

    @Test
    void testCapaianSingleTargetFormatTargetNonStandard() {
        Tujuan t = new Tujuan(304L, "T1", "TUJ-1",
                "I1", "IND-1",
               "TAR-1", "10", 10.0, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Tujuan t2 = new Tujuan(305L, "T2", "TUJ-2",
                "I1", "IND-1",
               "TAR-2", "10,01", 10.01, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Assertions.assertEquals("100.00%", t.capaian());
        Assertions.assertEquals("100.00%", t2.capaian());
    }

    @Test
    void testCapaianMultiTarget() {
        Tujuan t = new Tujuan(304L, "T1", "TUJ-1",
                "I1", "IND-1",
                "TAR-1", "10,0 - 15,0", 15.0, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Tujuan t2 = new Tujuan(334L, "T3", "TUJ-3",
                "I1", "IND-1",
                "TAR-2", "10,0 - 15,0", 8.0, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Tujuan t3 = new Tujuan(333L, "T4", "TUJ-4",
                "I1", "IND-1",
                "TAR-3", "10,0 - 15,0", 13.5, "%",
                "2025", "01", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED,
                Instant.now(), Instant.now(), 1);

        Assertions.assertEquals("100.00%", t.capaian());
        Assertions.assertEquals("53.33%", t2.capaian());
        Assertions.assertEquals("90.00%", t3.capaian());
    }
}
