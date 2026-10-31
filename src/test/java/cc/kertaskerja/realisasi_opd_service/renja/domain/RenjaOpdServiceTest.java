package cc.kertaskerja.realisasi_opd_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import cc.kertaskerja.realisasi.domain.JenisLaporan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenjaOpdServiceTest {
    @Mock
    private PenetapanRenjaOpdClient penetapanClient;
    @Mock
    private RenjaProgramOpdRepository targetProgramRepo;
    @Mock
    private RenjaKegiatanOpdRepository targetKegiatanRepo;
    @Mock
    private RenjaSubKegiatanOpdRepository targetSubKegiatanRepo;

    @InjectMocks
    private RenjaOpdService renjaOpdService;

    private final PenetapanRenjaOpd.PenetapanRenjaOpdRoot penetapanRoot = new PenetapanRenjaOpd.PenetapanRenjaOpdRoot(
            "5.01.5.05.0.00.01.0000",
            2026,
            1,
            List.of(new PenetapanRenjaOpd.ProgramPenetapanData(
                    1L, "5.01.02", "PROGRAM PERENCANAAN", true,
                    List.of(new PenetapanRenjaOpd.IndikatorPenetapanData(
                            1L, "IND-001", "test2026",
                            List.of(new PenetapanRenjaOpd.TargetPenetapanData(
                                    1L, "TGT-001", 2026, 100.0, "%"
                            ))
                    )),
                    null
            )),
            List.of(new PenetapanRenjaOpd.KegiatanPenetapanData(
                    2L, "5.01.02.2.01", "Penyusunan Perencanaan", false,
                    List.of(new PenetapanRenjaOpd.IndikatorPenetapanData(
                            2L, "IND-002", "INDI X",
                            List.of(new PenetapanRenjaOpd.TargetPenetapanData(
                                    2L, "TGT-002", 2026, 100.0, "%"
                            ))
                    )),
                    null
            )),
            List.of(new PenetapanRenjaOpd.SubkegiatanPenetapanData(
                    3L, "5.01.02.2.01.0005", "Musrenbang Kab/Kota", true,
                    List.of(new PenetapanRenjaOpd.IndikatorPenetapanData(
                            3L, "IND-003", "INDI X",
                            List.of(new PenetapanRenjaOpd.TargetPenetapanData(
                                    3L, "TGT-003", 2026, 100.0, "%"
                            ))
                    )),
                    List.of(new PenetapanRenjaOpd.PaguAnggaranData(
                            1L, "PAGU-3", 10000000.0, "PENDAPATAN"
                    ))
            ))
    );

    @Test
    void getPenetapanWithRealisasi_WithoutBulan_ShouldReturnDataWithoutRealisasi() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, null))
                .assertNext(response -> {
                    assertEquals("5.01.5.05.0.00.01.0000", response.kodeOpd());
                    assertEquals(2026, response.tahun());
                    assertNull(response.bulan());
                    assertEquals(1, response.programs().size());
                    assertEquals(1, response.kegiatans().size());
                    assertEquals(1, response.subkegiatans().size());
                    assertNull(response.programs().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WithBulan_ShouldMergeRealisasi() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetProgramRepo.findAllByKodeOpdAndTahunAndBulan("5.01.5.05.0.00.01.0000", "2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaProgramOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02", "IND-001", "TGT-001", "", BigDecimal.valueOf(70), "NAIK", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));
        when(targetKegiatanRepo.findAllByKodeOpdAndTahunAndBulan("5.01.5.05.0.00.01.0000", "2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaKegiatanOpd(2L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02.2.01", "IND-002", "TGT-002", "", BigDecimal.valueOf(70), "NAIK", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));
        when(targetSubKegiatanRepo.findAllByKodeOpdAndTahunAndBulan("5.01.5.05.0.00.01.0000", "2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaSubKegiatanOpd(3L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02.2.01.0005", "IND-003", "TGT-003", "PAGU-3", BigDecimal.valueOf(70),
                                BigDecimal.valueOf(5000000.0), "NAIK", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "1"))
                .assertNext(response -> {
                    assertEquals(1, response.bulan());
                    assertEquals(70.0, response.programs().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                    assertEquals(70.0, response.programs().getFirst().indikators().getFirst().targets().getFirst().capaian());
                    assertEquals(70.0, response.kegiatans().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                    assertEquals(70.0, response.subkegiatans().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                    
                    var pagu = response.subkegiatans().getFirst().paguAnggaran().getFirst();
                    assertEquals(5000000.0, pagu.realisasi());
                    assertEquals(50.0, pagu.capaian());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WhenClientReturnsEmpty_ShouldReturnDefault() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.empty());

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "1"))
                .assertNext(response -> {
                    assertEquals("5.01.5.05.0.00.01.0000", response.kodeOpd());
                    assertTrue(response.programs().isEmpty());
                    assertTrue(response.kegiatans().isEmpty());
                    assertTrue(response.subkegiatans().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void syncRealisasiProgram_WhenNew_ShouldInsert() {
        when(targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "5.01.02", "IND-001", "TGT-001", "2026", "1"))
                .thenReturn(Mono.empty());
        when(targetProgramRepo.save(any(RenjaProgramOpd.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(renjaOpdService.syncRealisasiProgram(
                "5.01.5.05.0.00.01.0000", "2026", "1",
                "5.01.02", "IND-001", "TGT-001", "PAGU-1",
                BigDecimal.valueOf(85.0)))
                .assertNext(res -> {
                    assertEquals("5.01.02", res.kodeProgram());
                    assertEquals(BigDecimal.valueOf(85.0), res.realisasi());
                })
                .verifyComplete();
    }

    @Test
    void syncRealisasiProgram_WhenExisting_ShouldUpdateRealisasiAndPreserveFactors() {
        RenjaProgramOpd existing = new RenjaProgramOpd(
                1L, "5.01.5.05.0.00.01.0000", "2026", "1",
                "5.01.02", "IND-001", "TGT-001", "PAGU-1",
                BigDecimal.valueOf(40.0), "NAIK", "Faktor A", "Faktor B", "bukti.pdf",
                Instant.now(), Instant.now(), "admin", "admin"
        );
        when(targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "5.01.02", "IND-001", "TGT-001", "2026", "1"))
                .thenReturn(Mono.just(existing));
        when(targetProgramRepo.save(any(RenjaProgramOpd.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(renjaOpdService.syncRealisasiProgram(
                "5.01.5.05.0.00.01.0000", "2026", "1",
                "5.01.02", "IND-001", "TGT-001", "PAGU-1",
                BigDecimal.valueOf(90.0)))
                .assertNext(res -> {
                    assertEquals(BigDecimal.valueOf(90.0), res.realisasi());
                    assertEquals("Faktor A", res.faktorPenunjang());
                    assertEquals("Faktor B", res.faktorPenghambat());
                    assertEquals("bukti.pdf", res.buktiPendukung());
                })
                .verifyComplete();
    }

    @Test
    void syncRealisasiSubKegiatan_WhenNew_ShouldInsertTargetAndPagu() {
        when(targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndKodePaguAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "5.01.02.2.01.0005", "IND-003", "TGT-003", "PAGU-3", "2026", "1"))
                .thenReturn(Mono.empty());
        when(targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "5.01.02.2.01.0005", "IND-003", "TGT-003", "2026", "1"))
                .thenReturn(Mono.empty());
        when(targetSubKegiatanRepo.save(any(RenjaSubKegiatanOpd.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(renjaOpdService.syncRealisasiSubKegiatan(
                "5.01.5.05.0.00.01.0000", "2026", "1",
                "5.01.02.2.01.0005", "IND-003", "TGT-003", "PAGU-3",
                BigDecimal.valueOf(75.0), BigDecimal.valueOf(7500000.0)))
                .assertNext(res -> {
                    assertEquals("5.01.02.2.01.0005", res.kodeSubkegiatan());
                    assertEquals(BigDecimal.valueOf(75.0), res.realisasi());
                    assertEquals(BigDecimal.valueOf(7500000.0), res.realisasiPagu());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasiProgram_Bulanan_ShouldAggregatePerMonthAndResolveNames() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetProgramRepo.findAllByKodeOpdAndTahun("5.01.5.05.0.00.01.0000", "2026"))
                .thenReturn(Flux.just(
                        programRecord("1", "IND-001", "TGT-001", 30.0),
                        programRecord("2", "IND-001", "TGT-001", 40.0)
                ));

        StepVerifier.create(renjaOpdService.getLaporanRealisasiProgram(
                        "5.01.5.05.0.00.01.0000", "2026", JenisLaporan.BULANAN, "1"))
                .assertNext(res -> {
                    assertEquals("PROGRAM PERENCANAAN", res.program());
                    assertEquals("test2026", res.indikator());
                    assertEquals("100.0", res.target());
                    assertEquals(30.0, res.listData().get("1"));
                    assertNull(res.totalRealisasi());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasiProgram_Tahunan_ShouldReturnKumulatif() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetProgramRepo.findAllByKodeOpdAndTahun("5.01.5.05.0.00.01.0000", "2026"))
                .thenReturn(Flux.just(
                        programRecord("1", "IND-001", "TGT-001", 30.0),
                        programRecord("2", "IND-001", "TGT-001", 40.0)
                ));

        StepVerifier.create(renjaOpdService.getLaporanRealisasiProgram(
                        "5.01.5.05.0.00.01.0000", "2026", JenisLaporan.TAHUNAN, null))
                .assertNext(res -> {
                    assertEquals(30.0, res.listData().get("1"));
                    assertEquals(40.0, res.listData().get("2"));
                    assertEquals(70.0, res.totalRealisasi());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasiKegiatan_Bulanan_ShouldResolveKegiatanName() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetKegiatanRepo.findAllByKodeOpdAndTahun("5.01.5.05.0.00.01.0000", "2026"))
                .thenReturn(Flux.just(kegiatanRecord("1", "IND-002", "TGT-002", 25.0)));

        StepVerifier.create(renjaOpdService.getLaporanRealisasiKegiatan(
                        "5.01.5.05.0.00.01.0000", "2026", JenisLaporan.BULANAN, "1"))
                .assertNext(res -> {
                    assertEquals("Penyusunan Perencanaan", res.kegiatan());
                    assertEquals("INDI X", res.indikator());
                    assertEquals("100.0", res.target());
                    assertEquals(25.0, res.listData().get("1"));
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasiSubKegiatan_ShouldUseRealisasiTargetNotPagu() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetSubKegiatanRepo.findAllByKodeOpdAndTahun("5.01.5.05.0.00.01.0000", "2026"))
                .thenReturn(Flux.just(
                        new RenjaSubKegiatanOpd(3L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02.2.01.0005", "IND-003", "TGT-003", "PAGU-3",
                                BigDecimal.valueOf(70), BigDecimal.valueOf(5_000_000), "NAIK", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));

        StepVerifier.create(renjaOpdService.getLaporanRealisasiSubKegiatan(
                        "5.01.5.05.0.00.01.0000", "2026", JenisLaporan.TAHUNAN, null))
                .assertNext(res -> {
                    assertEquals("Musrenbang Kab/Kota", res.subKegiatan());
                    assertEquals("INDI X", res.indikator());
                    assertEquals(70.0, res.listData().get("1"));
                    assertEquals(70.0, res.totalRealisasi());
                })
                .verifyComplete();
    }

    @Test
    void getLaporanRealisasi_BulananWithoutBulan_ShouldError() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetProgramRepo.findAllByKodeOpdAndTahun("5.01.5.05.0.00.01.0000", "2026"))
                .thenReturn(Flux.just(programRecord("1", "IND-001", "TGT-001", 30.0)));

        StepVerifier.create(renjaOpdService.getLaporanRealisasiProgram(
                        "5.01.5.05.0.00.01.0000", "2026", JenisLaporan.BULANAN, null))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    private RenjaProgramOpd programRecord(String bulan, String kodeIndikator, String kodeTarget, double realisasi) {
        return new RenjaProgramOpd(1L, "5.01.5.05.0.00.01.0000", "2026", bulan,
                "5.01.02", kodeIndikator, kodeTarget, "", BigDecimal.valueOf(realisasi), "NAIK", "", "", "",
                Instant.now(), Instant.now(), null, null);
    }

    private RenjaKegiatanOpd kegiatanRecord(String bulan, String kodeIndikator, String kodeTarget, double realisasi) {
        return new RenjaKegiatanOpd(2L, "5.01.5.05.0.00.01.0000", "2026", bulan,
                "5.01.02.2.01", kodeIndikator, kodeTarget, "", BigDecimal.valueOf(realisasi), "NAIK", "", "", "",
                Instant.now(), Instant.now(), null, null);
    }

    @Test
    void hitungCapaian_WhenTargetNull_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(70.0, null);
        assertNull(result.capaian());
        assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenRealisasiNull_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(null, 100.0);
        assertNull(result.capaian());
        assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenTargetZero_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(70.0, 0.0);
        assertNull(result.capaian());
        assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenNormal_ShouldCalculateCorrectly() {
        var result = RenjaOpdService.hitungCapaian(70.0, 100.0);
        assertEquals(70.0, result.capaian());
        assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenOver100_ShouldCapAndAddKeterangan() {
        var result = RenjaOpdService.hitungCapaian(150.0, 100.0);
        assertEquals(100.0, result.capaian());
        assertNotNull(result.keteranganCapaian());
        assertTrue(result.keteranganCapaian().contains("lebih dari 100%"));
    }
}
