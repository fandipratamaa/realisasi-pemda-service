package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguService;
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

@WebFluxTest(RenjaPaguController.class)
public class RenjaPaguControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenjaPaguService renjaPaguService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnsSavedRenjaPagus() throws Exception {
        RenjaPaguRequest r1 = new RenjaPaguRequest(
                null, "RENJA-1", "Program A", JenisRenja.PROGRAM, 100, 50,
                "rupiah", "2025", JenisRealisasi.NAIK, "001"
        );
        RenjaPaguRequest r2 = new RenjaPaguRequest(
                null, "RENJA-2", "Kegiatan B", JenisRenja.KEGIATAN, 200, 75,
                "rupiah", "2026", JenisRealisasi.NAIK, "001"
        );

        RenjaPagu p1 = RenjaPaguService.buildUncheckedRealisasiRenjaPagu(
                r1.renjaPaguId(),
                r1.renjaPagu(),
                r1.jenisRenjaPagu(),
                r1.pagu(),
                r1.realisasi(),
                r1.satuan(),
                r1.tahun(),
                r1.jenisRealisasi(),
                r1.kodeOpd()
        );
        RenjaPagu p2 = RenjaPaguService.buildUncheckedRealisasiRenjaPagu(
                r2.renjaPaguId(),
                r2.renjaPagu(),
                r2.jenisRenjaPagu(),
                r2.pagu(),
                r2.realisasi(),
                r2.satuan(),
                r2.tahun(),
                r2.jenisRealisasi(),
                r2.kodeOpd()
        );

        when(renjaPaguService.batchSubmitRealisasiRenjaPagu(anyList()))
                .thenReturn(Flux.just(p1, p2));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/renja_pagu/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(RenjaPagu.class)
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
