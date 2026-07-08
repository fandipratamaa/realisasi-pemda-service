package cc.kertaskerja.realisasi_opd_service.renja.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaOpdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(RenjaOpdController.class)
@Import(SecurityConfig.class)
class RenjaOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaOpdService renjaOpdService;

    @Test
    void whenAdminOpdGetsRenjaOpdPenetapan_thenDataReturned() {
        RenjaOpdPenetapanResponse data = new RenjaOpdPenetapanResponse(
                "5.01.5.05.0.00.01.0000",
                2026,
                null,
                List.of(new RenjaOpdPenetapanResponse.ProgramPenetapan(
                        1L, "5.01.02", "PROGRAM PERENCANAAN", true,
                        List.of(new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                                1L, "IND-RENJA-PENETAPAN-001", "test2026",
                                List.of(new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        1L, "TGT-TRG-PENETAPAN-001", 2026, null,
                                        100.0, null, "%", null, null,
                                        null, null
                                ))
                        )),
                        null
                )),
                List.of(),
                List.of()
        );

        when(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, null))
                .thenReturn(Mono.just(data));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renja_opd/5.01.5.05.0.00.01.0000/tahun/2026/penetapan")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.kode_opd").isEqualTo("5.01.5.05.0.00.01.0000")
                .jsonPath("$.tahun").isEqualTo(2026)
                .jsonPath("$.programs[0].kode_program").isEqualTo("5.01.02")
                .jsonPath("$.programs[0].program").isEqualTo("PROGRAM PERENCANAAN")
                .jsonPath("$.programs[0].is_locked").isEqualTo(true)
                .jsonPath("$.programs[0].indikators[0].kode_indikator").isEqualTo("IND-RENJA-PENETAPAN-001")
                .jsonPath("$.programs[0].indikators[0].targets[0].target").isEqualTo(100.0)
                .jsonPath("$.programs[0].indikators[0].targets[0].kode_target").isEqualTo("TGT-TRG-PENETAPAN-001");
    }

    @Test
    void whenAdminOpdGetsRenjaOpdPenetapanWithBulan_thenRealisasiIncluded() {
        RenjaOpdPenetapanResponse data = new RenjaOpdPenetapanResponse(
                "5.01.5.05.0.00.01.0000",
                2026,
                1,
                List.of(new RenjaOpdPenetapanResponse.ProgramPenetapan(
                        1L, "5.01.02", "PROGRAM PERENCANAAN", true,
                        List.of(new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                                1L, "IND-RENJA-PENETAPAN-001", "test2026",
                                List.of(new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        1L, "TGT-TRG-PENETAPAN-001", 2026, 1,
                                        100.0, 70.0, "%", 70.0, "",
                                        null, null
                                ))
                        )),
                        null
                )),
                List.of(),
                List.of()
        );

        when(renjaOpdService.getPenetapanWithRealisasi("5.01.5.05.0.00.01.0000", 2026, "1"))
                .thenReturn(Mono.just(data));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renja_opd/5.01.5.05.0.00.01.0000/tahun/2026/penetapan?bulan=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bulan").isEqualTo(1)
                .jsonPath("$.programs[0].indikators[0].targets[0].realisasi").isEqualTo(70.0)
                .jsonPath("$.programs[0].indikators[0].targets[0].capaian").isEqualTo(70.0)
                .jsonPath("$.programs[0].indikators[0].targets[0].keterangan_capaian").isEqualTo("");
    }
}
