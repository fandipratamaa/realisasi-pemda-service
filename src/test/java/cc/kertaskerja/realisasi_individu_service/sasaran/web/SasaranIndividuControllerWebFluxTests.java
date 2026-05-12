package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividuService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(SasaranIndividuController.class)
public class SasaranIndividuControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SasaranIndividuService sasaranIndividuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnsSaveSasaranIndividu() throws Exception {
        SasaranIndividuRequest r1 = new SasaranIndividuRequest(null, "S1", "I1", "TAR-1", "100.0", 50.0, "unit1", "2025", "JANUARI", JenisRealisasi.NAIK, "198012312005011001", "(realisasi/target)*100", "SIMDA");
        SasaranIndividuRequest r2 = new SasaranIndividuRequest(null, "S2", "I2", "TAR-2", "200.0", 75.0, "unit2", "2026", "FEBRUARI", JenisRealisasi.TURUN, "198012312005011002", "(realisasi/target)*100", "SAKIP");

        SasaranIndividu s1 = SasaranIndividuService.buildUncheckedRealisasiSasaranIndividu(
                r1.renjaId(), r1.indikatorId(), r1.targetId(), r1.target(), r1.realisasi(),
                r1.satuan(), r1.tahun(), r1.bulan(), r1.jenisRealisasi(),
                r1.nip(), r1.rumusPerhitungan(), r1.sumberData()
        );
        SasaranIndividu s2 = SasaranIndividuService.buildUncheckedRealisasiSasaranIndividu(
                r2.renjaId(), r2.indikatorId(), r2.targetId(), r2.target(), r2.realisasi(),
                r2.satuan(), r2.tahun(), r2.bulan(), r2.jenisRealisasi(),
                r2.nip(), r2.rumusPerhitungan(), r2.sumberData()
        );

        when(sasaranIndividuService.batchSubmitRealisasiSasaranIndividu(anyList()))
                .thenReturn(Flux.just(s1, s2));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasaran_individu/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(SasaranIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(s1, body.get(0));
                    Assertions.assertEquals("50.00%", body.get(0).capaian());
                    Assertions.assertEquals(s2, body.get(1));
                    Assertions.assertEquals("63.50%", body.get(1).capaian());
                });
    }

    @Test
    void whenGetByTahunBulanNipAndRenjaId_thenReturnsSasaranIndividuList() {
        SasaranIndividu result = SasaranIndividuService.buildUncheckedRealisasiSasaranIndividu(
                "REN-001", "IND-001", "TAR-001", "100", 80.0,
                "%", "2025", "1", JenisRealisasi.NAIK,
                "198012312005011001", "(realisasi/target)*100", "SIMDA"
        );

        when(sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndNipAndRenjaId(
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(result));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/sasaran_individu/by-tahun/2025/by-bulan/1/by-nip/198012312005011001/by-id-renja/REN-001")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(SasaranIndividu.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(result, body.get(0));
                });
    }
}
