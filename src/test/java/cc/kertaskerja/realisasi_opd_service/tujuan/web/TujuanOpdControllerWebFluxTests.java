package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpdService;
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

@WebFluxTest(TujuanOpdController.class)
@Import(SecurityConfig.class)
class TujuanOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TujuanOpdService tujuanOpdService;

    @Test
    void whenLevel1GetsTujuanOpdByKodeOpdTahunBulan_thenMergedResponseReturned() {
        PenetapanTujuanOpdListResponse result = new PenetapanTujuanOpdListResponse(
                "5.01.5.05.0.00.01.0000",
                2026,
                3,
                List.of(new TujuanOpdPenetapanResponse(
                        12L,
                        "TUJ-OPD-193",
                        null,
                        List.of(new TujuanOpdPenetapanResponse.IndikatorPenetapan(
                                "IND-59",
                                null,
                                null,
                                null,
                                null,
                                List.of(new TujuanOpdPenetapanResponse.TargetPenetapan(
                                        "TGT-TRG-TJN-1bdac",
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

        when(tujuanOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "3"))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("level_1")))
                .get()
                .uri("/tujuan_opd/5.01.5.05.0.00.01.0000/tahun/2026/penetapan?bulan=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.kode_opd").isEqualTo("5.01.5.05.0.00.01.0000")
                .jsonPath("$.tahun").isEqualTo(2026)
                .jsonPath("$.tujuanOpds[0].kode_tujuan_opd").isEqualTo("TUJ-OPD-193")
                .jsonPath("$.tujuanOpds[0].indikators[0].kode_indikator").isEqualTo("IND-59")
                .jsonPath("$.tujuanOpds[0].indikators[0].targets[0].realisasi").isEqualTo(80.0);
    }
}
