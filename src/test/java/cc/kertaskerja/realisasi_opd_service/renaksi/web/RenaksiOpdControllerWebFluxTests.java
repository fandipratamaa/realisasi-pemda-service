package cc.kertaskerja.realisasi_opd_service.renaksi.web;

import cc.kertaskerja.config.SecurityConfig;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@WebFluxTest(RenaksiOpdController.class)
@Import(SecurityConfig.class)
class RenaksiOpdControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RenaksiOpdService renaksiOpdService;

    private static final String KODE_OPD = "8.01.0.00.0.00.01.0000";

    private static RenaksiOpdPenetapanResponse penetapanItem(
            RenaksiOpdPenetapanResponse.Realisasi realisasi) {
        return new RenaksiOpdPenetapanResponse(
                "REN-OPD-1",
                "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten",
                KODE_OPD,
                "SAS-OPD-5975",
                "REKIN-PEG-2026-19928",
                "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten",
                "198607022010011013",
                "",
                "",
                168340222L,
                2026,
                25, 25, 35, 15,
                realisasi
        );
    }

    private static PenetapanRenaksiOpdListResponse penetapanData() {
        return new PenetapanRenaksiOpdListResponse(
                KODE_OPD,
                2026,
                1,
                3,
                true,
                List.of(penetapanItem(
                        new RenaksiOpdPenetapanResponse.Realisasi(2026, 1, 100.0, 30.0, 30.0, null, null, null)))
        );
    }

    private static PenetapanRenaksiOpdListResponse penetapanDataWithoutBulan() {
        return new PenetapanRenaksiOpdListResponse(
                KODE_OPD,
                2026,
                null,
                3,
                true,
                List.of(penetapanItem(
                        new RenaksiOpdPenetapanResponse.Realisasi(2026, null, 100.0, 0.0, null, null, null, null)))
        );
    }

    @Test
    void whenAdminOpdGetsRenaksiOpdPenetapan_thenDataReturned() {
        when(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "1"))
                .thenReturn(Mono.just(penetapanData()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/penetapan?bulan=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.kode_opd").isEqualTo("8.01.0.00.0.00.01.0000")
                .jsonPath("$.tahun_aktif").isEqualTo(2026)
                .jsonPath("$.bulan").isEqualTo(1)
                .jsonPath("$.is_locked").isEqualTo(true)
                .jsonPath("$.renaksi_opds[0].kode_rencana_aksi_opd").isEqualTo("REN-OPD-1")
                .jsonPath("$.renaksi_opds[0].nama_renaksi")
                .isEqualTo("Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten")
                .jsonPath("$.renaksi_opds[0].kode_pk").isEqualTo("REKIN-PEG-2026-19928")
                .jsonPath("$.renaksi_opds[0].anggaran_renaksi").isEqualTo(168340222)
                .jsonPath("$.renaksi_opds[0].tw1").isEqualTo(25)
                .jsonPath("$.renaksi_opds[0].tw3").isEqualTo(35)
                .jsonPath("$.renaksi_opds[0].realisasi.tahun").isEqualTo(2026)
                .jsonPath("$.renaksi_opds[0].realisasi.bulan").isEqualTo(1)
                .jsonPath("$.renaksi_opds[0].realisasi.target").isEqualTo(100.0)
                .jsonPath("$.renaksi_opds[0].realisasi.realisasi").isEqualTo(30.0)
                .jsonPath("$.renaksi_opds[0].realisasi.capaian").isEqualTo(30.0);
    }

    @Test
    void whenBulanNotProvided_thenRealisasiObjectStillReturned() {
        when(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, null))
                .thenReturn(Mono.just(penetapanDataWithoutBulan()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/penetapan")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bulan").isEmpty()
                .jsonPath("$.renaksi_opds[0].kode_rencana_aksi_opd").isEqualTo("REN-OPD-1")
                .jsonPath("$.renaksi_opds[0].realisasi.tahun").isEqualTo(2026)
                .jsonPath("$.renaksi_opds[0].realisasi.bulan").isEmpty()
                .jsonPath("$.renaksi_opds[0].realisasi.target").isEqualTo(100.0)
                .jsonPath("$.renaksi_opds[0].realisasi.realisasi").isEqualTo(0.0)
                .jsonPath("$.renaksi_opds[0].realisasi.capaian").isEmpty();
    }

    @Test
    void whenBulanInvalid_thenBadRequest() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/penetapan?bulan=13")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenAdminOpdSyncsRenaksiOpd_thenDataReturned() {
        when(renaksiOpdService.syncPenetapanRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just("""
                        {"data":{"sync_id":1,"status":"SUCCESS","kode_opd":"8.01.0.00.0.00.01.0000",
                        "tahun":2026,"jenis_penetapan":"RENAKSI-OPD","processed_at":"2026-10-01T07:39:32.048888362Z",
                        "processed_summary":{"renaksi":0,"indikator":0,"target":0}}}
                        """));
        when(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, null))
                .thenReturn(Mono.just(penetapanDataWithoutBulan()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .post()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/sync/penetapan")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.kode_opd").isEqualTo(KODE_OPD)
                .jsonPath("$.tahun_aktif").isEqualTo(2026)
                .jsonPath("$.renaksi_opds[0].kode_rencana_aksi_opd").isEqualTo("REN-OPD-1");
    }

    @Test
    void whenAdminOpdSyncsRenaksiOpdWithBulan_thenDataReturned() {
        when(renaksiOpdService.syncPenetapanRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.just("""
                        {"data":{"sync_id":1,"status":"SUCCESS","kode_opd":"8.01.0.00.0.00.01.0000",
                        "tahun":2026,"jenis_penetapan":"RENAKSI-OPD","processed_at":"2026-10-01T07:39:32.048888362Z",
                        "processed_summary":{"renaksi":0,"indikator":0,"target":0}}}
                        """));
        when(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, "1"))
                .thenReturn(Mono.just(penetapanData()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .post()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/sync/penetapan?bulan=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bulan").isEqualTo(1)
                .jsonPath("$.renaksi_opds[0].realisasi.realisasi").isEqualTo(30.0);
    }

    @Test
    void whenSyncFails_thenStillReturnsPenetapanData() {
        // Client menelan error upstream menjadi Mono.empty(); response tetap 200 dengan data penetapan.
        when(renaksiOpdService.syncPenetapanRenaksiOpd(KODE_OPD, 2026))
                .thenReturn(Mono.empty());
        when(renaksiOpdService.getPenetapanWithRealisasi(KODE_OPD, 2026, null))
                .thenReturn(Mono.just(penetapanDataWithoutBulan()));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .post()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/sync/penetapan")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.renaksi_opds[0].kode_rencana_aksi_opd").isEqualTo("REN-OPD-1");
    }

    @Test
    void whenAdminOpdGetsLaporanRealisasi_thenDataReturned() {
        when(renaksiOpdService.getLaporanRealisasi(KODE_OPD, "2026", JenisLaporan.BULANAN, "1"))
                .thenReturn(Flux.just(new LaporanRealisasiRenaksiOpdResponse(
                        "2026", KODE_OPD, "REN-OPD-1",
                        "Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten",
                        "100.0", JenisLaporan.BULANAN, Map.of("1", 30.0), null)));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renaksi_opd/laporan/kodeOpd/8.01.0.00.0.00.01.0000/tahun/2026/jenisLaporan/BULANAN?bulan=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].tahun").isEqualTo("2026")
                .jsonPath("$[0].kode_opd").isEqualTo(KODE_OPD)
                .jsonPath("$[0].kode_rencana_aksi_opd").isEqualTo("REN-OPD-1")
                .jsonPath("$[0].nama_renaksi")
                .isEqualTo("Terlaksananya Koordinasi antar Pimpinan Daerah di Kabupaten")
                .jsonPath("$[0].target").isEqualTo("100.0")
                .jsonPath("$[0].jenis_laporan").isEqualTo("BULANAN")
                .jsonPath("$[0].list_data.1").isEqualTo(30.0)
                .jsonPath("$[0].totalRealisasi").doesNotExist();
    }

    @Test
    void whenLaporanBulanInvalid_thenBadRequest() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(new SimpleGrantedAuthority("admin_opd")))
                .get()
                .uri("/renaksi_opd/laporan/kodeOpd/8.01.0.00.0.00.01.0000/tahun/2026/jenisLaporan/BULANAN?bulan=13")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticated_thenLaporanIsUnauthorized() {
        webTestClient
                .get()
                .uri("/renaksi_opd/laporan/kodeOpd/8.01.0.00.0.00.01.0000/tahun/2026/jenisLaporan/TAHUNAN")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenUnauthenticated_thenSyncIsUnauthorized() {
        webTestClient
                .post()
                .uri("/renaksi_opd/8.01.0.00.0.00.01.0000/tahun/2026/sync/penetapan")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
