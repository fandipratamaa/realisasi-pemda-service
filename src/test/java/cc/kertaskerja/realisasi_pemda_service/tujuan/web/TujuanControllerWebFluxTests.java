package cc.kertaskerja.realisasi_pemda_service.tujuan.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.Tujuan;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanService;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(TujuanController.class)
@Import(SecurityConfig.class)
public class TujuanControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TujuanService tujuanService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void whenSubmitRealisasiTujuan_thenReturnsTujuan() throws Exception {
        TujuanRequest request = new TujuanRequest(
                null, "TUJ-123", "IND-TUJ-123", "TAR-1", "100.0", 80.0, "%", "2025", "01",
                "Visi Misi 1", "(realisasi/target)*100", "BPS", JenisRealisasi.NAIK, "test.pdf", "keterangan bukti"
        );

        Tujuan updated = Tujuan.of("TUJ-123", "Realisasi Tujuan TUJ-123",
                "IND-TUJ-123", "Realisasi Indikator IND-TUJ-123",
                "TAR-1", "100.0", 80.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                "BPS", "", "", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "test.pdf", "keterangan bukti");

        when(tujuanService.submitRealisasiTujuan(any(TujuanRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tujuan.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("TUJ-123", body.tujuanId());
                    Assertions.assertEquals("test.pdf", body.buktiPendukung());
                    Assertions.assertEquals("keterangan bukti", body.keteranganBuktiPendukung());
                });
    }

    @Test
    void whenUploadFile_thenReturnsUrl() throws Exception {
        when(tujuanService.uploadFile(any())).thenReturn(Mono.just("test.pdf"));

        org.springframework.http.client.MultipartBodyBuilder builder = new org.springframework.http.client.MultipartBodyBuilder();
        builder.part("file", "test file content".getBytes())
                .filename("test.pdf");

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans/upload/file")
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                .body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.url").isEqualTo("test.pdf");
    }

    @Test
    void whenUpdateFaktorPenunjang_thenReturnsUpdatedTujuan() throws Exception {
        FaktorPenunjangRequest req = new FaktorPenunjangRequest(
                "TUJ-123", "IND-TUJ-123", "TAR-1", "2025", "01", "Kerjasama antar daerah");

        Tujuan updated = Tujuan.of("TUJ-123", "Realisasi Tujuan TUJ-123",
                "IND-TUJ-123", "Realisasi Indikator IND-TUJ-123",
                "TAR-1", "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                "BPS", "Kerjasama antar daerah", "", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "file.pdf", "bukti valid");

        when(tujuanService.updateFaktorPenunjang(any(FaktorPenunjangRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tujuan.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Kerjasama antar daerah", body.faktorPenunjang());
                });
    }

    @Test
    void whenUpdateFaktorPenghambat_thenReturnsUpdatedTujuan() throws Exception {
        FaktorPenghambatRequest req = new FaktorPenghambatRequest(
                "TUJ-123", "IND-TUJ-123", "TAR-1", "2025", "01", "Keterbatasan anggaran");

        Tujuan updated = Tujuan.of("TUJ-123", "Realisasi Tujuan TUJ-123",
                "IND-TUJ-123", "Realisasi Indikator IND-TUJ-123",
                "TAR-1", "100.0", 100.0, "%", "2025", "01", "Visi Misi 1", "(realisasi/target)*100",
                "BPS", "", "Keterbatasan anggaran", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "file.pdf", "bukti valid");

        when(tujuanService.updateFaktorPenghambat(any(FaktorPenghambatRequest.class)))
                .thenReturn(Mono.just(updated));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans/faktor-penghambat")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tujuan.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    Assertions.assertNotNull(body);
                    Assertions.assertEquals("Keterbatasan anggaran", body.faktorPenghambat());
                });
    }

    @Test
    void whenUpdateFaktorPenunjangAndNotFound_then404() throws Exception {
        FaktorPenunjangRequest req = new FaktorPenunjangRequest(
                "TUJ-XX", "IND-XX", "TAR-X", "2099", "01", "Faktor X");

        when(tujuanService.updateFaktorPenunjang(any(FaktorPenunjangRequest.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Tujuan tidak ditemukan")));

        webTestClient
                .mutateWith(csrf())
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .post()
                .uri("/tujuans/faktor-penunjang")
                .bodyValue(objectMapper.writeValueAsString(req))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isNotFound();
    }
}
