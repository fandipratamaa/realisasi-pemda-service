package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(RenaksiController.class)
public class RenaksiControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenaksiService renaksiService;

    @Test
    void whenGetByNipKodeOpdTahunBulan_thenReturnsList() {
        String nip = "198012312005011001";
        String kodeOpd = "4.01.01.";
        String tahun = "2026";
        String bulan = "1";

        RenaksiIndividu r1 = RenaksiIndividu.of(
                kodeOpd, nip, "REKIN-1", "RENAKSI-1", "TAR-1",
                BigDecimal.valueOf(75), tahun, bulan, "%",
                RenaksiStatus.UNCHECKED, JenisRealisasi.NAIK, "", "", "", "");
        RenaksiIndividu r2 = RenaksiIndividu.of(
                kodeOpd, nip, "REKIN-2", "RENAKSI-2", "TAR-2",
                BigDecimal.valueOf(25), tahun, bulan, "%",
                RenaksiStatus.UNCHECKED, JenisRealisasi.NAIK, "", "", "", "");

        when(renaksiService.searchRealisasi(kodeOpd, tahun, bulan, "LEVEL_1", nip))
                .thenReturn(Flux.just(r1, r2));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
                .get()
                .uri("/renaksi_individu/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}", kodeOpd, tahun, bulan, "LEVEL_1", nip)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RenaksiIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(r1, body.get(0));
                    Assertions.assertEquals(r2, body.get(1));
                });
    }
}
