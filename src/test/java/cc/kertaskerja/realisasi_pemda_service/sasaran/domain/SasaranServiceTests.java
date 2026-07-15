package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.SasaranRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import cc.kertaskerja.integration.upload.UploadClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SasaranServiceTests {

    @Mock
    private SasaranRepository sasaranRepository;

    @Mock
    private UploadClient uploadClient;

    @Mock
    private FilePart filePart;

    @InjectMocks
    private SasaranService sasaranService;

    @Test
    void submitRealisasiSasaran_withId_existingFound_shouldUpdate() {
        SasaranRequest request = new SasaranRequest(
                1L, "SAS-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://old-bukti", "Keterangan"
        );
        Sasaran existing = Sasaran.of(
                "SAS-1", "Sasaran Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 40.0, "%", "2026", "1",
                "Rumus Lama", "Sumber Lama", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );
        Sasaran updated = Sasaran.of(
                "SAS-1", "Sasaran Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Rumus", "Sumber", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, SasaranStatus.UNCHECKED, "http://old-bukti", "Keterangan"
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
                null, "SAS-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );

        Sasaran baru = SasaranService.buildUnchekcedRealisasiSasaran(
                "SAS-1", "IND-1", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Rumus", "Sumber", JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );

        when(sasaranRepository.findFirstBySasaranIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
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
}