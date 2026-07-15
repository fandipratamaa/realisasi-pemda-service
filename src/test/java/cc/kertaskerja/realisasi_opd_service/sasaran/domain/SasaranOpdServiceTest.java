package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.sasaran.web.SasaranOpdRequest;
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
class SasaranOpdServiceTest {
    @Mock
    private SasaranOpdRepository sasaranOpdRepository;
    @Mock
    private PenetapanSasaranOpdClient penetapanSasaranOpdClient;

    @InjectMocks
    private SasaranOpdService sasaranOpdService;

    @Test
    void getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan_ShouldReturnMappedResponses() {
        SasaranOpd entity = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "3",
                "SAS-OPD-001", "IND-01", "TGT-001", BigDecimal.valueOf(75),
                JenisRealisasi.NAIK, "file.pdf", "bukti valid", "", "",
                "tester", Instant.now(), Instant.now(), "tester");

        when(sasaranOpdRepository.findAllByKodeOpdAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "2026", "3"))
                .thenReturn(Flux.just(entity));
        when(penetapanSasaranOpdClient.fetchSasaranOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(
                        "2026", "5.01.5.05.0.00.01.0000", "3"))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, response.id());
                    org.junit.jupiter.api.Assertions.assertEquals(75.0, response.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("SAS-OPD-001", response.kodeSasaranOpd());
                    org.junit.jupiter.api.Assertions.assertEquals("IND-01", response.kodeIndikator());
                    org.junit.jupiter.api.Assertions.assertEquals("TGT-001", response.kodeTarget());
                })
                .verifyComplete();
    }

    @Test
    void getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan_ShouldReturnEmpty_WhenNoData() {
        when(sasaranOpdRepository.findAllByKodeOpdAndTahunAndBulan("1.01.0.00.0.00.01.0000", "2026", "1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(sasaranOpdService.getRealisasiSasaranOpdByTahunAndKodeOpdAndBulan(
                        "2026", "1.01.0.00.0.00.01.0000", "1"))
                .verifyComplete();
    }

    @Test
    void submitRealisasiSasaranOpd_ShouldCreateNew_WhenNotExists() {
        SasaranOpdRequest req = new SasaranOpdRequest("SAS-OPD-001", "IND-01", "TGT-001",
                75.0, JenisRealisasi.NAIK, "2026", "3", "5.01.5.05.0.00.01.0000", "file.pdf", "bukti valid");

        SasaranOpd saved = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "3",
                "SAS-OPD-001", "IND-01", "TGT-001", BigDecimal.valueOf(75),
                JenisRealisasi.NAIK, "file.pdf", "bukti valid", "", "",
                null, null, null, null);

        when(sasaranOpdRepository.findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "SAS-OPD-001", "IND-01", "TGT-001", "2026", "3"))
                .thenReturn(Mono.empty());
        when(sasaranOpdRepository.save(any(SasaranOpd.class))).thenReturn(Mono.just(saved));
        when(penetapanSasaranOpdClient.fetchSasaranOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(sasaranOpdService.submitRealisasiSasaranOpd(req))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(75.0, response.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("SAS-OPD-001", response.kodeSasaranOpd());
                })
                .verifyComplete();
    }

    @Test
    void submitRealisasiSasaranOpd_ShouldUpdate_WhenExists() {
        SasaranOpdRequest req = new SasaranOpdRequest("SAS-OPD-001", "IND-01", "TGT-001",
                85.0, JenisRealisasi.NAIK, "2026", "3", "5.01.5.05.0.00.01.0000", "file.pdf", "bukti valid");

        SasaranOpd existing = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "3",
                "SAS-OPD-001", "IND-01", "TGT-001", BigDecimal.valueOf(75),
                JenisRealisasi.NAIK, "faktor", "", "file.pdf", "bukti valid",
                "creator", Instant.now(), null, null);

        SasaranOpd updated = new SasaranOpd(1L, "5.01.5.05.0.00.01.0000", "2026", "3",
                "SAS-OPD-001", "IND-01", "TGT-001", BigDecimal.valueOf(85),
                JenisRealisasi.NAIK, "faktor", "", "file.pdf", "bukti valid",
                "creator", existing.createdDate(), null, null);

        when(sasaranOpdRepository.findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "5.01.5.05.0.00.01.0000", "SAS-OPD-001", "IND-01", "TGT-001", "2026", "3"))
                .thenReturn(Mono.just(existing));
        when(sasaranOpdRepository.save(any(SasaranOpd.class))).thenReturn(Mono.just(updated));
        when(penetapanSasaranOpdClient.fetchSasaranOpd("5.01.5.05.0.00.01.0000", 2026))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(sasaranOpdService.submitRealisasiSasaranOpd(req))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(85.0, response.realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("faktor", response.faktorPenunjang());
                })
                .verifyComplete();
    }
}
