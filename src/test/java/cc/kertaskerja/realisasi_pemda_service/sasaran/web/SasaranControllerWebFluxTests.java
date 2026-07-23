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

import static org.mockito.ArgumentMatchers.any;
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
    void whenSubmitRealisasiSasaran_thenReturnsSasaran() throws Exception {
        SasaranRequest request = new SasaranRequest(
                null, "SAS-001", "IND-SAS-123", "TAR-1", 80.0, "%", "2025", "01",
                JenisRealisasi.NAIK, "test.pdf", "keterangan bukti"
        );

        Sasaran updated = Sasaran.of("SAS-001",
                "IND-SAS-123",
                "TAR-1", 80.0, "%", "2025", "01",
                "", "",
                JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "test.pdf", "keterangan bukti");

        when(sasaranService.submitRealisasiSasaran(any(SasaranRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasarans")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sasaran.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("SAS-001", body.kodeSasaranPemda());
                    Assertions.assertEquals("test.pdf", body.buktiPendukung());
                    Assertions.assertEquals("keterangan bukti", body.keteranganBuktiPendukung());
                });
    }

    @Test
    void whenUploadFile_thenReturnsUrl() throws Exception {
        when(sasaranService.uploadFile(any())).thenReturn(Mono.just("test.pdf"));

        org.springframework.http.client.MultipartBodyBuilder builder = new org.springframework.http.client.MultipartBodyBuilder();
        builder.part("file", "test file content".getBytes())
                .filename("test.pdf");

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/sasarans/upload/file")
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.url").isEqualTo("test.pdf");
    }



    @Test
    void whenUpdateFaktorPenunjang_thenReturnsUpdatedSasaran() throws Exception {
        FaktorPenunjangSasaranRequest req = new FaktorPenunjangSasaranRequest(
                "SAS-001", "IND-SAS-123", "TAR-1", "2025", "01", "Kerjasama antar daerah");

        Sasaran updated = Sasaran.of("SAS-001",
                "IND-SAS-123",
                "TAR-1", 100.0, "%", "2025", "01",
                "Kerjasama antar daerah", "Keterbatasan anggaran",
                JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "file.pdf", "bukti valid");

        when(sasaranService.updateFaktorPenunjang(any(FaktorPenunjangSasaranRequest.class)))
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

        Sasaran updated = Sasaran.of("SAS-001",
                "IND-SAS-123",
                "TAR-1", 100.0, "%", "2025", "01",
                "Kerjasama antar daerah", "Keterbatasan anggaran",
                JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "file.pdf", "bukti valid");

        when(sasaranService.updateFaktorPenghambat(any(FaktorPenghambatSasaranRequest.class)))
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

        when(sasaranService.updateFaktorPenunjang(any(FaktorPenunjangSasaranRequest.class)))
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
