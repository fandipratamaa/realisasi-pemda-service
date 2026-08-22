package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.integration.penetapan.PenetapanSasaranPemdaClient;
import cc.kertaskerja.integration.penetapan.sasaran_pemda.PenetapanSasaranPemda;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.PenetapanSasaranPemdaListResponse;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SasaranServiceTests {

    @Mock
    private SasaranRepository sasaranRepository;

    @Mock
    private UploadClient uploadClient;

    @Mock
    private PenetapanSasaranPemdaClient penetapanClient;

    @Mock
    private FilePart filePart;

    @InjectMocks
    private SasaranService sasaranService;

    @Test
    void submitRealisasiSasaran_withId_existingFound_shouldUpdate() {
        SasaranRequest request = new SasaranRequest(
                1L, "SAS-1", "IND-1", "TAR-1", 50.0, "%",
                "2026", "1",
                JenisRealisasi.NAIK, "http://old-bukti", "Keterangan"
        );
        Sasaran existing = Sasaran.of(
                "SAS-1", "IND-1", "TAR-1", 40.0, "%", "2026", "1",
                "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );
        Sasaran updated = Sasaran.of(
                "SAS-1", "IND-1", "TAR-1", 50.0, "%", "2026", "1",
                "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );

        when(sasaranRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(sasaranRepository.save(any(Sasaran.class))).thenReturn(Mono.just(updated));

        Mono<Sasaran> result = sasaranService.submitRealisasiSasaran(request);

        StepVerifier.create(result)
                .expectNextMatches(s -> s.realisasi() == 50.0)
                .verifyComplete();

        verify(sasaranRepository).findById(1L);
        verify(sasaranRepository).save(any(Sasaran.class));
    }

    @Test
    void submitRealisasiSasaran_withoutId_existingNotFound_shouldSaveNew() {
        SasaranRequest request = new SasaranRequest(
                null, "SAS-1", "IND-1", "TAR-1", 50.0, "%",
                "2026", "1",
                JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );

        Sasaran baru = SasaranService.buildUnchekcedRealisasiSasaran(
                "SAS-1", "IND-1", "TAR-1", 50.0, "%", "2026", "1",
                JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );

        when(sasaranRepository.findFirstByKodeSasaranPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "SAS-1", "IND-1", "TAR-1", "2026", "1"
        )).thenReturn(Mono.empty());
        when(sasaranRepository.save(any(Sasaran.class))).thenReturn(Mono.just(baru));

        Mono<Sasaran> result = sasaranService.submitRealisasiSasaran(request);

        StepVerifier.create(result)
                .expectNextMatches(s -> s.buktiPendukung().equals("http://new-file.pdf"))
                .verifyComplete();

        verify(sasaranRepository).save(any(Sasaran.class));
    }

    @Test
    void uploadFile_shouldReturnUrl() {
        UploadClient.UploadMetadata metadata = new UploadClient.UploadMetadata(
                1, "key", "bucket", "name", "ext", "type", 1000L, "algo", "checksum", "cat", "vis", "http://new-file.pdf"
        );

        when(uploadClient.uploadFile(filePart)).thenReturn(Mono.just(metadata));

        Mono<String> result = sasaranService.uploadFile(filePart);

        StepVerifier.create(result)
                .expectNextMatches(url -> url.equals("http://new-file.pdf"))
                .verifyComplete();

        verify(uploadClient).uploadFile(filePart);
    }

    private PenetapanSasaranPemda.SasaranPenetapanPemdaData penetapan(
            String kodeSasaranPemda, List<PenetapanSasaranPemda.IndikatorSasaranPemdaData> indikators) {
        return new PenetapanSasaranPemda.SasaranPenetapanPemdaData(
                1L, kodeSasaranPemda, "Sasaran " + kodeSasaranPemda,
                "2025-2029", 2026, 1, false, indikators);
    }

    @Test
    void getPenetapanWithRealisasi_withoutBulan_indikatorsEmpty_shouldStillReturnSasaran() {
        PenetapanSasaranPemda.SasaranPenetapanPemdaData data = penetapan("SAS-1", List.of());
        when(penetapanClient.fetchSasaranPemda(2026)).thenReturn(Mono.just(List.of(data)));

        Mono<PenetapanSasaranPemdaListResponse> result = sasaranService.getPenetapanWithRealisasi(2026, null);

        StepVerifier.create(result)
                .expectNextMatches(res ->
                        res.tahunAktif() == 2026
                                && res.bulan() == null
                                && res.data().size() == 1
                                && res.data().getFirst().kodeSasaranPemda().equals("SAS-1")
                                && res.data().getFirst().indikators().isEmpty())
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_withoutBulan_indikatorsNull_shouldStillReturnSasaran() {
        PenetapanSasaranPemda.SasaranPenetapanPemdaData data = penetapan("SAS-1", null);
        when(penetapanClient.fetchSasaranPemda(2026)).thenReturn(Mono.just(List.of(data)));

        Mono<PenetapanSasaranPemdaListResponse> result = sasaranService.getPenetapanWithRealisasi(2026, null);

        StepVerifier.create(result)
                .expectNextMatches(res ->
                        res.data().size() == 1
                                && res.data().getFirst().kodeSasaranPemda().equals("SAS-1")
                                && res.data().getFirst().indikators().isEmpty())
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_withBulan_indikatorTargetsEmpty_shouldStillReturnIndikator() {
        PenetapanSasaranPemda.IndikatorSasaranPemdaData indKosong =
                new PenetapanSasaranPemda.IndikatorSasaranPemdaData(
                        20L, "IND-KOSONG", "Indikator Kosong", "rumus", "BPS", "definisi", 2026, List.of());
        PenetapanSasaranPemda.IndikatorSasaranPemdaData indNormal =
                new PenetapanSasaranPemda.IndikatorSasaranPemdaData(
                        21L, "IND-1", "Indikator 1", "rumus", "BPS", "definisi", 2026,
                        List.of(new PenetapanSasaranPemda.TargetSasaranPemdaData(10L, "TAR-1", "%", 2026, 100.0)));
        PenetapanSasaranPemda.SasaranPenetapanPemdaData data = penetapan("SAS-1", List.of(indKosong, indNormal));

        Sasaran realisasi = Sasaran.of(
                "SAS-1", "IND-1", "TAR-1", 80.0, "%", "2026", "3",
                "", "", JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "file.pdf", "keterangan");

        when(penetapanClient.fetchSasaranPemda(2026)).thenReturn(Mono.just(List.of(data)));
        when(sasaranRepository.findAllByTahunAndBulan("2026", "3")).thenReturn(Flux.just(realisasi));

        StepVerifier.create(sasaranService.getPenetapanWithRealisasi(2026, "3"))
                .expectNextMatches(res -> {
                    assertEquals(2026, res.tahunAktif());
                    assertEquals(3, res.bulan());

                    var sasaranRes = res.data().getFirst();
                    assertEquals(2, sasaranRes.indikators().size());

                    var kosong = sasaranRes.indikators().stream()
                            .filter(i -> i.kodeIndikator().equals("IND-KOSONG"))
                            .findFirst()
                            .orElseThrow();
                    assertTrue(kosong.targets().isEmpty());

                    var normal = sasaranRes.indikators().stream()
                            .filter(i -> i.kodeIndikator().equals("IND-1"))
                            .findFirst()
                            .orElseThrow();
                    assertEquals(80.0, normal.targets().getFirst().realisasi());
                    assertEquals(80.0, normal.targets().getFirst().capaian());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_withBulan_noRealisasiStored_targetFieldsShouldBeNull() {
        PenetapanSasaranPemda.SasaranPenetapanPemdaData data = penetapan("SAS-1",
                List.of(new PenetapanSasaranPemda.IndikatorSasaranPemdaData(
                        21L, "IND-1", "Indikator 1", "rumus", "BPS", "definisi", 2026,
                        List.of(new PenetapanSasaranPemda.TargetSasaranPemdaData(10L, "TAR-1", "%", 2026, 100.0)))));

        when(penetapanClient.fetchSasaranPemda(2026)).thenReturn(Mono.just(List.of(data)));
        when(sasaranRepository.findAllByTahunAndBulan("2026", "3")).thenReturn(Flux.empty());

        StepVerifier.create(sasaranService.getPenetapanWithRealisasi(2026, "3"))
                .expectNextMatches(res -> {
                    var target = res.data().getFirst().indikators().getFirst().targets().getFirst();
                    assertNull(target.realisasi());
                    assertNull(target.capaian());
                    assertNull(target.faktorPenunjang());
                    assertNull(target.buktiPendukung());
                    return true;
                })
                .verifyComplete();
    }
}