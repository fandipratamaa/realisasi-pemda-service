package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetService;
import cc.kertaskerja.renja.domain.JenisRenja;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(RenjaTargetController.class)
public class RenjaTargetControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaTargetService renjaTargetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnsSavedRenjaTargets() throws Exception {
        RenjaTargetRequest r1 = new RenjaTargetRequest(
                null, "RENJA-1", "Program A", JenisRenja.PROGRAM,
                "IND-1", "Indikator A", "T-1", "100", 50,
                "unit", "2025", JenisRealisasi.NAIK, "001"
        );
        RenjaTargetRequest r2 = new RenjaTargetRequest(
                null, "RENJA-2", "Kegiatan B", JenisRenja.KEGIATAN,
                "IND-2", "Indikator B", "T-2", "200", 75,
                "unit", "2026", JenisRealisasi.NAIK, "001"
        );

        RenjaTarget p1 = RenjaTargetService.buildUncheckedRealisasiRenjaTarget(
                r1.renjaTargetId(),
                r1.renjaTarget(),
                r1.jenisRenjaTarget(),
                r1.indikatorId(),
                r1.indikator(),
                r1.targetId(),
                r1.target(),
                r1.realisasi(),
                r1.satuan(),
                r1.tahun(),
                r1.jenisRealisasi(),
                r1.kodeOpd()
        );
        RenjaTarget p2 = RenjaTargetService.buildUncheckedRealisasiRenjaTarget(
                r2.renjaTargetId(),
                r2.renjaTarget(),
                r2.jenisRenjaTarget(),
                r2.indikatorId(),
                r2.indikator(),
                r2.targetId(),
                r2.target(),
                r2.realisasi(),
                r2.satuan(),
                r2.tahun(),
                r2.jenisRealisasi(),
                r2.kodeOpd()
        );

        when(renjaTargetService.batchSubmitRealisasiRenjaTarget(anyList()))
                .thenReturn(Flux.just(p1, p2));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/renja_target/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaTarget.class)
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
}
