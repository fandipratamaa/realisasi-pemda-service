package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.integration.penetapan.PenetapanTujuanPemdaClient;
import cc.kertaskerja.integration.penetapan.tujuan_pemda.PenetapanTujuanPemda;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.PenetapanTujuanPemdaListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TujuanServiceTests {

    @Mock
    private TujuanRepository tujuanRepository;

    @Mock
    private UploadClient uploadClient;

    @Mock
    private PenetapanTujuanPemdaClient penetapanClient;

    @InjectMocks
    private TujuanService tujuanService;

    private PenetapanTujuanPemda.TujuanPenetapanPemdaData penetapan(
            String kodeTujuanPemda, List<PenetapanTujuanPemda.IndikatorPenetapanPemdaData> indikators) {
        return new PenetapanTujuanPemda.TujuanPenetapanPemdaData(
                1L, "Visi", "Misi", kodeTujuanPemda, "Tujuan " + kodeTujuanPemda,
                "2025-2029", 2026, 1, false, indikators);
    }

    @Test
    void getPenetapanWithRealisasi_withoutBulan_indikatorsEmpty_shouldStillReturnTujuan() {
        PenetapanTujuanPemda.TujuanPenetapanPemdaData data = penetapan("TUJ-1", List.of());
        when(penetapanClient.fetchTujuanPemda(2026)).thenReturn(Mono.just(List.of(data)));

        Mono<PenetapanTujuanPemdaListResponse> result = tujuanService.getPenetapanWithRealisasi(2026, null);

        StepVerifier.create(result)
                .expectNextMatches(res ->
                        res.tahun() == 2026
                                && res.bulan() == null
                                && res.tujuanPemdas().size() == 1
                                && res.tujuanPemdas().getFirst().kodeTujuanPemda().equals("TUJ-1")
                                && res.tujuanPemdas().getFirst().indikators().isEmpty())
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_withoutBulan_indikatorsNull_shouldStillReturnTujuan() {
        PenetapanTujuanPemda.TujuanPenetapanPemdaData data = penetapan("TUJ-1", null);
        when(penetapanClient.fetchTujuanPemda(2026)).thenReturn(Mono.just(List.of(data)));

        Mono<PenetapanTujuanPemdaListResponse> result = tujuanService.getPenetapanWithRealisasi(2026, null);

        StepVerifier.create(result)
                .expectNextMatches(res ->
                        res.tujuanPemdas().size() == 1
                                && res.tujuanPemdas().getFirst().kodeTujuanPemda().equals("TUJ-1")
                                && res.tujuanPemdas().getFirst().indikators().isEmpty())
                .verifyComplete();
    }

    @Test
    void getPenetapanWithRealisasi_withBulan_indikatorTargetsEmpty_shouldStillReturnIndikator() {
        PenetapanTujuanPemda.IndikatorPenetapanPemdaData indKosong =
                new PenetapanTujuanPemda.IndikatorPenetapanPemdaData(
                        20L, "IND-KOSONG", "Indikator Kosong", "rumus", "BPS", "definisi", 2026, List.of());
        PenetapanTujuanPemda.IndikatorPenetapanPemdaData indNormal =
                new PenetapanTujuanPemda.IndikatorPenetapanPemdaData(
                        21L, "IND-1", "Indikator 1", "rumus", "BPS", "definisi", 2026,
                        List.of(new PenetapanTujuanPemda.TargetPenetapanPemdaData(10L, "TAR-1", "%", 2026, 100.0)));
        PenetapanTujuanPemda.TujuanPenetapanPemdaData data = penetapan("TUJ-1", List.of(indKosong, indNormal));

        Tujuan realisasi = Tujuan.of(
                "TUJ-1", "IND-1", "TAR-1", 80.0, "%", "2026", "3",
                "", "", JenisRealisasi.NAIK, TujuanStatus.UNCHECKED, "file.pdf", "keterangan");

        when(penetapanClient.fetchTujuanPemda(2026)).thenReturn(Mono.just(List.of(data)));
        when(tujuanRepository.findAllByTahunAndBulan("2026", "3")).thenReturn(Flux.just(realisasi));

        StepVerifier.create(tujuanService.getPenetapanWithRealisasi(2026, "3"))
                .expectNextMatches(res -> {
                    assertEquals(2026, res.tahun());
                    assertEquals(3, res.bulan());

                    var tujuanRes = res.tujuanPemdas().getFirst();
                    assertEquals(2, tujuanRes.indikators().size());

                    var kosong = tujuanRes.indikators().stream()
                            .filter(i -> i.kodeIndikator().equals("IND-KOSONG"))
                            .findFirst()
                            .orElseThrow();
                    assertTrue(kosong.targets().isEmpty());

                    var normal = tujuanRes.indikators().stream()
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
        PenetapanTujuanPemda.TujuanPenetapanPemdaData data = penetapan("TUJ-1",
                List.of(new PenetapanTujuanPemda.IndikatorPenetapanPemdaData(
                        21L, "IND-1", "Indikator 1", "rumus", "BPS", "definisi", 2026,
                        List.of(new PenetapanTujuanPemda.TargetPenetapanPemdaData(10L, "TAR-1", "%", 2026, 100.0)))));

        when(penetapanClient.fetchTujuanPemda(2026)).thenReturn(Mono.just(List.of(data)));
        when(tujuanRepository.findAllByTahunAndBulan("2026", "3")).thenReturn(Flux.empty());

        StepVerifier.create(tujuanService.getPenetapanWithRealisasi(2026, "3"))
                .expectNextMatches(res -> {
                    var target = res.tujuanPemdas().getFirst().indikators().getFirst().targets().getFirst();
                    assertNull(target.realisasi());
                    assertNull(target.capaian());
                    assertNull(target.faktorPenunjang());
                    assertNull(target.buktiPendukung());
                    return true;
                })
                .verifyComplete();
    }
}
