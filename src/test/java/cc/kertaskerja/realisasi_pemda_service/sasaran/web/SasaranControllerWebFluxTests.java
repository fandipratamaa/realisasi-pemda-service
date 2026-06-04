package cc.kertaskerja.realisasi_pemda_service.sasaran.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.Sasaran;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.SasaranService;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.SasaranStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@WebFluxTest(SasaranController.class)
public class SasaranControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SasaranService sasaranService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenBatchSubmit_thenReturnSavedSasarans() throws Exception {
        // prepare data
        SasaranRequest s1 = new SasaranRequest(null, "S-1", "IS-1", "TIS-1",
                "10", 10.0, "%", "2025", "01", "(realisasi/target)*100", "BPS", JenisRealisasi.NAIK);
        SasaranRequest s2 = new SasaranRequest(null, "S-12", "IS-12", "TIS-12",
                "10", 5.0, "%", "2025", "01", "(realisasi/target)*100", "BPS", JenisRealisasi.NAIK);

        Sasaran ss1 = SasaranService.buildUnchekcedRealisasiSasaran(
                s1.sasaranId(), s1.indikatorId(), s1.targetId(),
                s1.target(), s1.realisasi(), s1.satuan(), s1.tahun(), s1.bulan(), s1.rumusPerhitungan(), s1.sumberData(), s1.jenisRealisasi()
        );

        Sasaran ss2 = SasaranService.buildUnchekcedRealisasiSasaran(
                s2.sasaranId(), s2.indikatorId(), s2.targetId(),
                s2.target(), s2.realisasi(), s2.satuan(), s2.tahun(), s2.bulan(), s2.rumusPerhitungan(), s2.sumberData(), s2.jenisRealisasi()
        );

        when(sasaranService.batchSubmitRealisasiSasaran(anyList()))
                .thenReturn(Flux.just(ss1, ss2));

        // execute
        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .post()
                .uri("/sasarans/batch")
                .header("Content-Type", "application/json")
                .bodyValue(objectMapper.writeValueAsString(List.of(ss1, ss2)))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Sasaran.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(2, body.size());
                    Assertions.assertEquals(ss1, body.get(0));
                    Assertions.assertEquals(ss2, body.get(1));
                });

    }

    @Test
    void whenGetByTahunAndBulan_thenReturnSasarans() throws Exception {
        String tahun = "2025";
        String bulan = "01";

        Sasaran ss = SasaranService.buildUnchekcedRealisasiSasaran(
                "S-1", "IS-1", "TIS-1",
                "10", 10.0, "%", tahun, bulan, "(realisasi/target)*100", "BPS", JenisRealisasi.NAIK
        );

        when(sasaranService.getAllRealisasiSasaranByTahunAndBulan(anyString(), anyString()))
                .thenReturn(Flux.just(ss));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .get()
                .uri("/sasarans/by-tahun/{tahun}/by-bulan/{bulan}", tahun, bulan)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Sasaran.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals(1, body.size());
                    Assertions.assertEquals(tahun, body.get(0).tahun());
                    Assertions.assertEquals(bulan, body.get(0).bulan());
                });
    }

    @Test
    void whenUpdateFaktorPenunjang_thenReturnsUpdatedSasaran() throws Exception {
        FaktorPenunjangSasaranRequest req = new FaktorPenunjangSasaranRequest(
                "SAS-001", "IND-SAS-123", "TAR-1", "2025", "01", "Kerjasama antar daerah");

        Sasaran updated = Sasaran.of("SAS-001", "Realisasi Sasaran SAS-001",
                "IND-SAS-123", "Realisasi Indikator IND-SAS-123",
                "TAR-1", "100.0", 100.0, "%", "2025", "01",
                "(realisasi/target)*100", "BPS",
                "Kerjasama antar daerah", "Keterbatasan anggaran",
                JenisRealisasi.NAIK, SasaranStatus.UNCHECKED);

        when(sasaranService.updateFaktorPenunjang(
                "SAS-001", "IND-SAS-123", "TAR-1", "2025", "01", "Kerjasama antar daerah"))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasarans/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sasaran.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Kerjasama antar daerah", body.faktorPenunjang());
                });
    }

    @Test
    void whenUpdateFaktorPenghambat_thenReturnsUpdatedSasaran() throws Exception {
        FaktorPenghambatSasaranRequest req = new FaktorPenghambatSasaranRequest(
                "SAS-001", "IND-SAS-123", "TAR-1", "2025", "01", "Keterbatasan anggaran");

        Sasaran updated = Sasaran.of("SAS-001", "Realisasi Sasaran SAS-001",
                "IND-SAS-123", "Realisasi Indikator IND-SAS-123",
                "TAR-1", "100.0", 100.0, "%", "2025", "01",
                "(realisasi/target)*100", "BPS",
                "Kerjasama antar daerah", "Keterbatasan anggaran",
                JenisRealisasi.NAIK, SasaranStatus.UNCHECKED);

        when(sasaranService.updateFaktorPenghambat(
                "SAS-001", "IND-SAS-123", "TAR-1", "2025", "01", "Keterbatasan anggaran"))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasarans/faktor-penghambat")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sasaran.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Keterbatasan anggaran", body.faktorPenghambat());
                });
    }

    @Test
    void whenUpdateFaktorPenunjangAndNotFound_then404() throws Exception {
        FaktorPenunjangSasaranRequest req = new FaktorPenunjangSasaranRequest(
                "SAS-XX", "IND-XX", "TAR-X", "2099", "01", "Faktor X");

        when(sasaranService.updateFaktorPenunjang(
                "SAS-XX", "IND-XX", "TAR-X", "2099", "01", "Faktor X"))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sasaran tidak ditemukan")));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasarans/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }
}
