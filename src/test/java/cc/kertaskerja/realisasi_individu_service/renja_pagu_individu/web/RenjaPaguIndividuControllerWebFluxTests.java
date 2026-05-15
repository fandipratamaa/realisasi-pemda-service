package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain.RenjaPaguIndividu;
import cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain.RenjaPaguIndividuService;
import cc.kertaskerja.renja.domain.JenisRenja;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import cc.kertaskerja.config.SecurityConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RenjaPaguIndividuController.class)
@Import(SecurityConfig.class)
public class RenjaPaguIndividuControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaPaguIndividuService renjaPaguIndividuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnsSavedRenjaPaguIndividus() throws Exception {
RenjaPaguIndividuRequest r1 = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );
        RenjaPaguIndividuRequest r2 = new RenjaPaguIndividuRequest(
                null,
                "1.02.02",
                JenisRenja.KEGIATAN,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-2",
                "Indikator B",
                200,
                75,
                "rupiah",
                "2026",
                "Februari",
                JenisRealisasi.NAIK
        );

        RenjaPaguIndividu p1 = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                r1.kodeRenja(),
                r1.jenisRenja(),
                r1.nip(),
                r1.namaPegawai(),
                r1.kodeOpd(),
                r1.idIndikator(),
                r1.indikator(),
                r1.pagu(),
                r1.realisasi(),
                r1.satuan(),
                r1.tahun(),
                r1.bulan(),
                r1.jenisRealisasi()
        );
        RenjaPaguIndividu p2 = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                r2.kodeRenja(),
                r2.jenisRenja(),
                r2.nip(),
                r2.namaPegawai(),
                r2.kodeOpd(),
                r2.idIndikator(),
                r2.indikator(),
                r2.pagu(),
                r2.realisasi(),
                r2.satuan(),
                r2.tahun(),
                r2.bulan(),
                r2.jenisRealisasi()
        );

        when(renjaPaguIndividuService.batchSubmitRealisasiRenjaPaguIndividu(anyList()))
                .thenReturn(Flux.just(p1, p2));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/renja_pagu_individu/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaPaguIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(p1, body.get(0));
                    Assertions.assertEquals("50.00%", body.get(0).capaian());
                    Assertions.assertEquals(p2, body.get(1));
                    Assertions.assertEquals("37.50%", body.get(1).capaian());
                });
    }

    @Test
    void whenGetByTahunBulanAndKodeOpd_thenReturnsRenjaPaguIndividus() {
        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "1",
                JenisRealisasi.NAIK
        );

        when(renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByTahunAndBulanAndKodeOpd(anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renja_pagu_individu/by-kode-opd/1.01.0.00.0.00.01.0000/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaPaguIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }

    @Test
    void whenGetByKodeOpdNipTahunBulan_thenReturnsRenjaPaguIndividus() {
        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "1",
                JenisRealisasi.NAIK
        );

        when(renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulanAndKodeOpd(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renja_pagu_individu/by-kode-opd/1.01.0.00.0.00.01.0000/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaPaguIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }

    @Test
    void whenSuperAdminGetsIndividuEndpoint_thenAllowed() {
        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "1",
                JenisRealisasi.NAIK
        );

        when(renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan("198012312005011001", "2025", "1"))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("super_admin")))
                .get()
                .uri("/renja_pagu_individu/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RenjaPaguIndividu.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenSuperAdminPostsIndividuEndpoint_thenForbidden() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("super_admin")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAdminOpdGetsIndividuEndpoint_thenAllowed() {
        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "1",
                JenisRealisasi.NAIK
        );

        when(renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan("198012312005011001", "2025", "1"))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renja_pagu_individu/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RenjaPaguIndividu.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenAdminOpdPostsIndividuEndpoint_thenForbidden() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("admin_opd")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel1GetsRenjaPaguIndividuEndpoint_thenAllowed() {
        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "1",
                JenisRealisasi.NAIK
        );

        when(renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan("198012312005011001", "2025", "1"))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .get()
                .uri("/renja_pagu_individu/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RenjaPaguIndividu.class)
                .hasSize(1)
                .contains(result);
    }

    @Test
    void whenLevel1PostsRenjaPaguIndividuEndpoint_thenForbidden() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel2PostsRenjaPaguIndividuEndpoint_thenAllowed() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                request.kodeRenja(),
                request.jenisRenja(),
                request.nip(),
                request.namaPegawai(),
                request.kodeOpd(),
                request.idIndikator(),
                request.indikator(),
                request.pagu(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.jenisRealisasi()
        );

        when(renjaPaguIndividuService.submitRealisasiRenjaPaguIndividu(
                request.kodeRenja(),
                request.jenisRenja(),
                request.nip(),
                request.namaPegawai(),
                request.kodeOpd(),
                request.idIndikator(),
                request.indikator(),
                request.pagu(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.jenisRealisasi()))
                .thenReturn(reactor.core.publisher.Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_2")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RenjaPaguIndividu.class)
                .isEqualTo(result);
    }

    @Test
    void whenLevel3PostsRenjaPaguIndividuEndpoint_thenAllowed() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        RenjaPaguIndividu result = RenjaPaguIndividuService.buildUncheckedRealisasiRenjaPaguIndividu(
                request.kodeRenja(),
                request.jenisRenja(),
                request.nip(),
                request.namaPegawai(),
                request.kodeOpd(),
                request.idIndikator(),
                request.indikator(),
                request.pagu(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.jenisRealisasi()
        );

        when(renjaPaguIndividuService.submitRealisasiRenjaPaguIndividu(
                request.kodeRenja(),
                request.jenisRenja(),
                request.nip(),
                request.namaPegawai(),
                request.kodeOpd(),
                request.idIndikator(),
                request.indikator(),
                request.pagu(),
                request.realisasi(),
                request.satuan(),
                request.tahun(),
                request.bulan(),
                request.jenisRealisasi()))
                .thenReturn(reactor.core.publisher.Mono.just(result));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_3")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RenjaPaguIndividu.class)
                .isEqualTo(result);
    }

    @Test
    void whenLevel4GetsRenjaPaguIndividuEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .get()
                .uri("/renja_pagu_individu/by-nip/198012312005011001/by-tahun/2025/by-bulan/1")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel4PostsRenjaPaguIndividuEndpoint_thenForbidden() throws Exception {
        RenjaPaguIndividuRequest request = new RenjaPaguIndividuRequest(
                null,
                "1.02.01",
                JenisRenja.PROGRAM,
                "198012312005011001",
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "IND-1",
                "Indikator A",
                100,
                50,
                "rupiah",
                "2025",
                "Januari",
                JenisRealisasi.NAIK
        );

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .post()
                .uri("/renja_pagu_individu")
                .bodyValue(objectMapper.writeValueAsString(request))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isForbidden();
    }
}
