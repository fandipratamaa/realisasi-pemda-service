package cc.kertaskerja.realisasi_opd_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpd;
import cc.kertaskerja.realisasi_opd_service.tujuan.domain.TujuanOpdService;
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

@WebFluxTest(TujuanOpdController.class)
public class TujuanOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TujuanOpdService tujuanOpdService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnsSavedTujuans() throws Exception {
        // prepare requests
        TujuanOpdRequest r1 = new TujuanOpdRequest(null, "T1", "I1", "TAR-1", "100.0", 50.0, "unit1", "2025", "1", JenisRealisasi.NAIK, "001");
        TujuanOpdRequest r2 = new TujuanOpdRequest(null, "T2", "I2", "TAR-2", "200.0", 75.0, "unit2", "2026", "2", JenisRealisasi.TURUN, "001");

        // prepare expected domain objects
        TujuanOpd t1 = TujuanOpdService.buildUncheckedRealisasiTujuanOpd(
                r1.tujuanId(), r1.indikatorId(), r1.targetId() ,r1.target(), r1.realisasi(),
                r1.satuan(), r1.tahun(), r1.bulan(), r1.jenisRealisasi(),
                r1.kodeOpd()
        );
        TujuanOpd t2 = TujuanOpdService.buildUncheckedRealisasiTujuanOpd(
                r2.tujuanId(), r2.indikatorId(), r2.targetId(), r2.target(), r2.realisasi(),
                r2.satuan(), r2.tahun(), r2.bulan(), r2.jenisRealisasi(),
                r2.kodeOpd()
        );

        when(tujuanOpdService.batchSubmitRealisasiTujuanOpd(anyList()))
                .thenReturn(Flux.just(t1, t2));

        // execute POST /tujuans/batch
        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuan_opd/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(TujuanOpd.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(t1, body.get(0));
                    Assertions.assertEquals("50.00%", body.get(0).capaian());
                    Assertions.assertEquals(t2, body.get(1));
                    Assertions.assertEquals("63.50%", body.get(1).capaian());
                });
    }
}
