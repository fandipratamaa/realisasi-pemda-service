package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    void whenGetPenetapanByNipTahun_thenReturnsPenetapanData() {
        var response = new PenetapanRekinIndividuResponse(
                "198012312005011001", "MATILDA DEW -, S.Sos",
                "8.01.0.00.0.00.01.0000", 2026, null,
                List.of(new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                        1L, null, "REKIN-PEG-2026-33475",
                        "Peningkatan Pembinaan", 5,
                        List.of(new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                                1L, "IND-REKIN-87169", "Persentase terlaksananya",
                                List.of(new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                                        1L, "TRGT-IND-REKIN-66602", 2026, 100.0, "%",
                                        null, null, null, null, null, null
                                ))
                        ))
                ))
        );

        when(rekinService.getPenetapanByNip(anyString(), anyString(), anyInt(), eq(null)))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
                .get()
                .uri("/rekin/nip/198012312005011001/kodeOpd/8.01.0.00.0.00.01.0000/tahun/2026/penetapan")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PenetapanRekinIndividuResponse.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("198012312005011001", body.pegawaiId());
                    Assertions.assertEquals("MATILDA DEW -, S.Sos", body.nama());
                    Assertions.assertEquals("8.01.0.00.0.00.01.0000", body.kodeOpd());
                    Assertions.assertEquals(2026, body.tahunAktif());
                    Assertions.assertNull(body.bulan());
                    Assertions.assertNotNull(body.rekins());
                    Assertions.assertEquals(1, body.rekins().size());
                    Assertions.assertEquals("REKIN-PEG-2026-33475", body.rekins().getFirst().kodePk());
                });
    }

    @Test
    void whenGetPenetapanWithBulan_thenIncludesRealisasi() {
        var response = new PenetapanRekinIndividuResponse(
                "198012312005011001", "MATILDA DEW -, S.Sos",
                "8.01.0.00.0.00.01.0000", 2026, 1,
                List.of(new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                        1L, null, "REKIN-PEG-2026-33475",
                        "Peningkatan Pembinaan", 5,
                        List.of(new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                                1L, "IND-REKIN-87169", "Persentase terlaksananya",
                                List.of(new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                                        1L, "TRGT-IND-REKIN-66602", 2026, 100.0, "%",
                                        75.5, 75.5, null, null, null, null
                                ))
                        ))
                ))
        );

        when(rekinService.getPenetapanByNip(eq("198012312005011001"), eq("8.01.0.00.0.00.01.0000"), eq(2026), eq("1")))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
                .get()
                .uri("/rekin/nip/198012312005011001/kodeOpd/8.01.0.00.0.00.01.0000/tahun/2026/penetapan?bulan=1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PenetapanRekinIndividuResponse.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.bulan());
                    var target = body.rekins().getFirst().indikatorPk().getFirst().targetPk().getFirst();
                    Assertions.assertEquals(75.5, target.realisasi());
                    Assertions.assertEquals(75.5, target.capaian());
                });
    }

    @Test
    void whenLevel1PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2025",
                "01"
        );

        RekinResponse result = new RekinResponse(
                1L, request.kodeOpd(), request.nip(), request.tahun(), request.bulan(),
                request.kodePkRekin(), request.kodeIndikatorPKrekin(), request.kodeTargetPKrekin(),
                request.kodeSasaranOpd(),
                request.realisasi(), request.jenisRealisasi(), "", "",
                null, null, null, null, 75.5, null);

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
                .expectBody(RekinResponse.class)
                .isEqualTo(result);
    }

    @Test
    void whenLevel4PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2025",
                "01"
        );

        RekinResponse result = new RekinResponse(
                1L, request.kodeOpd(), request.nip(), request.tahun(), request.bulan(),
                request.kodePkRekin(), request.kodeIndikatorPKrekin(), request.kodeTargetPKrekin(),
                request.kodeSasaranOpd(),
                request.realisasi(), request.jenisRealisasi(), "", "",
                null, null, null, null, 75.5, null);

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
                .expectBody(RekinResponse.class)
                .isEqualTo(result);
    }
}
