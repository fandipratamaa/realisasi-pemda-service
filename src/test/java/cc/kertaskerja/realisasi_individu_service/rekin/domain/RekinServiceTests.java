package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RekinServiceTests {
    @Mock
    private RekinRepository rekinRepository;

    @InjectMocks
    private RekinService rekinService;

    @Test
    void whenBatchSubmitWithoutTargetIdAndExistingFound_thenUpdatesUsingFourKeys() {
        RekinRequest req = new RekinRequest(
                null,
                "REKIN-1",
                "Rekin A",
                "198012312005011001",
                "SAS-1",
                "Sasaran A",
                "IND-1",
                "Indikator A",
                "TAR-1",
                "100",
                55,
                "%",
                "2026",
                "Januari",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        Rekin existing = new Rekin(
                10L,
                "REKIN-1",
                "Rekin A",
                "IND-1",
                "Indikator A",
                "198012312005011001",
                "SAS-1",
                "Sasaran A",
                "TAR-1",
                "100",
                10,
                "old",
                "2026",
                "Desember",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK,
                RekinStatus.UNCHECKED,
                null,
                null,
                null,
                null,
                0
        );

        when(rekinRepository.findFirstByNipAndIdSasaranAndTahunAndRekinId(req.nip(), req.idSasaran(), req.tahun(), req.rekinId()))
                .thenReturn(Mono.just(existing));
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Flux<Rekin> result = rekinService.batchSubmitRealisasiRekin(List.of(req));

        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.id().equals(existing.id())
                                && saved.realisasi().equals(req.realisasi())
                                && saved.satuan().equals(req.satuan())
                                && saved.tahun().equals(req.tahun()))
                .verifyComplete();

        verify(rekinRepository, times(1))
                .findFirstByNipAndIdSasaranAndTahunAndRekinId(req.nip(), req.idSasaran(), req.tahun(), req.rekinId());
        verify(rekinRepository, times(1)).save(any(Rekin.class));
        verify(rekinRepository, never()).findById(anyLong());
    }

    @Test
    void whenBatchSubmitWithoutTargetIdAndNoExisting_thenInsertsNew() {
        RekinRequest req = new RekinRequest(
                null,
                "REKIN-2",
                "Rekin B",
                "198012312005011001",
                "SAS-2",
                "Sasaran B",
                "IND-2",
                "Indikator B",
                "TAR-2",
                "200",
                70,
                "%",
                "2026",
                "Februari",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        when(rekinRepository.findFirstByNipAndIdSasaranAndTahunAndRekinId(req.nip(), req.idSasaran(), req.tahun(), req.rekinId()))
                .thenReturn(Mono.empty());
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Flux<Rekin> result = rekinService.batchSubmitRealisasiRekin(List.of(req));

        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.id() == null
                                && saved.rekinId().equals(req.rekinId())
                                && saved.nip().equals(req.nip())
                                && saved.idSasaran().equals(req.idSasaran())
                                && saved.tahun().equals(req.tahun()))
                .verifyComplete();

        verify(rekinRepository, times(1))
                .findFirstByNipAndIdSasaranAndTahunAndRekinId(req.nip(), req.idSasaran(), req.tahun(), req.rekinId());
        verify(rekinRepository, times(1)).save(any(Rekin.class));
        verify(rekinRepository, never()).findById(anyLong());
    }

    @Test
    void whenBatchSubmitWithTargetId_thenUsesFindById() {
        RekinRequest req = new RekinRequest(
                99L,
                "REKIN-3",
                "Rekin C",
                "198012312005011001",
                "SAS-3",
                "Sasaran C",
                "IND-3",
                "Indikator C",
                "TAR-3",
                "300",
                80,
                "%",
                "2026",
                "Maret",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK
        );

        Rekin existing = new Rekin(
                99L,
                "REKIN-3",
                "Rekin C",
                "IND-3",
                "Indikator C",
                "198012312005011001",
                "SAS-3",
                "Sasaran C",
                "TAR-3",
                "300",
                10,
                "old",
                "2026",
                "Januari",
                "1.01.0.00.0.00.01.0000",
                JenisRealisasi.NAIK,
                RekinStatus.UNCHECKED,
                null,
                null,
                null,
                null,
                0
        );

        when(rekinRepository.findById(req.targetRealisasiId()))
                .thenReturn(Mono.just(existing));
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Flux<Rekin> result = rekinService.batchSubmitRealisasiRekin(List.of(req));

        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.id().equals(existing.id())
                                && saved.realisasi().equals(req.realisasi())
                                && saved.satuan().equals(req.satuan()))
                .verifyComplete();

        verify(rekinRepository, times(1)).findById(req.targetRealisasiId());
        verify(rekinRepository, times(1)).save(any(Rekin.class));
        verify(rekinRepository, never())
                .findFirstByNipAndIdSasaranAndTahunAndRekinId(anyString(), anyString(), anyString(), anyString());
    }
}
