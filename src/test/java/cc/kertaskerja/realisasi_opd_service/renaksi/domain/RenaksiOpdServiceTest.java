package cc.kertaskerja.realisasi_opd_service.renaksi.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenaksiOpdClient;
import cc.kertaskerja.integration.penetapan.renaksi.PenetapanRenaksiOpd;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RenaksiOpdServiceTest {
    @Mock
    private RenaksiOpdRepository renaksiOpdRepository;
    @Mock
    private PenetapanRenaksiOpdClient penetapanRenaksiOpdClient;

    @InjectMocks
    private RenaksiOpdService renaksiOpdService;

    private static final String KODE_OPD = "8.01.0.00.0.00.01.0000";
    private static final String KODE_RENAKSI = "REN-OPD-1";
    private static final String KODE_PK = "REKIN-PEG-2026-19928";

    private final PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot penetapanRoot =
            new PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot(
                    KODE_OPD,
                    2026,
                    3,
                    true,
                    List.of(new PenetapanRenaksiOpd.RenaksiPenetapanData(
                            KODE_RENAKSI,
                            "",
                            "SAS-OPD-5975",
                            KODE_PK,
                            "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten",
                            "198607022010011013",
                            "",
                            "",
                            168340222L,
                            2026,
                            25, 25, 35, 15
                    ))
            );

    private RenaksiOpd realisasi(String bulan, int nilai) {
        return new RenaksiOpd(
                1L, KODE_OPD, "2026", bulan, KODE_RENAKSI, BigDecimal.valueOf(nilai),
                JenisRealisasi.NAIK, "", "",
                null, Instant.now(), Instant.now(), null
        );
    }

    @Test
    void syncRealisasiFromRenaksiIndividu_WhenRowNotExists_ShouldInsertWithNaiK() {
        when(renaksiOpdRepository.findFirstByKodeOpdAndTahunAndBulanAndKodeRencanaAksiOpd(
                KODE_OPD, "2026", "3", KODE_RENAKSI))
                .thenReturn(Mono.empty());
        when(renaksiOpdRepository.save(any(RenaksiOpd.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(renaksiOpdService.syncRealisasiFromRenaksiIndividu(
                        KODE_OPD, "2026", "3", KODE_RENAKSI, BigDecimal.valueOf(4)))
                .assertNext(saved -> {
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_OPD, saved.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals("2026", saved.tahun());
                    org.junit.jupiter.api.Assertions.assertEquals("3", saved.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_RENAKSI, saved.kodeRencanaAksiOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(0, BigDecimal.valueOf(4).compareTo(saved.realisasi()));
                    org.junit.jupiter.api.Assertions.assertEquals(JenisRealisasi.NAIK, saved.jenisRealisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("", saved.faktorPenunjang());
                    org.junit.jupiter.api.Assertions.assertEquals("", saved.faktorPenghambat());
                })
                .verifyComplete();
    }

    @Test
    void syncRealisasiFromRenaksiIndividu_WhenRowExists_ShouldOnlyUpdateRealisasi() {
        Instant createdDate = Instant.parse("2026-01-05T00:00:00Z");
        RenaksiOpd existing = new RenaksiOpd(
                9L, KODE_OPD, "2026", "3", KODE_RENAKSI, BigDecimal.valueOf(4),
                JenisRealisasi.TURUN, "Dukungan anggaran", "Keterbatasan SDM",
                "pegawai", createdDate, createdDate, "pegawai"
        );
        when(renaksiOpdRepository.findFirstByKodeOpdAndTahunAndBulanAndKodeRencanaAksiOpd(
                KODE_OPD, "2026", "3", KODE_RENAKSI))
                .thenReturn(Mono.just(existing));
        when(renaksiOpdRepository.save(any(RenaksiOpd.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(renaksiOpdService.syncRealisasiFromRenaksiIndividu(
                        KODE_OPD, "2026", "3", KODE_RENAKSI, BigDecimal.valueOf(12)))
                .assertNext(saved -> {
                    org.junit.jupiter.api.Assertions.assertEquals(9L, saved.id());
                    org.junit.jupiter.api.Assertions.assertEquals(0, BigDecimal.valueOf(12).compareTo(saved.realisasi()));
                    // Faktor, jenis realisasi, dan audit lama tidak boleh berubah.
                    org.junit.jupiter.api.Assertions.assertEquals(JenisRealisasi.TURUN, saved.jenisRealisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("Dukungan anggaran", saved.faktorPenunjang());
                    org.junit.jupiter.api.Assertions.assertEquals("Keterbatasan SDM", saved.faktorPenghambat());
                    org.junit.jupiter.api.Assertions.assertEquals("pegawai", saved.createdBy());
                    org.junit.jupiter.api.Assertions.assertEquals(createdDate, saved.createdDate());
                })
                .verifyComplete();
    }

    @Test
    void syncPenetapanRenaksiOpd_ShouldDelegateToClient() {
        when(penetapanRenaksiOpdClient.syncRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just("{\"data\":{\"status\":\"SUCCESS\"}}"));

        StepVerifier.create(renaksiOpdService.syncPenetapanRenaksiOpd(KODE_OPD, 2026))
                .expectNext("{\"data\":{\"status\":\"SUCCESS\"}}")
                .verifyComplete();
    }

    @Test
    void syncPenetapanRenaksiOpd_WhenClientEmpty_ShouldCompleteEmpty() {
        // Error upstream ditelan client menjadi Mono.empty(), tidak melempar error.
        when(penetapanRenaksiOpdClient.syncRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.empty());

        StepVerifier.create(renaksiOpdService.syncPenetapanRenaksiOpd(KODE_OPD, 2026))
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WithoutBulan_ShouldStillReturnRealisasiObject() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, null))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_OPD, response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(2026, response.tahunAktif());
                    org.junit.jupiter.api.Assertions.assertNull(response.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(3, response.versi());
                    org.junit.jupiter.api.Assertions.assertEquals(true, response.isLocked());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.renaksiOpds().size());

                    var realisasi = response.renaksiOpds().getFirst().realisasi();
                    org.junit.jupiter.api.Assertions.assertNotNull(realisasi);
                    org.junit.jupiter.api.Assertions.assertEquals(2026, realisasi.tahun());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(100.0, realisasi.target());
                    org.junit.jupiter.api.Assertions.assertEquals(0.0, realisasi.realisasi());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.capaian());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.faktorPenunjang());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.faktorPenghambat());
                })
                .verifyComplete();

        verify(renaksiOpdRepository, never())
                .findAllByKodeOpdAndTahunAndBulan(anyString(), anyString(), anyString());
    }

    @Test
    void getPenetapanWithRealisasi_WithBulan_ShouldSumRealisasi() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(KODE_OPD, "2026", "1"))
                .thenReturn(Flux.just(
                        realisasi("1", 10),
                        realisasi("1", 20)
                ));

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "1"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_OPD, response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(2026, response.tahunAktif());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(3, response.versi());
                    org.junit.jupiter.api.Assertions.assertEquals(true, response.isLocked());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.renaksiOpds().size());

                    var item = response.renaksiOpds().getFirst();
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_RENAKSI, item.kodeRencanaAksiOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(
                            "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten", item.namaRenaksi());
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_PK, item.kodePk());
                    org.junit.jupiter.api.Assertions.assertEquals(168340222L, item.anggaranRenaksi());
                    org.junit.jupiter.api.Assertions.assertEquals(25, item.tw1());
                    org.junit.jupiter.api.Assertions.assertEquals(15, item.tw4());

                    var realisasi = item.realisasi();
                    org.junit.jupiter.api.Assertions.assertEquals(2026, realisasi.tahun());
                    org.junit.jupiter.api.Assertions.assertEquals(1, realisasi.bulan());
                    // target = tw1 + tw2 + tw3 + tw4 = 25 + 25 + 35 + 15
                    org.junit.jupiter.api.Assertions.assertEquals(100.0, realisasi.target());
                    org.junit.jupiter.api.Assertions.assertEquals(30.0, realisasi.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals(30.0, realisasi.capaian());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.keteranganCapaian());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WithBulan_ShouldReturnFaktorFromStoredRow() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(KODE_OPD, "2026", "10"))
                .thenReturn(Flux.just(new RenaksiOpd(
                        1L, KODE_OPD, "2026", "10", KODE_RENAKSI, BigDecimal.valueOf(5),
                        JenisRealisasi.NAIK, "Dukungan anggaran", "Keterbatasan SDM",
                        null, Instant.now(), Instant.now(), null
                )));

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "10"))
                .assertNext(response -> {
                    var realisasi = response.renaksiOpds().getFirst().realisasi();
                    org.junit.jupiter.api.Assertions.assertEquals(10, realisasi.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(5.0, realisasi.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("Dukungan anggaran", realisasi.faktorPenunjang());
                    org.junit.jupiter.api.Assertions.assertEquals("Keterbatasan SDM", realisasi.faktorPenghambat());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WhenNoRealisasi_ShouldReturnZeroWithTargetFromTriwulan() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(KODE_OPD, "2026", "1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "1"))
                .assertNext(response -> {
                    var realisasi = response.renaksiOpds().getFirst().realisasi();
                    org.junit.jupiter.api.Assertions.assertNotNull(realisasi);
                    org.junit.jupiter.api.Assertions.assertEquals(1, realisasi.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(100.0, realisasi.target());
                    org.junit.jupiter.api.Assertions.assertEquals(0.0, realisasi.realisasi());
                    org.junit.jupiter.api.Assertions.assertNull(realisasi.capaian());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WhenRealisasiExceedsTarget_ShouldCapCapaianAndSetKeterangan() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(KODE_OPD, "2026", "1"))
                .thenReturn(Flux.just(realisasi("1", 150)));

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "1"))
                .assertNext(response -> {
                    var realisasi = response.renaksiOpds().getFirst().realisasi();
                    org.junit.jupiter.api.Assertions.assertEquals(150.0, realisasi.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals(100.0, realisasi.capaian());
                    org.junit.jupiter.api.Assertions.assertNotNull(realisasi.keteranganCapaian());
                    org.junit.jupiter.api.Assertions.assertTrue(
                            realisasi.keteranganCapaian().contains("lebih dari 100%"));
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasi_Bulanan_ShouldSumSelectedMonthAndEnrichFromPenetapan() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahun(KODE_OPD, "2026"))
                .thenReturn(Flux.just(realisasi("1", 10), realisasi("2", 20)));

        StepVerifier.create(renaksiOpdService.getLaporanRealisasi(
                        KODE_OPD, "2026", JenisLaporan.BULANAN, "1"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("2026", response.tahun());
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_OPD, response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_RENAKSI, response.kodeRencanaAksiOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(
                            "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten", response.namaRenaksi());
                    org.junit.jupiter.api.Assertions.assertEquals("100.0", response.target());
                    org.junit.jupiter.api.Assertions.assertEquals(JenisLaporan.BULANAN, response.jenisLaporan());
                    org.junit.jupiter.api.Assertions.assertEquals(java.util.Map.of("1", 10.0), response.listData());
                    org.junit.jupiter.api.Assertions.assertNull(response.totalRealisasi());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasi_Triwulan_ShouldReturnCumulativeAndTotal() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahun(KODE_OPD, "2026"))
                .thenReturn(Flux.just(realisasi("1", 10), realisasi("2", 20), realisasi("4", 5)));

        StepVerifier.create(renaksiOpdService.getLaporanRealisasi(
                        KODE_OPD, "2026", JenisLaporan.TRIWULAN, null))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(
                            java.util.Map.of("1", 30.0, "2", 35.0), response.listData());
                    org.junit.jupiter.api.Assertions.assertEquals(35.0, response.totalRealisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("100.0", response.target());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasi_Tahunan_ShouldReturnCumulativePerMonthAndTotal() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(renaksiOpdRepository.findAllByKodeOpdAndTahun(KODE_OPD, "2026"))
                .thenReturn(Flux.just(realisasi("1", 10), realisasi("2", 20)));

        StepVerifier.create(renaksiOpdService.getLaporanRealisasi(
                        KODE_OPD, "2026", JenisLaporan.TAHUNAN, null))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(
                            java.util.Map.of("1", 10.0, "2", 30.0), response.listData());
                    org.junit.jupiter.api.Assertions.assertEquals(30.0, response.totalRealisasi());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasi_BulananWithoutBulan_ShouldReturnBadRequest() {
        StepVerifier.create(renaksiOpdService.getLaporanRealisasi(
                        KODE_OPD, "2026", JenisLaporan.BULANAN, null))
                .expectErrorSatisfies(error -> {
                    org.junit.jupiter.api.Assertions.assertTrue(
                            error instanceof org.springframework.web.server.ResponseStatusException);
                    var status = (org.springframework.web.server.ResponseStatusException) error;
                    org.junit.jupiter.api.Assertions.assertEquals(400, status.getStatusCode().value());
                })
                .verify();
    }

    @Test
    void getLaporanRealisasi_WhenPenetapanEmpty_ShouldStillReturnStoredRealisasi() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.empty());
        when(renaksiOpdRepository.findAllByKodeOpdAndTahun(KODE_OPD, "2026"))
                .thenReturn(Flux.just(realisasi("1", 10)));

        StepVerifier.create(renaksiOpdService.getLaporanRealisasi(
                        KODE_OPD, "2026", JenisLaporan.BULANAN, "1"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_RENAKSI, response.namaRenaksi());
                    org.junit.jupiter.api.Assertions.assertNull(response.target());
                    org.junit.jupiter.api.Assertions.assertEquals(java.util.Map.of("1", 10.0), response.listData());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WhenClientReturnsEmpty_ShouldReturnDefault() {
        when(penetapanRenaksiOpdClient.fetchRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.empty());

        StepVerifier.create(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, null))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(KODE_OPD, response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(2026, response.tahunAktif());
                    org.junit.jupiter.api.Assertions.assertNull(response.bulan());
                    org.junit.jupiter.api.Assertions.assertTrue(response.renaksiOpds().isEmpty());
                })
                .verifyComplete();
    }
}
