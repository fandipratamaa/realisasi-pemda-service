package cc.kertaskerja.realisasi_individu_service.renja_target_individu.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain.RenjaTargetIndividuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(RenjaTargetIndividuController.class)
@Import(SecurityConfig.class)
public class RenjaTargetIndividuControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaTargetIndividuService renjaTargetIndividuService;

    @Test
    void whenLevel4GetsRenjaTargetIndividuEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .get()
                .uri("/renja_target_individu/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().isForbidden();
    }
}
