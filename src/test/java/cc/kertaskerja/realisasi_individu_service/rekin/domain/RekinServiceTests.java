package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRekinIndividuClient;
import cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinResponse;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RekinServiceTests {
    @Mock
    private RekinIndividuRepository repository;

    @Mock
    private PenetapanRekinIndividuClient penetapanClient;

    @InjectMocks
    private RekinService rekinService;

    private final RekinRequest req = new RekinRequest(
            "1.01.0.00.0.00.01.0000",
            "198012312005011001",
            "REKIN-001",
            "SAS-001",
            "IND-REKIN-001",
            "TAR-1",
            new BigDecimal("75.5"),
            JenisRealisasi.NAIK,
            "2026",
            "1"
    );

    private PenetapanRekinIndividu.RekinIndividuData createPenetapanData(Double targetValue) {
        var target = new PenetapanRekinIndividu.TargetRekinData(1L, "TAR-1", 2026, targetValue, "%");
        var indikator = new PenetapanRekinIndividu.IndikatorRekinData(1L, "IND-REKIN-001", "Indikator test", List.of(target));
        var rekin = new PenetapanRekinIndividu.RekinData(1L, 1, null, "REKIN-001", "Rekin test", null, 1, List.of(indikator));
        return new PenetapanRekinIndividu.RekinIndividuData("198012312005011001", "Test User", "1.01.0.00.0.00.01.0000", 2026, List.of(rekin));
    }

    @Test
    void whenCreateRekinNotExists_thenCreates() {
        when(repository.findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(repository.save(ArgumentMatchers.any(RekinIndividu.class)))
                .thenAnswer(invocation -> {
                    RekinIndividu r = invocation.getArgument(0);
                    return Mono.just(new RekinIndividu(
                            1L, r.kodeOpd(), r.nip(), r.tahun(), r.bulan(),
                            r.kodePkRekin(), r.kodeIndikatorPkRekin(), r.kodeTargetPkRekin(),
                            r.kodeSasaranOpd(),
                            r.realisasi(), r.jenisRealisasi(), r.faktorPenunjang(), r.faktorPenghambat(),
                            null, null, null, null));
                });
        when(penetapanClient.fetchRekinIndividu(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(createPenetapanData(100.0)));

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(r ->
                        r.id().equals(1L)
                                && r.kodePkRekin().equals(req.kodePkRekin())
                                && r.realisasi().compareTo(req.realisasi()) == 0
                                && r.jenisRealisasi() == JenisRealisasi.NAIK
                                && r.capaian() != null
                                && Double.compare(r.capaian(), 75.5) == 0
                                && r.keteranganCapaian() == null)
                .verifyComplete();

        verify(repository, times(1)).save(any(RekinIndividu.class));
    }

    @Test
    void whenCreateRekinExists_thenUpdates() {
        RekinIndividu existing = new RekinIndividu(
                99L, "1.01.0.00.0.00.01.0000", "198012312005011001", "2025", "1",
                "REKIN-001", "IND-REKIN-001", "TAR-1", null,
                new BigDecimal("50.0"), JenisRealisasi.NAIK, "", "",
                null, null, null, null);

        when(repository.findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(existing));
        when(repository.save(ArgumentMatchers.any(RekinIndividu.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(penetapanClient.fetchRekinIndividu(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(createPenetapanData(100.0)));

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(r ->
                        r.id().equals(99L)
                                && r.realisasi().compareTo(new BigDecimal("75.5")) == 0
                                && r.capaian() != null
                                && Double.compare(r.capaian(), 75.5) == 0
                                && r.keteranganCapaian() == null)
                .verifyComplete();

        verify(repository, times(1)).save(any(RekinIndividu.class));
    }

    @Test
    void whenPenetapanEmpty_thenReturnsResponseWithoutCapaian() {
        when(repository.findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(repository.save(ArgumentMatchers.any(RekinIndividu.class)))
                .thenAnswer(invocation -> {
                    RekinIndividu r = invocation.getArgument(0);
                    return Mono.just(new RekinIndividu(
                            1L, r.kodeOpd(), r.nip(), r.tahun(), r.bulan(),
                            r.kodePkRekin(), r.kodeIndikatorPkRekin(), r.kodeTargetPkRekin(),
                            r.kodeSasaranOpd(),
                            r.realisasi(), r.jenisRealisasi(), r.faktorPenunjang(), r.faktorPenghambat(),
                            null, null, null, null));
                });
        when(penetapanClient.fetchRekinIndividu(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.empty());

        var result = rekinService.createRekin(req);

        StepVerifier.create(result)
                .expectNextMatches(r ->
                        r.id().equals(1L)
                                && r.kodePkRekin().equals(req.kodePkRekin())
                                && r.realisasi().compareTo(req.realisasi()) == 0
                                && r.capaian() == null
                                && r.keteranganCapaian() == null)
                .verifyComplete();
    }

    @Test
    void whenUpdateFaktorPenunjang_thenUpdates() {
        var faktorReq = new FaktorPenunjangRekinRequest(
                "1.01.0.00.0.00.01.0000", "198012312005011001",
                "2026", "1", "REKIN-001", "IND-REKIN-001", "TAR-1",
                "Kerjasama tim");

        RekinIndividu existing = new RekinIndividu(
                1L, "1.01.0.00.0.00.01.0000", "198012312005011001", "2026", "1",
                "REKIN-001", "IND-REKIN-001", "TAR-1", null,
                new BigDecimal("75.5"), JenisRealisasi.NAIK, "", "",
                null, null, null, null);

        when(repository.findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(existing));
        when(repository.save(ArgumentMatchers.any(RekinIndividu.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = rekinService.updateFaktorPenunjang(faktorReq);

        StepVerifier.create(result)
                .expectNextMatches(r ->
                        r.faktorPenunjang().equals("Kerjasama tim")
                                && r.faktorPenghambat().equals(""))
                .verifyComplete();
    }

    @Test
    void whenUpdateFaktorPenghambatNotFound_thenError() {
        var faktorReq = new FaktorPenghambatRekinRequest(
                "1.01.0.00.0.00.01.0000", "198012312005011001",
                "2026", "1", "REKIN-001", "IND-REKIN-001", "TAR-1",
                "Perubahan prioritas");

        when(repository.findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        var result = rekinService.updateFaktorPenghambat(faktorReq);

        StepVerifier.create(result)
                .expectErrorMatches(e ->
                        e instanceof org.springframework.web.server.ResponseStatusException
                                && ((org.springframework.web.server.ResponseStatusException) e).getStatusCode().value() == 404)
                .verify();
    }

    @Test
    void whenGetAllByKodeOpdAndTahunAndBulan_thenReturnsList() {
        String kodeOpd = "1.01.0.00.0.00.01.0000";
        String tahun = "2026";
        String bulan = "1";

        RekinIndividu r1 = RekinIndividu.of(
                kodeOpd, "198012312005011001", "2026", "1",
                "REKIN-001", "IND-REKIN-001", "TAR-1", "SAS-001",
                BigDecimal.valueOf(75.5), JenisRealisasi.NAIK, "", "");

        when(repository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan))
                .thenReturn(Flux.just(r1));

        var result = rekinService.getAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);

        StepVerifier.create(result)
                .expectNext(r1)
                .verifyComplete();

        verify(repository, times(1)).findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }
}
