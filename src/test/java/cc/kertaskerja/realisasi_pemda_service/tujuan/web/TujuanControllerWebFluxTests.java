package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.Tujuan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanService;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanStatus;
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

@WebFluxTest(TujuanController.class)
public class TujuanControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TujuanService tujuanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenTujuanIdParamsExistsInByTahunShouldReturnRealisasiTujuanByTahunAndTujuanId() {
        List<Tujuan> mockTujuans = List.of(
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                        "TAR-1", "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED));
        when(tujuanService.getRealisasiTujuanByTahunAndTujuanId("2025", "TUJ-123"))
                .thenReturn(Flux.fromIterable(mockTujuans));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tujuans/by-tahun/2025")
                        .queryParam("tujuanId", "TUJ-123")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tujuan.class)
                .hasSize(1)
                .contains(mockTujuans.get(0));
    }

    @Test
    void whenNoTujuanIdShouldReutrnRealisasiTujuanByTahun() {
        List<Tujuan> mockTujuans = List.of(
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                       "TAR-1", "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED),
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                       "TAR-2", "100.0", 100.0, "%", "2026", "01", "Visi Misi 2", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED)
        );
        when(tujuanService.getRealisasiTujuanByTahun("2025"))
                .thenReturn(Flux.just(mockTujuans.get(0)));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/tujuans/by-tahun/2025")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tujuan.class)
                .hasSize(1)
                .contains(mockTujuans.get(0));
    }

    @Test
    void whenByIndikatorIdShouldReturnRealisasiTujuanByIndikatorId() {
        List<Tujuan> mockTujuans = List.of(
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                        "TAR-1", "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED));
        when(tujuanService.getRealisasiTujuanByIndikatorId("IND-TUJ-123"))
                .thenReturn(Flux.fromIterable(mockTujuans));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/tujuans/by-indikator/IND-TUJ-123")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tujuan.class)
                .hasSize(1)
                .contains(mockTujuans.get(0));
    }

    @Test
    void whenByPeriodeRpjmdShouldReturnRealisasiTujuanInTahunRpjmd() {
        List<Tujuan> mockTujuans = List.of(
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                      "TAR-1",  "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED),
                Tujuan.of("TUJ-123", "Test-Tujuan",
                        "IND-TUJ-123", "Produk-A",
                      "TAR-2",  "100.0", 100.0, "%", "2026", "01", "Visi Misi 2", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED),
                Tujuan.of("TUJ-12", "Test-Tujuan",
                        "IND-TUJ-124", "Produk-B",
                        "TAR-3", "4,70 - 4,75", 4.75, "%", "2025", "01", "Visi Misi 3", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED),
                Tujuan.of("TUJ-12", "Test-Tujuan",
                        "IND-TUJ-124", "Produk-B",
                      "TAR-4",  "4,85 - 4,90", 4.50, "%", "2027", "01", "Visi Misi 4", "(realisasi/target)*100",
                        JenisRealisasi.NAIK, TujuanStatus.UNCHECKED)
        );
        when(tujuanService.getRealisasiTujuanByPeriodeRpjmd("2025", "2030"))
                .thenReturn(Flux.fromIterable(mockTujuans));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .get()
                .uri("/tujuans/by-periode/2025/2030/rpjmd")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Tujuan.class)
                .hasSize(4)
                .contains(mockTujuans.get(0));
    }

    @Test
    void whenBatchSubmit_thenReturnsSavedTujuans() throws Exception {
        // prepare requests
TujuanRequest r1 = new TujuanRequest(null, "T1", "I1", "TAR-1", "100.0", 50.0, "unit1", "2025", "01", "Visi Misi 1", "(realisasi/target)*100", JenisRealisasi.NAIK);
        TujuanRequest r2 = new TujuanRequest(null, "T2", "I2", "TAR-2", "200.0", 75.0, "unit2", "2026", "01", "Visi Misi 2", "(realisasi/target)*100", JenisRealisasi.TURUN);

Tujuan t1 = TujuanService.buildUncheckedRealisasiTujuan(
                r1.tujuanId(), r1.indikatorId(), r1.targetId() ,r1.target(), r1.realisasi(),
                r1.satuan(), r1.tahun(), r1.bulan(), r1.visiMisi(), r1.rumusPerhitungan(), r1.jenisRealisasi()
        );
        Tujuan t2Baru = TujuanService.buildUncheckedRealisasiTujuan(
                r2.tujuanId(), r2.indikatorId(), r2.targetId(), r2.target(), r2.realisasi(),
                r2.satuan(), r2.tahun(), r2.bulan(), r2.visiMisi(), r2.rumusPerhitungan(), r2.jenisRealisasi()
        );

        when(tujuanService.batchSubmitRealisasiTujuan(anyList()))
                .thenReturn(Flux.just(t1, t2Baru));

        // execute POST /tujuans/batch
        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans/batch")
                .bodyValue(objectMapper.writeValueAsString(List.of(r1, r2)))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Tujuan.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(t1, body.get(0));
                    Assertions.assertEquals(t2Baru, body.get(1));
                });
    }
}
