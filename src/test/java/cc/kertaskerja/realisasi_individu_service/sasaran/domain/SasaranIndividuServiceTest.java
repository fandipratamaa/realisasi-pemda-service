package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import cc.kertaskerja.integration.penetapan.PenetapanSasaranOpdClient;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator.IndikatorSasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator.IndikatorSasaranIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.target.TargetIndikatorSasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.target.TargetIndikatorSasaranIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.sasaran.web.SasaranIndividuResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SasaranIndividuServiceTest {
    @Mock
    private SasaranIndividuRepository sasaranIndividuRepository;

    @Mock
    private IndikatorSasaranIndividuRepository indikatorSasaranIndividuRepository;

    @Mock
    private TargetIndikatorSasaranIndividuRepository targetIndikatorIndividuOpdRepository;

    @Mock
    private PenetapanSasaranOpdClient penetapanClient;

    @InjectMocks
    private SasaranIndividuService sasaranIndividuService;

    @Test
    void getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan_ShouldReturnResponse() {
        String tahun = "2025";
        String kodeOpd = "1.01.0.00.0.00.01.0000";
        String bulan = "1";

        SasaranIndividu sasaran = new SasaranIndividu(
                1L, kodeOpd, "SAS-001", tahun, bulan,
                null, null, null, null
        );

        IndikatorSasaranIndividu indikator = new IndikatorSasaranIndividu(
                1L, 1L, "IND-001", kodeOpd, tahun, bulan,
                null, null, null, null
        );

        TargetIndikatorSasaranIndividu target = new TargetIndikatorSasaranIndividu(
                1L, 1L, "TAR-001", new BigDecimal("50.0"), tahun, bulan,
                null, null, null, null
        );

        when(sasaranIndividuRepository.findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan))
                .thenReturn(Flux.just(sasaran));

        when(indikatorSasaranIndividuRepository.findAll())
                .thenReturn(Flux.just(indikator));

        when(targetIndikatorIndividuOpdRepository.findAll())
                .thenReturn(Flux.just(target));

        when(penetapanClient.fetchSasaranOpd(kodeOpd, Integer.parseInt(tahun)))
                .thenReturn(Mono.just(List.of()));

        Flux<SasaranIndividuResponse> result =
                sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.kodeOpd().equals(kodeOpd) &&
                                response.kodeSasaranOpd().equals("SAS-001") &&
                                response.tahun().equals(2025) &&
                                response.bulan().equals(1) &&
                                response.indikators().size() == 1 &&
                                response.indikators().getFirst().kodeIndikator().equals("IND-001") &&
                                response.indikators().getFirst().targets().size() == 1 &&
                                response.indikators().getFirst().targets().getFirst().kodeTarget().equals("TAR-001") &&
                                response.indikators().getFirst().targets().getFirst().realisasi().equals(50.0)
                )
                .verifyComplete();
    }
}
