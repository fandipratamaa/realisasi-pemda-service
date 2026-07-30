package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(RenaksiController.class)
public class RenaksiControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenaksiService renaksiService;

    @Test
    void whenSearchRealisasi_thenReturnsPenetapanResponse() {
        String nip = "198012312005011001";
        String kodeOpd = "4.01.01.";
        String tahun = "2026";
        String bulan = "1";
        int tahunInt = 2026;

        PenetapanRekinIndividuResponse response = new PenetapanRekinIndividuResponse(
                nip, null, kodeOpd, tahunInt, Integer.parseInt(bulan), List.of()
        );

        when(renaksiService.searchRealisasi(kodeOpd, tahun, bulan, "LEVEL_1", nip))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
                .get()
                .uri("/renaksi_individu/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}", kodeOpd, tahun, bulan, "LEVEL_1", nip)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PenetapanRekinIndividuResponse.class)
                .consumeWith(responseResult -> {
                    var body = responseResult.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(nip, body.pegawaiId());
                    Assertions.assertEquals(kodeOpd, body.kodeOpd());
                    Assertions.assertEquals(tahunInt, body.tahunAktif());
                    Assertions.assertEquals(Integer.parseInt(bulan), body.bulan());
                });
    }
}
