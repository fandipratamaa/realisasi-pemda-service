package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekinRepository;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.indikator.IndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.target.TargetIndikatorSasaranOpdRepository;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RekinServiceTests {
    @Mock
    private RekinRepository rekinRepository;

    @Mock
    private IndikatorRekinRepository indikatorRekinRepository;

    @Mock
    private TargetIndikatorRekinRepository targetIndikatorRekinRepository;

    @Mock
    private SasaranOpdRepository sasaranOpdRepository;

    @Mock
    private IndikatorSasaranOpdRepository indikatorSasaranOpdRepository;

    @Mock
    private TargetIndikatorSasaranOpdRepository targetIndikatorSasaranOpdRepository;

    @InjectMocks
    private RekinService rekinService;

    private IndikatorRekin buildSavedIndikator(Long rekinId, String kodeOpd, String nip, String tahun, String bulan) {
        return new IndikatorRekin(
                100L, rekinId, "IND-REKIN-001", "Realisasi Indikator IND-REKIN-001",
                kodeOpd, nip, tahun, bulan,
                null, null, null, null);
    }

    private TargetIndikatorRekin buildSavedTarget(Long indikatorRekinId, String kodeOpd, String nip, String tahun, String bulan) {
        return new TargetIndikatorRekin(
                200L, indikatorRekinId, "TAR-1",
                kodeOpd, nip, tahun, bulan,
                new BigDecimal("100.0"), new BigDecimal("75.5"), JenisRealisasi.NAIK,
                "", "", null, null, null, null);
    }

    private void stubSasaranSync() {
        when(targetIndikatorSasaranOpdRepository.findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorSasaranOpdAndKodeTargetSasaranOpdAndTahunAndBulan(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
    }

    @Test
    void whenCreateRekinWithoutIdAndNotExists_thenCreates() {
        RekinRequest req = new RekinRequest(
                null,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2026",
                "1"
        );

        when(rekinRepository.findFirstByNipAndTahunAndBulanAndKodeRekin(
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> {
                    Rekin r = invocation.getArgument(0);
                    return Mono.just(new Rekin(
                            1L, r.kodeOpd(), r.nip(), r.kodeRekin(), r.kodeSasaranOpd(), r.rekin(),
                            r.tahun(), r.bulan(), r.status(),
                            null, null, null, null));
                });

        IndikatorRekin savedInd = buildSavedIndikator(1L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());
        TargetIndikatorRekin savedTarget = buildSavedTarget(100L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());

        when(indikatorRekinRepository.findAllByRekinId(anyLong()))
                .thenReturn(Flux.empty(), Flux.just(savedInd));
        when(indikatorRekinRepository.save(ArgumentMatchers.any(IndikatorRekin.class)))
                .thenReturn(Mono.just(savedInd));
        when(targetIndikatorRekinRepository.findAllByIndikatorRekinIdIn(anyList()))
                .thenReturn(Flux.just(savedTarget));
        when(targetIndikatorRekinRepository.save(ArgumentMatchers.any(TargetIndikatorRekin.class)))
                .thenReturn(Mono.just(savedTarget));

        stubSasaranSync();

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.rekin().id().equals(1L)
                                && details.rekin().kodeRekin().equals(req.kodeRekin())
                                && details.rekin().rekin().equals("Realisasi Rekin " + req.kodeRekin())
                                && details.rekin().status() == RekinStatus.UNCHECKED
                                && details.indikators().size() == 1
                                && details.targets().size() == 1)
                .verifyComplete();

        verify(rekinRepository, times(1))
                .findFirstByNipAndTahunAndBulanAndKodeRekin(anyString(), anyString(), anyString(), anyString());
        verify(rekinRepository, times(1)).save(any(Rekin.class));
        verify(indikatorRekinRepository, times(1)).save(any(IndikatorRekin.class));
        verify(targetIndikatorRekinRepository, times(1)).save(any(TargetIndikatorRekin.class));
    }

    @Test
    void whenCreateRekinWithoutIdAndExistsByCompositeKey_thenUpdates() {
        Rekin existing = new Rekin(
                99L, "1.01.0.00.0.00.01.0000", "198012312005011001",
                "REKIN-001", "SAS-001", "Realisasi Rekin REKIN-001", "2025", "1", RekinStatus.UNCHECKED,
                null, null, null, null);

        RekinRequest req = new RekinRequest(
                null,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2026",
                "2"
        );

        when(rekinRepository.findFirstByNipAndTahunAndBulanAndKodeRekin(
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(existing));
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> {
                    Rekin r = invocation.getArgument(0);
                    return Mono.just(r);
                });

        IndikatorRekin savedInd = buildSavedIndikator(99L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());
        TargetIndikatorRekin savedTarget = buildSavedTarget(100L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());

        when(indikatorRekinRepository.findAllByRekinId(anyLong()))
                .thenReturn(Flux.empty(), Flux.just(savedInd));
        when(indikatorRekinRepository.save(ArgumentMatchers.any(IndikatorRekin.class)))
                .thenReturn(Mono.just(savedInd));
        when(targetIndikatorRekinRepository.findAllByIndikatorRekinIdIn(anyList()))
                .thenReturn(Flux.just(savedTarget));
        when(targetIndikatorRekinRepository.save(ArgumentMatchers.any(TargetIndikatorRekin.class)))
                .thenReturn(Mono.just(savedTarget));

        stubSasaranSync();

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.rekin().kodeOpd().equals(req.kodeOpd())
                                && details.rekin().tahun().equals(req.tahun())
                                && details.rekin().bulan().equals(req.bulan())
                                && details.rekin().status() == RekinStatus.UNCHECKED
                                && details.rekin().rekin().equals("Realisasi Rekin REKIN-001"))
                .verifyComplete();

        verify(rekinRepository, times(1))
                .findFirstByNipAndTahunAndBulanAndKodeRekin(anyString(), anyString(), anyString(), anyString());
        verify(rekinRepository, times(1)).save(any(Rekin.class));
    }

    @Test
    void whenCreateRekinWithIdAndExists_thenUpdates() {
        Rekin existing = new Rekin(
                99L, "1.01.0.00.0.00.01.0000", "198012312005011001",
                "REKIN-001", "SAS-001", "Realisasi Rekin REKIN-001", "2025", "1", RekinStatus.CHECKED,
                "user1", "user1", null, null);

        RekinRequest req = new RekinRequest(
                99L,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2026",
                "2"
        );

        when(rekinRepository.findById(99L))
                .thenReturn(Mono.just(existing));
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> {
                    Rekin r = invocation.getArgument(0);
                    return Mono.just(r);
                });

        IndikatorRekin savedInd = buildSavedIndikator(99L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());
        TargetIndikatorRekin savedTarget = buildSavedTarget(100L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());

        when(indikatorRekinRepository.findAllByRekinId(anyLong()))
                .thenReturn(Flux.empty(), Flux.just(savedInd));
        when(indikatorRekinRepository.save(ArgumentMatchers.any(IndikatorRekin.class)))
                .thenReturn(Mono.just(savedInd));
        when(targetIndikatorRekinRepository.findAllByIndikatorRekinIdIn(anyList()))
                .thenReturn(Flux.just(savedTarget));
        when(targetIndikatorRekinRepository.save(ArgumentMatchers.any(TargetIndikatorRekin.class)))
                .thenReturn(Mono.just(savedTarget));

        stubSasaranSync();

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.rekin().id().equals(99L)
                                && details.rekin().tahun().equals(req.tahun())
                                && details.rekin().bulan().equals(req.bulan())
                                && details.rekin().status() == RekinStatus.UNCHECKED
                                && details.rekin().rekin().equals("Realisasi Rekin REKIN-001")
                                && details.rekin().createdBy().equals("user1"))
                .verifyComplete();

        verify(rekinRepository, times(1)).findById(99L);
        verify(rekinRepository, times(1)).save(any(Rekin.class));
    }

    @Test
    void whenCreateRekinWithIdAndNotExists_thenCreates() {
        RekinRequest req = new RekinRequest(
                99L,
                "1.01.0.00.0.00.01.0000",
                "198012312005011001",
                "REKIN-001",
                "SAS-001",
                "IND-REKIN-001",
                "TAR-1",
                new BigDecimal("100.0"),
                new BigDecimal("75.5"),
                JenisRealisasi.NAIK,
                "2026",
                "1"
        );

        when(rekinRepository.findById(99L))
                .thenReturn(Mono.empty());
        when(rekinRepository.findFirstByNipAndTahunAndBulanAndKodeRekin(
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(rekinRepository.save(ArgumentMatchers.any(Rekin.class)))
                .thenAnswer(invocation -> {
                    Rekin r = invocation.getArgument(0);
                    return Mono.just(new Rekin(
                            1L, r.kodeOpd(), r.nip(), r.kodeRekin(), r.kodeSasaranOpd(), r.rekin(),
                            r.tahun(), r.bulan(), r.status(),
                            null, null, null, null));
                });

        IndikatorRekin savedInd = buildSavedIndikator(1L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());
        TargetIndikatorRekin savedTarget = buildSavedTarget(100L, req.kodeOpd(), req.nip(), req.tahun(), req.bulan());

        when(indikatorRekinRepository.findAllByRekinId(anyLong()))
                .thenReturn(Flux.empty(), Flux.just(savedInd));
        when(indikatorRekinRepository.save(ArgumentMatchers.any(IndikatorRekin.class)))
                .thenReturn(Mono.just(savedInd));
        when(targetIndikatorRekinRepository.findAllByIndikatorRekinIdIn(anyList()))
                .thenReturn(Flux.just(savedTarget));
        when(targetIndikatorRekinRepository.save(ArgumentMatchers.any(TargetIndikatorRekin.class)))
                .thenReturn(Mono.just(savedTarget));

        stubSasaranSync();

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.rekin().id().equals(1L)
                                && details.rekin().kodeRekin().equals(req.kodeRekin())
                                && details.rekin().rekin().equals("Realisasi Rekin " + req.kodeRekin())
                                && details.rekin().status() == RekinStatus.UNCHECKED)
                .verifyComplete();

        verify(rekinRepository, times(1)).findById(99L);
        verify(rekinRepository, times(1)).save(any(Rekin.class));
    }

    @Test
    void whenGetRekinByNipAndTahunAndBulan_thenReturnsList() {
        Rekin rekin = new Rekin(
                1L, "1.01.0.00.0.00.01.0000", "198012312005011001",
                "REKIN-001", "SAS-001", "Realisasi Rekin REKIN-001", "2026", "1", RekinStatus.UNCHECKED,
                null, null, null, null);

        when(rekinRepository.findAllByNipAndTahunAndBulan(anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(rekin));
        when(indikatorRekinRepository.findAllByRekinId(anyLong()))
                .thenReturn(Flux.empty());

        var result = rekinService.getRekinWithDetailsByNipAndTahunAndBulan(
                "198012312005011001", "2026", "1");

        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.rekin().nip().equals("198012312005011001")
                                && details.indikators().isEmpty()
                                && details.targets().isEmpty())
                .verifyComplete();

        verify(rekinRepository, times(1))
                .findAllByNipAndTahunAndBulan(anyString(), anyString(), anyString());
    }
}
