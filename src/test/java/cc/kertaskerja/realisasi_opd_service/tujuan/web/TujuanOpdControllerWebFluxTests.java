package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpdService;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpdStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@WebFluxTest(TujuanOpdController.class)
@Import(SecurityConfig.class)
public class TujuanOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TujuanOpdService tujuanOpdService;

    @Test
    void whenLevel1GetsTujuanOpdEndpoint_thenAllowed() {
        TujuanOpd result = TujuanOpd.of(
                "TUJ-123",
                "Tujuan OPD",
                "IND-123",
                "Indikator OPD",
                "TAR-1",
                "100",
                80.0,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "1.01.0.00.0.00.01.0000",
                "(realisasi/target)*100",
                "BPS",
                "Definisi indikator tujuan",
                TujuanOpdStatus.UNCHECKED
        );

        when(tujuanOpdService.getAllRealisasiTujuanOpd())
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .get()
                .uri("/tujuan_opd")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TujuanOpd.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenLevel1PostsTujuanOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/tujuan_opd")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel2GetsTujuanOpdEndpoint_thenAllowed() {
        TujuanOpd result = TujuanOpd.of(
                "TUJ-123",
                "Tujuan OPD",
                "IND-123",
                "Indikator OPD",
                "TAR-1",
                "100",
                80.0,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "1.01.0.00.0.00.01.0000",
                "(realisasi/target)*100",
                "BPS",
                "Definisi indikator tujuan",
                TujuanOpdStatus.UNCHECKED
        );

        when(tujuanOpdService.getAllRealisasiTujuanOpd())
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_2")))
                .get()
                .uri("/tujuan_opd")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TujuanOpd.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenLevel2PostsTujuanOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_2")))
                .post()
                .uri("/tujuan_opd")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel3GetsTujuanOpdEndpoint_thenAllowed() {
        TujuanOpd result = TujuanOpd.of(
                "TUJ-123",
                "Tujuan OPD",
                "IND-123",
                "Indikator OPD",
                "TAR-1",
                "100",
                80.0,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "1.01.0.00.0.00.01.0000",
                "(realisasi/target)*100",
                "BPS",
                "Definisi indikator tujuan",
                TujuanOpdStatus.UNCHECKED
        );

        when(tujuanOpdService.getAllRealisasiTujuanOpd())
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_3")))
                .get()
                .uri("/tujuan_opd")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TujuanOpd.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenLevel3PostsTujuanOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_3")))
                .post()
                .uri("/tujuan_opd")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel4GetsTujuanOpdEndpoint_thenAllowed() {
        TujuanOpd result = TujuanOpd.of(
                "TUJ-123",
                "Tujuan OPD",
                "IND-123",
                "Indikator OPD",
                "TAR-1",
                "100",
                80.0,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "1.01.0.00.0.00.01.0000",
                "(realisasi/target)*100",
                "BPS",
                "Definisi indikator tujuan",
                TujuanOpdStatus.UNCHECKED
        );

        when(tujuanOpdService.getAllRealisasiTujuanOpd())
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .get()
                .uri("/tujuan_opd")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TujuanOpd.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenLevel4PostsTujuanOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .post()
                .uri("/tujuan_opd")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }
}
