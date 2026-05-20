package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.integration.penetapan.PenetapanTujuanOpdClient;
import cc.kertaskerja.integration.penetapan.tujuan_opd.PenetapanTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.indikator.IndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.target.TargetIndikatorTujuanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.tujuan.web.TujuanOpdResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TujuanOpdServiceTest {
    @Mock
    private TujuanOpdRepository tujuanOpdRepository;
    @Mock
    private IndikatorTujuanOpdRepository indikatorTujuanOpdRepository;
    @Mock
    private TargetIndikatorTujuanOpdRepository targetIndikatorTujuanOpdRepository;
    @Mock
    private PenetapanTujuanOpdClient penetapanTujuanOpdClient;

    @InjectMocks
    private TujuanOpdService tujuanOpdService;

    @Test
    void getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan_ShouldReturnMappedResponses() {
        TujuanOpd tujuan = new TujuanOpd(1L, "5.01.5.05.0.00.01.0000", "TUJ-OPD-001",
                "2026", "3", "tester", Instant.now(), Instant.now(), "tester");
        IndikatorTujuanOpd indikator = new IndikatorTujuanOpd(2L, 1L, "IND-01", "5.01.5.05.0.00.01.0000",
                "2026", "3", Instant.now(), Instant.now(), "tester", null);
        TargetIndikatorTujuanOpd target = new TargetIndikatorTujuanOpd(3L, 2L, "TGT-001", BigDecimal.valueOf(75),
                "2026", "3", Instant.now(), Instant.now(), "tester", null);

        when(tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .thenReturn(Flux.just(tujuan));
        when(indikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(indikator));
        when(targetIndikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(target));
        when(penetapanTujuanOpdClient.fetchTujuanOpd("5.01.5.05.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(tujuanOpdService.getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, response.id());
                    org.junit.jupiter.api.Assertions.assertEquals(75.0, response.indikator().getFirst().target().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void submitRealisasiTujuanOpd_ShouldCreateNewAndReturnResponse() {
        TujuanOpdRequest request = new TujuanOpdRequest(
                "KODE-TUJ-OPD-001", "KODE-IND-TUJ-OPD-001",
                "KODE-TAR-TUJ-OPD-001", 75.5, "2026", "1",
                "1.01.0.00.0.00.01.0000"
        );
        TujuanOpd saved = new TujuanOpd(1L, "1.01.0.00.0.00.01.0000", "KODE-TUJ-OPD-001",
                "2026", "1", null, Instant.now(), Instant.now(), null);
        IndikatorTujuanOpd savedIndikator = new IndikatorTujuanOpd(2L, 1L, "KODE-IND-TUJ-OPD-001",
                "1.01.0.00.0.00.01.0000", "2026", "1",
                Instant.now(), Instant.now(), null, null);
        TargetIndikatorTujuanOpd savedTarget = new TargetIndikatorTujuanOpd(3L, 2L, "KODE-TAR-TUJ-OPD-001",
                BigDecimal.valueOf(76), "2026", "1", Instant.now(), Instant.now(), null, null);

        when(tujuanOpdRepository.findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                "1.01.0.00.0.00.01.0000", "KODE-TUJ-OPD-001", "2026", "1"))
                .thenReturn(Mono.empty());
        when(tujuanOpdRepository.save(any(TujuanOpd.class))).thenReturn(Mono.just(saved));
        when(indikatorTujuanOpdRepository.findFirstByTujuanOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                1L, "KODE-IND-TUJ-OPD-001", "1.01.0.00.0.00.01.0000", "2026", "1"))
                .thenReturn(Mono.empty());
        when(indikatorTujuanOpdRepository.save(any(IndikatorTujuanOpd.class))).thenReturn(Mono.just(savedIndikator));
        when(targetIndikatorTujuanOpdRepository.findFirstByIndikatorTujuanIdAndKodeTargetAndTahunAndBulan(
                2L, "KODE-TAR-TUJ-OPD-001", "2026", "1"))
                .thenReturn(Mono.empty());
        when(targetIndikatorTujuanOpdRepository.save(any(TargetIndikatorTujuanOpd.class))).thenReturn(Mono.just(savedTarget));
        when(indikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(savedIndikator));
        when(targetIndikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(savedTarget));
        when(penetapanTujuanOpdClient.fetchTujuanOpd("1.01.0.00.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(tujuanOpdService.submitRealisasiTujuanOpd(request))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, response.id());
                    org.junit.jupiter.api.Assertions.assertEquals("KODE-TUJ-OPD-001", response.kodeTujuanOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.indikator().size());
                    org.junit.jupiter.api.Assertions.assertEquals(76.0, response.indikator().getFirst().target().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void submitRealisasiTujuanOpd_ShouldUpdateExistingTarget() {
        TujuanOpdRequest request = new TujuanOpdRequest(
                "KODE-TUJ-OPD-001", "KODE-IND-TUJ-OPD-001",
                "KODE-TAR-TUJ-OPD-001", 90.0, "2026", "1",
                "1.01.0.00.0.00.01.0000"
        );
        TujuanOpd existingTujuan = new TujuanOpd(1L, "1.01.0.00.0.00.01.0000", "KODE-TUJ-OPD-001",
                "2026", "1", "admin", Instant.now(), Instant.now(), "admin");
        IndikatorTujuanOpd existingIndikator = new IndikatorTujuanOpd(2L, 1L, "KODE-IND-TUJ-OPD-001",
                "1.01.0.00.0.00.01.0000", "2026", "1", Instant.now(), Instant.now(), "admin", null);
        TargetIndikatorTujuanOpd existingTarget = new TargetIndikatorTujuanOpd(3L, 2L, "KODE-TAR-TUJ-OPD-001",
                BigDecimal.valueOf(50), "2026", "1", Instant.now(), Instant.now(), "admin", null);
        TargetIndikatorTujuanOpd updatedTarget = new TargetIndikatorTujuanOpd(3L, 2L, "KODE-TAR-TUJ-OPD-001",
                BigDecimal.valueOf(90), "2026", "1", Instant.now(), null, "admin", null);

        when(tujuanOpdRepository.findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                "1.01.0.00.0.00.01.0000", "KODE-TUJ-OPD-001", "2026", "1"))
                .thenReturn(Mono.just(existingTujuan));
        when(indikatorTujuanOpdRepository.findFirstByTujuanOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                1L, "KODE-IND-TUJ-OPD-001", "1.01.0.00.0.00.01.0000", "2026", "1"))
                .thenReturn(Mono.just(existingIndikator));
        when(targetIndikatorTujuanOpdRepository.findFirstByIndikatorTujuanIdAndKodeTargetAndTahunAndBulan(
                2L, "KODE-TAR-TUJ-OPD-001", "2026", "1"))
                .thenReturn(Mono.just(existingTarget));
        when(targetIndikatorTujuanOpdRepository.save(any(TargetIndikatorTujuanOpd.class))).thenReturn(Mono.just(updatedTarget));
        when(indikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(existingIndikator));
        when(targetIndikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(updatedTarget));
        when(penetapanTujuanOpdClient.fetchTujuanOpd("1.01.0.00.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(tujuanOpdService.submitRealisasiTujuanOpd(request))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(90.0, response.indikator().getFirst().target().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void batchSubmitRealisasiTujuanOpd_ShouldSubmitAll() {
        TujuanOpdRequest req1 = new TujuanOpdRequest(
                "KODE-1", "KODE-IND-1",
                "KODE-TAR-1", 25.0, "2026", "1",
                "OPD-001"
        );
        TujuanOpdRequest req2 = new TujuanOpdRequest(
                "KODE-2", "KODE-IND-2",
                "KODE-TAR-2", 50.0, "2026", "1",
                "OPD-001"
        );

        TujuanOpd saved1 = new TujuanOpd(1L, "OPD-001", "KODE-1",
                "2026", "1", null, Instant.now(), Instant.now(), null);
        TujuanOpd saved2 = new TujuanOpd(2L, "OPD-001", "KODE-2",
                "2026", "1", null, Instant.now(), Instant.now(), null);

        when(tujuanOpdRepository.findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                "OPD-001", "KODE-1", "2026", "1")).thenReturn(Mono.empty());
        when(tujuanOpdRepository.findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
                "OPD-001", "KODE-2", "2026", "1")).thenReturn(Mono.empty());

        when(tujuanOpdRepository.save(any(TujuanOpd.class)))
                .thenReturn(Mono.just(saved1))
                .thenReturn(Mono.just(saved2));

        when(indikatorTujuanOpdRepository.findFirstByTujuanOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
                any(), any(), any(), any(), any())).thenReturn(Mono.empty());
        IndikatorTujuanOpd ind1 = new IndikatorTujuanOpd(3L, 1L, "KODE-IND-1", "OPD-001",
                "2026", "1", null, null, null, null);
        IndikatorTujuanOpd ind2 = new IndikatorTujuanOpd(4L, 2L, "KODE-IND-2", "OPD-001",
                "2026", "1", null, null, null, null);
        when(indikatorTujuanOpdRepository.save(any(IndikatorTujuanOpd.class)))
                .thenReturn(Mono.just(ind1))
                .thenReturn(Mono.just(ind2));

        when(targetIndikatorTujuanOpdRepository.findFirstByIndikatorTujuanIdAndKodeTargetAndTahunAndBulan(
                any(), any(), any(), any())).thenReturn(Mono.empty());
        TargetIndikatorTujuanOpd tgt1 = new TargetIndikatorTujuanOpd(5L, 3L, "KODE-TAR-1",
                BigDecimal.valueOf(25), "2026", "1", null, null, null, null);
        TargetIndikatorTujuanOpd tgt2 = new TargetIndikatorTujuanOpd(6L, 4L, "KODE-TAR-2",
                BigDecimal.valueOf(50), "2026", "1", null, null, null, null);
        when(targetIndikatorTujuanOpdRepository.save(any(TargetIndikatorTujuanOpd.class)))
                .thenReturn(Mono.just(tgt1))
                .thenReturn(Mono.just(tgt2));

        when(indikatorTujuanOpdRepository.findAll())
                .thenReturn(Flux.just(ind1))
                .thenReturn(Flux.just(ind2));
        when(targetIndikatorTujuanOpdRepository.findAll())
                .thenReturn(Flux.just(tgt1))
                .thenReturn(Flux.just(tgt2));
        when(penetapanTujuanOpdClient.fetchTujuanOpd("OPD-001", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(tujuanOpdService.batchSubmitRealisasiTujuanOpd(List.of(req1, req2)))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan_ShouldReturnEmpty_WhenNoData() {
        when(tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "1.01.0.00.0.00.01.0000", "1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(tujuanOpdService.getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan("2026", "1.01.0.00.0.00.01.0000", "1"))
                .verifyComplete();
    }

    @Test
    void getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan_ShouldHideOrphanData() {
        TujuanOpd tujuan = new TujuanOpd(1L, "5.01.5.05.0.00.01.0000", "TUJ-ORPHAN",
                "2026", "3", "tester", Instant.now(), Instant.now(), "tester");
        IndikatorTujuanOpd indikator = new IndikatorTujuanOpd(2L, 1L, "IND-ORPHAN", "5.01.5.05.0.00.01.0000",
                "2026", "3", Instant.now(), Instant.now(), "tester", null);
        TargetIndikatorTujuanOpd target = new TargetIndikatorTujuanOpd(3L, 2L, "TGT-ORPHAN", BigDecimal.valueOf(75),
                "2026", "3", Instant.now(), Instant.now(), "tester", null);

        when(tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .thenReturn(Flux.just(tujuan));
        when(indikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(indikator));
        when(targetIndikatorTujuanOpdRepository.findAll()).thenReturn(Flux.just(target));
        when(penetapanTujuanOpdClient.fetchTujuanOpd("5.01.5.05.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(tujuanOpdService.getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("TUJ-ORPHAN", response.kodeTujuanOpd());
                    org.junit.jupiter.api.Assertions.assertTrue(response.indikator().getFirst().target().stream()
                            .anyMatch(t -> "TGT-ORPHAN".equals(t.kodeTarget())));
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_ShouldHideTargetsFromPreviousMonthsButKeepCurrentMonthTargetsVisible() {
        String kodeOpd = "5.01.5.05.0.00.01.0000";
        TujuanOpd januaryTujuan = new TujuanOpd(1L, kodeOpd, "TUJ-1",
                "2026", "1", "tester", Instant.now(), Instant.now(), "tester");
        TujuanOpd februaryTujuan = new TujuanOpd(2L, kodeOpd, "TUJ-2",
                "2026", "2", "tester", Instant.now(), Instant.now(), "tester");

        IndikatorTujuanOpd januaryIndikator = new IndikatorTujuanOpd(10L, 1L, "IND-1", kodeOpd,
                "2026", "1", Instant.now(), Instant.now(), "tester", null);
        IndikatorTujuanOpd februaryIndikator = new IndikatorTujuanOpd(20L, 2L, "IND-2", kodeOpd,
                "2026", "2", Instant.now(), Instant.now(), "tester", null);

        TargetIndikatorTujuanOpd januaryTarget = new TargetIndikatorTujuanOpd(100L, 10L, "TGT-HIDDEN", BigDecimal.ZERO,
                "2026", "1", Instant.now(), Instant.now(), "tester", null);
        TargetIndikatorTujuanOpd februaryTarget = new TargetIndikatorTujuanOpd(200L, 20L, "TGT-VISIBLE", BigDecimal.valueOf(55),
                "2026", "2", Instant.now(), Instant.now(), "tester", null);

        PenetapanTujuanOpd.TujuanPenetapanData hiddenTujuan = new PenetapanTujuanOpd.TujuanPenetapanData(
                11L,
                kodeOpd,
                "TUJ-1",
                "Tujuan Tersembunyi",
                "2026-2031",
                2026,
                1,
                List.of(new PenetapanTujuanOpd.IndikatorPenetapanData(
                        12L,
                        11L,
                        "IND-1",
                        "Indikator Tersembunyi",
                        "Rumus 1",
                        "Sumber 1",
                        "Definisi 1",
                        2026,
                        List.of(new PenetapanTujuanOpd.TargetPenetapanData(
                                13L,
                                12L,
                                "TGT-HIDDEN",
                                "persen",
                                2026,
                                100.0
                        ))
                ))
        );

        PenetapanTujuanOpd.TujuanPenetapanData visibleTujuan = new PenetapanTujuanOpd.TujuanPenetapanData(
                21L,
                kodeOpd,
                "TUJ-2",
                "Tujuan Februari",
                "2026-2031",
                2026,
                1,
                List.of(new PenetapanTujuanOpd.IndikatorPenetapanData(
                        22L,
                        21L,
                        "IND-2",
                        "Indikator Februari",
                        "Rumus 2",
                        "Sumber 2",
                        "Definisi 2",
                        2026,
                        List.of(new PenetapanTujuanOpd.TargetPenetapanData(
                                23L,
                                22L,
                                "TGT-VISIBLE",
                                "persen",
                                2026,
                                200.0
                        ))
                ))
        );

        when(penetapanTujuanOpdClient.fetchTujuanOpd(kodeOpd, 2026))
                .thenReturn(Mono.just(List.of(hiddenTujuan, visibleTujuan)));
        when(tujuanOpdRepository.findAllByTahunAndKodeOpd("2026", kodeOpd))
                .thenReturn(Flux.just(januaryTujuan, februaryTujuan));
        when(tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", kodeOpd, "2"))
                .thenReturn(Flux.just(februaryTujuan));
        when(indikatorTujuanOpdRepository.findAll())
                .thenReturn(Flux.just(januaryIndikator, februaryIndikator));
        when(targetIndikatorTujuanOpdRepository.findAll())
                .thenReturn(Flux.just(januaryTarget, februaryTarget));

        StepVerifier.create(tujuanOpdService.getPenetapanWithRealisasi(kodeOpd, 2026, "2"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("TUJ-2", response.kodeTujuanOpd());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.indikator().size());
                    org.junit.jupiter.api.Assertions.assertEquals(1, response.indikator().getFirst().target().size());
                    org.junit.jupiter.api.Assertions.assertEquals("TGT-VISIBLE", response.indikator().getFirst().target().getFirst().kodeTarget());
                    org.junit.jupiter.api.Assertions.assertEquals(55.0, response.indikator().getFirst().target().getFirst().realisasi());
                })
                .verifyComplete();
    }
}
