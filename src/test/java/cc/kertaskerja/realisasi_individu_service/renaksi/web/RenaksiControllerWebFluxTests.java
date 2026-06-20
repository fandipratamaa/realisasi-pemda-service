package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiStatus;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.SasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.SasaranWithDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

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
        String bulan = "Januari";

        SasaranIndividu s1 = SasaranIndividu.of(kodeOpd, nip, "SASARAN-1",
                "Sasaran A", tahun, bulan, RenaksiStatus.UNCHECKED);
        SasaranIndividu s2 = SasaranIndividu.of(kodeOpd, nip, "SASARAN-2",
                "Sasaran B", tahun, bulan, RenaksiStatus.UNCHECKED);

        SasaranWithDetails d1 = new SasaranWithDetails(s1, List.of());
        SasaranWithDetails d2 = new SasaranWithDetails(s2, List.of());

        when(renaksiService.getSasaranWithDetailsByNipAndKodeOpdAndTahunAndBulan(nip, kodeOpd, tahun, bulan))
                .thenReturn(Flux.just(d1, d2));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renaksi_individu/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}", nip, kodeOpd, tahun, bulan)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SasaranWithDetails.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(d1, body.get(0));
                    Assertions.assertEquals(d2, body.get(1));
                });
    }
}
