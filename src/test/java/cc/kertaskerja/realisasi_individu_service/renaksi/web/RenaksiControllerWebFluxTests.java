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
        String bulan = "1";

        RenaksiIndividu r1 = RenaksiIndividu.of(
                kodeOpd, nip, "SASARAN-1", "Realisasi Sasaran SASARAN-1",
                "RENAKSI-1", "Realisasi Renaksi RENAKSI-1",
                "IND-1", "Realisasi Indikator IND-1",
                "TAR-1", BigDecimal.valueOf(100), BigDecimal.valueOf(50000000),
                BigDecimal.valueOf(75), tahun, bulan, "%",
                RenaksiStatus.UNCHECKED, JenisRealisasi.NAIK, "", "");
        RenaksiIndividu r2 = RenaksiIndividu.of(
                kodeOpd, nip, "SASARAN-2", "Realisasi Sasaran SASARAN-2",
                "RENAKSI-2", "Realisasi Renaksi RENAKSI-2",
                "IND-2", "Realisasi Indikator IND-2",
                "TAR-2", BigDecimal.valueOf(50), BigDecimal.valueOf(25000000),
                BigDecimal.valueOf(25), tahun, bulan, "%",
                RenaksiStatus.UNCHECKED, JenisRealisasi.NAIK, "", "");

        when(renaksiService.getAllByNipAndKodeOpdAndTahunAndBulan(nip, kodeOpd, tahun, bulan))
                .thenReturn(Flux.just(r1, r2));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renaksi_individu/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}", nip, kodeOpd, tahun, bulan)
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
