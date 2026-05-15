package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RenjaPaguIndividuTests {
    @Test
    void testCapaianRealisasiDibagiPagu() {
        RenjaPaguIndividu renjaPaguIndividu = RenjaPaguIndividu.of(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK,
                RenjaPaguIndividuStatus.UNCHECKED
        );

        Assertions.assertEquals("50.00%", renjaPaguIndividu.capaian());
    }

    @Test
    void testCapaianRealisasiDibagiPaguDenganDesimal() {
        RenjaPaguIndividu renjaPaguIndividu = RenjaPaguIndividu.of(
                "1.02.02",
                JenisRenja.KEGIATAN,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-2",
                "Indikator B",
                200,
                75,
                "rupiah",
                "2025",
                "Februari",
                JenisRealisasi.NAIK,
                RenjaPaguIndividuStatus.UNCHECKED
        );

        Assertions.assertEquals("37.50%", renjaPaguIndividu.capaian());
    }

    @Test
    void testCapaianPaguNol() {
        RenjaPaguIndividu renjaPaguIndividu = RenjaPaguIndividu.of(
                "1.02.03",
                JenisRenja.SUBKEGIATAN,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-3",
                "Indikator C",
                0,
                75,
                "rupiah",
                "2025",
                "Maret",
                JenisRealisasi.NAIK,
                RenjaPaguIndividuStatus.UNCHECKED
        );

        Assertions.assertEquals("0.00%", renjaPaguIndividu.capaian());
    }

    @Test
    void testCapaianRealisasiLebihBesarDariPagu() {
        RenjaPaguIndividu renjaPaguIndividu = RenjaPaguIndividu.of(
                "1.02.04",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-4",
                "Indikator D",
                100,
                150,
                "rupiah",
                "2025",
                "April",
                JenisRealisasi.NAIK,
                RenjaPaguIndividuStatus.UNCHECKED
        );

        Assertions.assertEquals("100.00%", renjaPaguIndividu.capaian());
        Assertions.assertEquals("nilai capaian lebih dari 100% (150.00%)", renjaPaguIndividu.keteranganCapaian());
    }
}
