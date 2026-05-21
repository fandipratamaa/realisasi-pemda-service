package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdResponse;
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
class SasaranOpdServiceTest {
    @Mock
    private SasaranOpdRepository sasaranOpdRepository;
    @Mock
    private IndikatorSasaranOpdRepository indikatorSasaranOpdRepository;
    @Mock
    private TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;
    @Mock
    private PenetapanSasaranOpdClient penetapanSasaranOpdClient;

    @InjectMocks
    private SasaranOpdService sasaranOpdService;

    @Test
    void getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan_ShouldReturnMappedResponses() {
        SasaranOpd sasaran = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "SAS-OPD-001",
                "2026", "3", "tester", Instant.now(), Instant.now(), "tester");
        IndikatorSasaranOpd indikator = new IndikatorSasaranOpd(2L, 1L, "IND-01", "5.01.5.05.0.00.01.0000",
                "2026", "3", Instant.now(), Instant.now(), "tester", null);
        TargetIndikatorSasaranOpd target = new TargetIndikatorSasaranOpd(3L, 2L, "TGT-001", BigDecimal.valueOf(75),
                "2026", "3", Instant.now(), Instant.now(), "tester", null);

        when(sasaranOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .thenReturn(Flux.just(sasaran));
        when(indikatorSasaranOpdRepository.findAll()).thenReturn(Flux.just(indikator));
        when(targetIndikatorSasaranOpdRepository.findAll()).thenReturn(Flux.just(target));
        when(penetapanSasaranOpdClient.fetchSasaranOpd("5.01.5.05.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, response.id());
                    org.junit.jupiter.api.Assertions.assertEquals(75.0, response.indikators().getFirst().targets().getFirst().realisasi());
                })
                .verifyComplete();
    }

    @Test
    void getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan_ShouldReturnEmpty_WhenNoData() {
        when(sasaranOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "1.01.0.00.0.00.01.0000", "1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan("2026", "1.01.0.00.0.00.01.0000", "1"))
                .verifyComplete();
    }

    @Test
    void getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan_ShouldHideOrphanData() {
        SasaranOpd sasaran = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "SAS-ORPHAN",
                "2026", "3", "tester", Instant.now(), Instant.now(), "tester");
        IndikatorSasaranOpd indikator = new IndikatorSasaranOpd(2L, 1L, "IND-ORPHAN", "5.01.5.05.0.00.01.0000",
                "2026", "3", Instant.now(), Instant.now(), "tester", null);
        TargetIndikatorSasaranOpd target = new TargetIndikatorSasaranOpd(3L, 2L, "TGT-ORPHAN", BigDecimal.valueOf(75),
                "2026", "3", Instant.now(), Instant.now(), "tester", null);

        when(sasaranOpdRepository.findAllByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .thenReturn(Flux.just(sasaran));
        when(indikatorSasaranOpdRepository.findAll()).thenReturn(Flux.just(indikator));
        when(targetIndikatorSasaranOpdRepository.findAll()).thenReturn(Flux.just(target));
        when(penetapanSasaranOpdClient.fetchSasaranOpd("5.01.5.05.0.00.01.0000", 2026)).thenReturn(Mono.just(List.of()));

        StepVerifier.create(sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan("2026", "5.01.5.05.0.00.01.0000", "3"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals("SAS-ORPHAN", response.kodeSasaranOpd());
                    org.junit.jupiter.api.Assertions.assertTrue(response.indikators().getFirst().targets().stream()
                            .anyMatch(t -> "TGT-ORPHAN".equals(t.kodeTarget())));
                })
                .verifyComplete();
    }
}
