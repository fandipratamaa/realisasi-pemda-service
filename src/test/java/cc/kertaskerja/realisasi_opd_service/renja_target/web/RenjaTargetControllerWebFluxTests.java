package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiService;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import cc.kertaskerja.config.SecurityConfig;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(RenjaTargetController.class)
@Import(SecurityConfig.class)
public class RenjaTargetControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaTargetService renjaTargetService;

    @MockitoBean
    private RenjaOpdHierarkiService renjaOpdHierarkiService;

    @Test
    void whenGetByKodeOpdTahunBulan_thenReturnsHierarkiResponse() {
        RenjaOpdHierarkiResponse response = new RenjaOpdHierarkiResponse(List.of(
                new RenjaOpdHierarkiResponse.DataItem(
                        "001", "2025", "01", 500000, "RENJA-1",
                        List.of(new RenjaOpdHierarkiResponse.RenjaItem(
                                "5.01.03", null, "PROGRAM",
                                List.of(new RenjaOpdHierarkiResponse.TargetItem(
                                        1L, "TAR-1", "100", "50", "%", "NAIK", "CHECKED", "maker", "reviewer", "50.00%", null
                                )),
                                List.of(new RenjaOpdHierarkiResponse.PaguItem(
                                        1L, "500000", 1000000, "CHECKED", "maker", "reviewer", "50.00%", null
                                )),
                                List.of(),
                                null,
                                List.of(new RenjaOpdHierarkiResponse.RenjaItem(
                                        "5.01.03.2.02", null, "KEGIATAN",
                                        List.of(),
                                        List.of(),
                                        List.of(),
                                        null,
                                        null,
                                        List.of(new RenjaOpdHierarkiResponse.RenjaItem(
                                                "5.01.03.2.02.0005", null, "SUBKEGIATAN",
                                                List.of(new RenjaOpdHierarkiResponse.TargetItem(
                                                        2L, "TAR-1", "100", "100", "%", "NAIK", "CHECKED", "maker", "reviewer", "100.00%", null
                                                )),
                                                List.of(new RenjaOpdHierarkiResponse.PaguItem(
                                                        2L, "500000", 1000000, "CHECKED", "maker", "reviewer", "50.00%", null
                                                )),
                                                List.of(),
                                                null,
                                                null,
                                                null
                                        ))
                                )),
                                null
                        ))
                )
        ));

        when(renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan("001", "2025", "01", RenjaOpdHierarkiService.DataSource.TARGET))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data[0].kode_opd").isEqualTo("001")
                .jsonPath("$.data[0].pagu_total_realisasi").isEqualTo(500000)
                .jsonPath("$.data[0].id_renja").isEqualTo("RENJA-1")
                .jsonPath("$.data[0].program[0].target[0].targetRealisasiId").isEqualTo(1)
                .jsonPath("$.data[0].program[0].target[0].realisasi").isEqualTo("50")
                .jsonPath("$.data[0].program[0].target[0].jenisRealisasi").isEqualTo("NAIK")
                .jsonPath("$.data[0].program[0].target[0].status").isEqualTo("CHECKED")
                .jsonPath("$.data[0].program[0].target[0].createdBy").isEqualTo("maker")
                .jsonPath("$.data[0].program[0].target[0].lastModifiedBy").isEqualTo("reviewer")
                .jsonPath("$.data[0].program[0].target[0].capaian").isEqualTo("50.00%")
                .jsonPath("$.data[0].program[0].pagu[0].paguRealisasiId").isEqualTo(1)
                .jsonPath("$.data[0].program[0].pagu[0].realisasi").isEqualTo("500000")
                .jsonPath("$.data[0].program[0].pagu[0].pagu").isEqualTo(1000000)
                .jsonPath("$.data[0].program[0].kegiatan[0].subkegiatan[0].target[0].id_target").isEqualTo("TAR-1")
                .jsonPath("$.data[0].program[0].kegiatan[0].subkegiatan[0].target[0].target").isEqualTo("100")
                .jsonPath("$.data[0].program[0].nama_renja").isEmpty();
    }

    @Test
    void whenSuperAdminGetsOpdEndpoint_thenAllowed() {
        RenjaOpdHierarkiResponse response = new RenjaOpdHierarkiResponse(List.of());

        when(renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan("001", "2025", "01", RenjaOpdHierarkiService.DataSource.TARGET))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("super_admin")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data").isArray();
    }

    @Test
    void whenAdminOpdGetsOpdEndpoint_thenAllowed() {
        RenjaOpdHierarkiResponse response = new RenjaOpdHierarkiResponse(List.of());

        when(renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan("001", "2025", "01", RenjaOpdHierarkiService.DataSource.TARGET))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data").isArray();
    }

    @Test
    void whenLevel1GetsRenjaTargetOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_1")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel2GetsRenjaTargetOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_2")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel3GetsRenjaTargetOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_3")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLevel4GetsRenjaTargetOpdEndpoint_thenForbidden() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("level_4")))
                .get()
                .uri("/renja_target/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().isForbidden();
    }

}
