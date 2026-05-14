package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdHierarkiService;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguService;
import cc.kertaskerja.renja.domain.JenisRenja;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RenjaPaguController.class)
public class RenjaPaguControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaPaguService renjaPaguService;

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
                                        1L, "TAR-1", "100", "10", "%", "NAIK", "CHECKED", "maker", "reviewer", "10.00%", null
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
                                                        2L, "TAR-1", "100", "10", "%", "NAIK", "CHECKED", "maker", "reviewer", "10.00%", null
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

        when(renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan("001", "2025", "01", RenjaOpdHierarkiService.DataSource.PAGU))
                .thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renja_pagu/kodeOpd/001/tahun/2025/bulan/01")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data[0].kode_opd").isEqualTo("001")
                .jsonPath("$.data[0].pagu_total_realisasi").isEqualTo(500000)
                .jsonPath("$.data[0].id_renja").isEqualTo("RENJA-1")
                .jsonPath("$.data[0].program[0].target[0].targetRealisasiId").isEqualTo(1)
                .jsonPath("$.data[0].program[0].target[0].realisasi").isEqualTo("10")
                .jsonPath("$.data[0].program[0].target[0].jenisRealisasi").isEqualTo("NAIK")
                .jsonPath("$.data[0].program[0].target[0].status").isEqualTo("CHECKED")
                .jsonPath("$.data[0].program[0].target[0].createdBy").isEqualTo("maker")
                .jsonPath("$.data[0].program[0].target[0].lastModifiedBy").isEqualTo("reviewer")
                .jsonPath("$.data[0].program[0].target[0].capaian").isEqualTo("10.00%")
                .jsonPath("$.data[0].program[0].pagu[0].paguRealisasiId").isEqualTo(1)
                .jsonPath("$.data[0].program[0].pagu[0].realisasi").isEqualTo("500000")
                .jsonPath("$.data[0].program[0].pagu[0].pagu").isEqualTo(1000000)
                .jsonPath("$.data[0].program[0].kegiatan[0].subkegiatan[0].target[0].id_target").isEqualTo("TAR-1")
                .jsonPath("$.data[0].program[0].kegiatan[0].subkegiatan[0].target[0].target").isEqualTo("100")
                .jsonPath("$.data[0].program[0].nama_renja").isEmpty();
    }

    @Test
    void whenGetByFilterLengkap_thenReturnsFilteredRenjaPagus() throws Exception {
        RenjaPagu p1 = RenjaPaguService.buildUncheckedRealisasiRenjaPagu(
                "RENJA-1", JenisRenja.PROGRAM, 100, 50,
                "rupiah", "2025", "01", JenisRealisasi.NAIK, "001", "001"
        );

        when(renjaPaguService.getRealisasiRenjaPaguByKodeOpdAndTahunAndBulanAndJenisRenjaAndKodeRenjaAndRenjaId(
                "001", "2025", "01", "PROGRAM", "001", "RENJA-1"))
                .thenReturn(Flux.just(p1));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/renja_pagu/kodeOpd/001/by-tahun/2025/by-bulan/01/by-jenis-renja/PROGRAM/by-kode-renja/001/by-jenis-renja-id/RENJA-1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaPagu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals("RENJA-1", body.get(0).jenisRenjaId());
                    Assertions.assertEquals("001", body.get(0).kodeOpd());
                    Assertions.assertEquals("2025", body.get(0).tahun());
                    Assertions.assertEquals("01", body.get(0).bulan());
                });
    }

    @Test
    void whenDeleteByRenjaId_thenReturnsNoContent() {
        when(renjaPaguService.deleteRealisasiRenjaPaguByRenjaId("RENJA-1"))
                .thenReturn(Mono.empty());

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .delete()
                .uri("/renja_pagu/RENJA-1")
                .exchange()
                .expectStatus().isOk();
    }
}
