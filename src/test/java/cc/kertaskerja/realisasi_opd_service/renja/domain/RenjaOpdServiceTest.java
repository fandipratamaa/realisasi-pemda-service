package cc.kertaskerja.realisasi_opd_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;

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

import static org.mockito.Mockito.when;

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
                    null
            ))
    );

    @Test
    void getPenetapanWithRealisasi_WithoutBulan_ShouldReturnDataWithoutRealisasi() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, null))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("5.01.5.05.0.00.01.0000", response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(2026, response.tahun());
                    org.junit.jupiter.api.Assertions.assertNull(response.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.programs().size());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.kegiatans().size());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.subkegiatans().size());
                    org.junit.jupiter.api.Assertions.assertNull(response.programs().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WithBulan_ShouldMergeRealisasi() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(penetapanRoot));
        when(targetProgramRepo.findAllByTahunAndBulan("2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaProgramOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02", "IND-001", "TGT-001", "", BigDecimal.valueOf(70), "", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));
        when(targetKegiatanRepo.findAllByTahunAndBulan("2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaKegiatanOpd(2L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02.2.01", "IND-002", "TGT-002", "", BigDecimal.valueOf(70), "", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));
        when(targetSubKegiatanRepo.findAllByTahunAndBulan("2026", "1"))
                .thenReturn(Flux.just(
                        new RenjaSubKegiatanOpd(3L, "5.01.5.05.0.00.01.0000", "2026", "1",
                                "5.01.02.2.01.0005", "IND-003", "TGT-003", "", BigDecimal.valueOf(70), "", "", "", "",
                                Instant.now(), Instant.now(), null, null)
                ));

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "1"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.bulan());
                    org.junit.jupiter.api.Assertions.assertEquals(70.0, response.programs().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals(70.0, response.programs().getFirst().indikators().getFirst().targets().getFirst().capaian());
                    org.junit.jupiter.api.Assertions.assertEquals(70.0, response.kegiatans().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals(70.0, response.subkegiatans().getFirst().indikators().getFirst().targets().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_WhenClientReturnsEmpty_ShouldReturnDefault() {
        when(penetapanClient.fetchRenjaOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.empty());

        StepVerifier.create(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "1"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("5.01.5.05.0.00.01.0000", response.kodeOpd());
                    org.junit.jupiter.api.Assertions.assertTrue(response.programs().isEmpty());
                    org.junit.jupiter.api.Assertions.assertTrue(response.kegiatans().isEmpty());
                    org.junit.jupiter.api.Assertions.assertTrue(response.subkegiatans().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void hitungCapaian_WhenTargetNull_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(70.0, null);
        org.junit.jupiter.api.Assertions.assertNull(result.capaian());
        org.junit.jupiter.api.Assertions.assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenRealisasiNull_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(null, 100.0);
        org.junit.jupiter.api.Assertions.assertNull(result.capaian());
        org.junit.jupiter.api.Assertions.assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenTargetZero_ShouldReturnNull() {
        var result = RenjaOpdService.hitungCapaian(70.0, 0.0);
        org.junit.jupiter.api.Assertions.assertNull(result.capaian());
        org.junit.jupiter.api.Assertions.assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenNormal_ShouldCalculateCorrectly() {
        var result = RenjaOpdService.hitungCapaian(70.0, 100.0);
        org.junit.jupiter.api.Assertions.assertEquals(70.0, result.capaian());
        org.junit.jupiter.api.Assertions.assertNull(result.keteranganCapaian());
    }

    @Test
    void hitungCapaian_WhenOver100_ShouldCapAndAddKeterangan() {
        var result = RenjaOpdService.hitungCapaian(150.0, 100.0);
        org.junit.jupiter.api.Assertions.assertEquals(100.0, result.capaian());
        org.junit.jupiter.api.Assertions.assertNotNull(result.keteranganCapaian());
        org.junit.jupiter.api.Assertions.assertTrue(result.keteranganCapaian().contains("lebih dari 100%"));
    }
}
