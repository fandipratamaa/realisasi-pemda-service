package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(SasaranIndividuController.class)
public class SasaranIndividuControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SasaranIndividuService sasaranIndividuService;

    @Test
    void whenGetPenetapan_thenReturnsPenetapanSasaranIndividuListResponse() {
        PenetapanSasaranIndividuListResponse response = new PenetapanSasaranIndividuListResponse(
                "1.01.0.00.0.00.01.0000", 2026, null, List.of()
        );

        when(sasaranIndividuService.getPenetapanWithRealisasi(any(), any(), anyInt(), any()))
                .thenReturn(Mono.just(response));

        var result = webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/sasaran_individu/1.01.0.00.0.00.01.0000/nip/123456/tahun/2026/penetapan")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PenetapanSasaranIndividuListResponse.class)
                .returnResult();

        PenetapanSasaranIndividuListResponse body = result.getResponseBody();
        assert body != null;
        assert body.kodeOpd().equals("1.01.0.00.0.00.01.0000");
        assert body.tahun().equals(2026);
        assert body.sasaranIndividus().isEmpty();
    }

    @Test
    void whenGetRealisasiBulanan_thenReturnsSasaranIndividuResponseList() {
        SasaranIndividuResponse sasaran = new SasaranIndividuResponse(
                1L, "1.01.0.00.0.00.01.0000", "SAS-001", "198001012010011001", "John Doe", "Sasaran Test",
                2025, 1, List.of()
        );

        when(sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndKodeOpdAndBulan(
                anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(sasaran));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/sasaran_individu/1.01.0.00.0.00.01.0000/tahun/2025/bulan/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(SasaranIndividuResponse.class)
                .hasSize(1);
    }
}
