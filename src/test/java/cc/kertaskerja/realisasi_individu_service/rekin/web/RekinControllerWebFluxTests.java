package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.Rekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RekinController.class)
public class RekinControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RekinService rekinService;

    @Test
    void whenGetByKodeOpdTahunBulan_thenReturnsList() {
        Rekin result = RekinService.buildUncheckedRealisasiRekin(
                "REKIN-001",
                "Rekin Peningkatan Infrastruktur",
                "IND-001",
                "Persentase capaian rekin",
                "198012312005011001",
                "SAS-001",
                "Meningkatkan kualitas layanan",
                "TAR-001",
                "100",
                85,
                "%",
                "2025",
                "01",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        when(rekinService.getRealisasiRekinByKodeOpdAndTahunAndBulan(anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/rekin/by-kode-opd/1.01.0.00.0.00.01.0000/by-tahun/2025/by-bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Rekin.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }

    @Test
    void whenGetByKodeOpdNipTahunBulan_thenReturnsList() {
        Rekin result = RekinService.buildUncheckedRealisasiRekin(
                "REKIN-001",
                "Rekin Peningkatan Infrastruktur",
                "IND-001",
                "Persentase capaian rekin",
                "198012312005011001",
                "SAS-001",
                "Meningkatkan kualitas layanan",
                "TAR-001",
                "100",
                85,
                "%",
                "2025",
                "01",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        when(rekinService.getRealisasiRekinByKodeOpdAndNipAndTahunAndBulan(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/rekin/by-kode-opd/1.01.0.00.0.00.01.0000/by-nip/198012312005011001/by-tahun/2025/by-bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Rekin.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }
}
