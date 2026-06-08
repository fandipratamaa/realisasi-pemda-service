package cc.kertaskerja.realisasi_opd_service.sasaran.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(SasaranOpdController.class)
@Import(SecurityConfig.class)
class SasaranOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SasaranOpdService sasaranOpdService;

    @Test
    void whenLevel1GetsSasaranOpdByKodeOpdTahunBulan_thenMergedResponseReturned() {
        PenetapanSasaranOpdListResponse result = new PenetapanSasaranOpdListResponse(
                "5.01.5.05.0.00.01.0000",
                2026,
                3,
                List.of(new SasaranOpdPenetapanResponse(
                        12L,
                        "SAS-OPD-193",
                        null,
                        List.of(new SasaranOpdPenetapanResponse.IndikatorPenetapan(
                                "IND-59",
                                null,
                                null,
                                null,
                                null,
                                List.of(new SasaranOpdPenetapanResponse.TargetPenetapan(
                                        "TGT-TRG-SAS-1bdac",
                                        null,
                                        null,
                                        80.0,
                                        null,
                                        null,
                                        null,
                                        null
                                ))
                        ))
                ))
        );

        when(sasaranOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "3"))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("level_1")))
                .get()
                .uri("/sasaran_opd/5.01.5.05.0.00.01.0000/tahun/2026/penetapan?bulan=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.kode_opd").isEqualTo("5.01.5.05.0.00.01.0000")
                .jsonPath("$.tahun").isEqualTo(2026)
                .jsonPath("$.sasaranOpds[0].kode_sasaran_opd").isEqualTo("SAS-OPD-193")
                .jsonPath("$.sasaranOpds[0].indikators[0].kode_indikator").isEqualTo("IND-59")
                .jsonPath("$.sasaranOpds[0].indikators[0].targets[0].realisasi").isEqualTo(80.0);
    }
}
