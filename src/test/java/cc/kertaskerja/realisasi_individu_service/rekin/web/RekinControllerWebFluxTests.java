package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.Rekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinStatus;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinWithDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RekinController.class)
@Import(SecurityConfig.class)
public class RekinControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RekinService rekinService;

    @Test
    void whenGetByKodeOpdTahunBulan_thenReturnsDetails() {
        Rekin rekin = Rekin.of("1.01.0.00.0.00.01.0000", "198012312005011001",
                "REKIN-001", "SAS-001", "Realisasi Rekin REKIN-001", "2025", "01", RekinStatus.UNCHECKED);

        RekinWithDetails details = new RekinWithDetails(rekin, Collections.emptyList(), Collections.emptyList());

        when(rekinService.getRekinWithDetailsByKodeOpdAndTahunAndBulan(anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(details));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
                .get()
                .uri("/rekin/by-kode-opd/1.01.0.00.0.00.01.0000/by-tahun/2025/by-bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RekinWithDetails.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(rekin.kodeRekin(), body.get(0).rekin().kodeRekin());
                });
    }

    @Test
    void whenLevel1PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                null,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2025",
                "01"
        );

        Rekin rekin = Rekin.of(
                request.kodeOpd(),
                request.nip(),
                request.kodeRekin(),
                request.kodeSasaranOpd(),
                "Realisasi Rekin " + request.kodeRekin(),
                request.tahun(),
                request.bulan(),
                RekinStatus.UNCHECKED
        );

        RekinWithDetails result = new RekinWithDetails(rekin, Collections.emptyList(), Collections.emptyList());

        when(rekinService.createRekin(any(RekinRequest.class)))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/rekin")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RekinWithDetails.class)
                .isEqualTo(result);
    }

    @Test
    void whenLevel4PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                null,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2025",
                "01"
        );

        Rekin rekin = Rekin.of(
                request.kodeOpd(),
                request.nip(),
                request.kodeRekin(),
                request.kodeSasaranOpd(),
                "Realisasi Rekin " + request.kodeRekin(),
                request.tahun(),
                request.bulan(),
                RekinStatus.UNCHECKED
        );

        RekinWithDetails result = new RekinWithDetails(rekin, Collections.emptyList(), Collections.emptyList());

        when(rekinService.createRekin(any(RekinRequest.class)))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .post()
                .uri("/rekin")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RekinWithDetails.class)
                .isEqualTo(result);
    }
}
