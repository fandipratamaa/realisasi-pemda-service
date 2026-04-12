package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.Renaksi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@WebFluxTest(RenaksiController.class)
public class RenaksiControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenaksiService renaksiService;

    @Test
    void whenGetByNipAndBulan_thenReturnsList() {
        String nip = "198012312005011001";
        String bulan = "Januari";

        Renaksi r1 = RenaksiService.buildUncheckedRealisasiRenaksi(
                "RENAKSI-1",
                "Renaksi A",
                nip,
                "REKIN-1",
                "Rekin A",
                "TAR-1",
                "100",
                50,
                "%",
                bulan,
                "2026",
                JenisRealisasi.NAIK
        );
        Renaksi r2 = RenaksiService.buildUncheckedRealisasiRenaksi(
                "RENAKSI-2",
                "Renaksi B",
                nip,
                "REKIN-1",
                "Rekin A",
                "TAR-2",
                "200",
                120,
                "%",
                bulan,
                "2026",
                JenisRealisasi.NAIK
        );

        when(renaksiService.getRealisasiRenaksiByNipAndBulan(nip, bulan))
                .thenReturn(Flux.just(r1, r2));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renaksi/by-nip/{nip}/by-bulan/{bulan}", nip, bulan)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Renaksi.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(r1, body.get(0));
                    Assertions.assertEquals(r2, body.get(1));
                });
    }
}

