package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.Renaksi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
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

@WebFluxTest(RenaksiController.class)
public class RenaksiControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenaksiService renaksiService;

    @MockitoBean
    private RenaksiOpdService renaksiOpdService;

    @Test
    void whenGetByKodeOpdTahunBulan_thenReturnsList() {
        String kodeOpd = "4.01.01.";
        String tahun = "2026";
        String bulan = "Januari";

        Renaksi r1 = RenaksiService.buildUncheckedRealisasiRenaksi(
                "RENAKSI-1",
                "Renaksi A",
                "198012312005011001",
                "REKIN-1",
                "Rekin A",
                "TAR-1",
                "100",
                50,
                "%",
                bulan,
                tahun,
                JenisRealisasi.NAIK,
                kodeOpd
        );
        Renaksi r2 = RenaksiService.buildUncheckedRealisasiRenaksi(
                "RENAKSI-2",
                "Renaksi B",
                "198012312005011001",
                "REKIN-1",
                "Rekin A",
                "TAR-2",
                "200",
                120,
                "%",
                bulan,
                tahun,
                JenisRealisasi.NAIK,
                kodeOpd
        );

        when(renaksiService.getRealisasiRenaksiByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan))
                .thenReturn(Flux.just(r1, r2));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renaksi/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}", kodeOpd, tahun, bulan)
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

    @Test
    void whenGetByKodeOpdNipTahunBulan_thenReturnsList() {
        String kodeOpd = "4.01.01.";
        String nip = "198012312005011001";
        String tahun = "2026";
        String bulan = "Januari";

        Renaksi result = RenaksiService.buildUncheckedRealisasiRenaksi(
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
                tahun,
                JenisRealisasi.NAIK,
                kodeOpd
        );

        when(renaksiService.getRealisasiRenaksiByKodeOpdAndNipAndTahunAndBulan(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renaksi/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}", kodeOpd, nip, tahun, bulan)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Renaksi.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }
}

