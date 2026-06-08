package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.Rekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import cc.kertaskerja.config.SecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void whenGetByKodeOpdTahunBulan_thenReturnsList() {
        Rekin result = RekinService.buildUncheckedRealisasiRekin(
                "REKIN-001",
                "IND-001",
                "198012312005011001",
                "Anon",
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
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN_OPD")))
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
    void whenLevel1PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                null,
                "REKIN-001",
                "198012312005011001",
                "Anon",
                "IND-001",
                "TAR-001",
                "100",
                85,
                "%",
                "2025",
                "01",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        Rekin result = RekinService.buildUncheckedRealisasiRekin(
                request.rekinId(),
                request.indikatorId(),
                request.nip(),
                request.namaPegawai(),
                request.targetId(),
                request.target(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.kodeOpd(),
                request.jenisRealisasi()
        );

        when(rekinService.submitRealisasiRekin(any(RekinRequest.class)))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/rekin")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Rekin.class)
                .isEqualTo(result);
    }

    @Test
    void whenLevel4PostsRekinEndpoint_thenAllowed() {
        RekinRequest request = new RekinRequest(
                null,
                "REKIN-001",
                "198012312005011001",
                "Anon",
                "IND-001",
                "TAR-001",
                "100",
                85,
                "%",
                "2025",
                "01",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        Rekin result = RekinService.buildUncheckedRealisasiRekin(
                request.rekinId(),
                request.indikatorId(),
                request.nip(),
                request.namaPegawai(),
                request.targetId(),
                request.target(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.kodeOpd(),
                request.jenisRealisasi()
        );

        when(rekinService.submitRealisasiRekin(any(RekinRequest.class)))
                .thenReturn(Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .post()
                .uri("/rekin")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Rekin.class)
                .isEqualTo(result);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenUpdateFaktorPenunjang_thenReturnsUpdatedRekin() throws Exception {
        FaktorPenunjangRekinRequest req = new FaktorPenunjangRekinRequest(
                "198012312005011001", "2025", "01", "REKIN-001", "TAR-001", "Kerjasama tim");

        Rekin updated = Rekin.of("REKIN-001", "Rekin A",
                "IND-001", "Indikator A",
                "198012312005011001", "Anon",
                "TAR-001", "100", 85, "%", "2025", "01",
                "1.01.0.00.0.00.01.0000",
                "Kerjasama tim", "Perubahan prioritas",
                JenisRealisasi.NAIK, RekinStatus.UNCHECKED);

        when(rekinService.updateFaktorPenunjang(any(FaktorPenunjangRekinRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/rekin/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Rekin.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Kerjasama tim", body.faktorPenunjang());
                });
    }

    @Test
    void whenUpdateFaktorPenghambat_thenReturnsUpdatedRekin() throws Exception {
        FaktorPenghambatRekinRequest req = new FaktorPenghambatRekinRequest(
                "198012312005011001", "2025", "01", "REKIN-001", "TAR-001", "Perubahan prioritas");

        Rekin updated = Rekin.of("REKIN-001", "Rekin A",
                "IND-001", "Indikator A",
                "198012312005011001", "Anon",
                "TAR-001", "100", 85, "%", "2025", "01",
                "1.01.0.00.0.00.01.0000",
                "Kerjasama tim", "Perubahan prioritas",
                JenisRealisasi.NAIK, RekinStatus.UNCHECKED);

        when(rekinService.updateFaktorPenghambat(any(FaktorPenghambatRekinRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/rekin/faktor-penghambat")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Rekin.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Perubahan prioritas", body.faktorPenghambat());
                });
    }

    @Test
    void whenUpdateFaktorPenunjangAndNotFound_then404() throws Exception {
        FaktorPenunjangRekinRequest req = new FaktorPenunjangRekinRequest(
                "99", "2099", "01", "REKIN-XX", "TAR-001", "Faktor X");

        when(rekinService.updateFaktorPenunjang(any(FaktorPenunjangRekinRequest.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Rekin tidak ditemukan")));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/rekin/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }
}
