package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.TujuanRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TujuanServiceTests {
    @Mock
    private TujuanRepository tujuanRepository;

    @Mock
    private UploadClient uploadClient;

    @Mock
    private FilePart filePart;

    @InjectMocks
    private TujuanService tujuanService;

    @Test
    void submitRealisasiTujuan_withId_existingFound_shouldUpdate() {
        TujuanRequest request = new TujuanRequest(
                1L, "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Visi", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://old-bukti", "Keterangan"
        );
        Tujuan existing = Tujuan.of(
                "TUJ-1", "Tujuan Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 40.0, "%", "2026", "1",
                "Visi Lama", "Rumus Lama", "Sumber Lama", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );
        Tujuan updated = Tujuan.of(
                "TUJ-1", "Tujuan Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Visi", "Rumus", "Sumber", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );

        when(tujuanRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(tujuanRepository.save(any(Tujuan.class))).thenReturn(Mono.just(updated));

        Mono<Tujuan> result = tujuanService.submitRealisasiTujuan(request);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.realisasi() == 50.0)
                .verifyComplete();

        verify(tujuanRepository).findById(1L);
        verify(tujuanRepository).save(any(Tujuan.class));
    }

    @Test
    void submitRealisasiTujuan_withId_existingNotFound_shouldCreate() {
        TujuanRequest request = new TujuanRequest(
                1L, "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Visi", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://bukti", "Keterangan"
        );
        Tujuan baru = TujuanService.buildUncheckedRealisasiTujuan(
                "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Visi", "Rumus", "Sumber", JenisRealisasi.NAIK, "http://bukti", "Keterangan"
        );

        when(tujuanRepository.findById(1L)).thenReturn(Mono.empty());
        when(tujuanRepository.save(any(Tujuan.class))).thenReturn(Mono.just(baru));

        Mono<Tujuan> result = tujuanService.submitRealisasiTujuan(request);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.realisasi() == 50.0 && t.tujuanId().equals("TUJ-1"))
                .verifyComplete();

        verify(tujuanRepository).findById(1L);
        verify(tujuanRepository).save(any(Tujuan.class));
    }

    @Test
    void submitRealisasiTujuan_withoutId_existingFound_shouldUpdate() {
        TujuanRequest request = new TujuanRequest(
                null, "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Visi", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://old-bukti", "Keterangan"
        );
        Tujuan existing = Tujuan.of(
                "TUJ-1", "Tujuan Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 40.0, "%", "2026", "1",
                "Visi Lama", "Rumus Lama", "Sumber Lama", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );
        Tujuan updated = Tujuan.of(
                "TUJ-1", "Tujuan Lama", "IND-1", "Indikator Lama", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Visi", "Rumus", "Sumber", "Faktor Penunjang", "Faktor Penghambat", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "http://old-bukti", "Keterangan"
        );

        when(tujuanRepository.findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                "TUJ-1", "IND-1", "TAR-1", "2026", "1"
        )).thenReturn(Mono.just(existing));
        when(tujuanRepository.save(any(Tujuan.class))).thenReturn(Mono.just(updated));

        Mono<Tujuan> result = tujuanService.submitRealisasiTujuan(request);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.realisasi() == 50.0)
                .verifyComplete();

        verify(tujuanRepository).findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan("TUJ-1", "IND-1", "TAR-1", "2026", "1");
        verify(tujuanRepository).save(any(Tujuan.class));
    }

    @Test
    void submitRealisasiTujuan_withoutId_existingNotFound_shouldSaveNew() {
        TujuanRequest request = new TujuanRequest(
                null, "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%",
                "2026", "1", "Visi", "Rumus", "Sumber",
                JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );
        Tujuan baru = TujuanService.buildUncheckedRealisasiTujuan(
                "TUJ-1", "IND-1", "TAR-1", "100", 50.0, "%", "2026", "1",
                "Visi", "Rumus", "Sumber", JenisRealisasi.NAIK, "http://new-file.pdf", "Keterangan"
        );

        when(tujuanRepository.findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
                "TUJ-1", "IND-1", "TAR-1", "2026", "1"
        )).thenReturn(Mono.empty());
        when(tujuanRepository.save(any(Tujuan.class))).thenReturn(Mono.just(baru));

        Mono<Tujuan> result = tujuanService.submitRealisasiTujuan(request);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.buktiPendukung().equals("http://new-file.pdf"))
                .verifyComplete();

        verify(tujuanRepository).save(any(Tujuan.class));
    }

    @Test
    void uploadFile_shouldReturnUrl() {
        UploadClient.UploadMetadata metadata = new UploadClient.UploadMetadata(
                1, "key", "bucket", "name", "ext", "type", 1000L, "algo", "checksum", "cat", "vis", "http://new-file.pdf"
        );

        when(uploadClient.uploadFile(filePart)).thenReturn(Mono.just(metadata));

        Mono<String> result = tujuanService.uploadFile(filePart);

        StepVerifier.create(result)
                .expectNextMatches(url -> url.equals("http://new-file.pdf"))
                .verifyComplete();

        verify(uploadClient).uploadFile(filePart);
    }
}
