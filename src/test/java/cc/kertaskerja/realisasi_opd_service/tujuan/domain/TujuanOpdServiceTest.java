package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TujuanOpdServiceTest {
    @Mock
    private TujuanOpdRepository tujuanOpdRepository;

    @InjectMocks
    private TujuanOpdService tujuanOpdService;

    @Test
    void submitRealisasiTujuanOpd_ShouldReturnSavedEntity_WhenValidInputProvided() {
        // Arrange
        String tujuanId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "1";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String kodeOpd = "OPD001";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";
        String definisiOperational = "Definisi indikator tujuan";

        TujuanOpd expectedTujuanOpd = TujuanOpd.of(
                tujuanId,
                "Realisasi Tujuan Opd " + tujuanId,
                indikatorId,
                "Realisasi Indikator Opd " + indikatorId,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
                kodeOpd,
                rumusPerhitungan,
                sumberData,
                definisiOperational,
                TujuanOpdStatus.UNCHECKED
        );

        when(tujuanOpdRepository.save(any(TujuanOpd.class))).thenReturn(Mono.just(expectedTujuanOpd));

        // Act
        Mono<TujuanOpd> result = tujuanOpdService.submitRealisasiTujuanOpd(
                tujuanId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData, definisiOperational);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(tujuanOpd ->
                        tujuanOpd.tujuanId().equals(expectedTujuanOpd.tujuanId()) &&
                                tujuanOpd.indikatorId().equals(expectedTujuanOpd.indikatorId()) &&
                                tujuanOpd.target().equals(expectedTujuanOpd.target()) &&
                                tujuanOpd.realisasi().equals(expectedTujuanOpd.realisasi()) &&
                                tujuanOpd.capaian().equals(expectedTujuanOpd.capaian()) &&
                                tujuanOpd.satuan().equals(expectedTujuanOpd.satuan()) &&
                                tujuanOpd.tahun().equals(expectedTujuanOpd.tahun()) &&
                                tujuanOpd.bulan().equals(expectedTujuanOpd.bulan()) &&
                                tujuanOpd.jenisRealisasi() == expectedTujuanOpd.jenisRealisasi() &&
                                tujuanOpd.kodeOpd().equals(expectedTujuanOpd.kodeOpd()) &&
                                tujuanOpd.rumusPerhitungan().equals(expectedTujuanOpd.rumusPerhitungan()) &&
                                tujuanOpd.sumberData().equals(expectedTujuanOpd.sumberData()) &&
                                tujuanOpd.definisiOperational().equals(expectedTujuanOpd.definisiOperational()) &&
                                tujuanOpd.status() == TujuanOpdStatus.UNCHECKED)
                .verifyComplete();
    }

    @Test
    void submitRealisasiTujuanOpd_ShouldThrowError_WhenRepositoryFails() {
        // Arrange
        String tujuanId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "1";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String kodeOpd = "OPD001";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";
        String definisiOperational = "Definisi indikator tujuan";

        when(tujuanOpdRepository.save(any(TujuanOpd.class))).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // Act
        Mono<TujuanOpd> result = tujuanOpdService.submitRealisasiTujuanOpd(
                tujuanId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData, definisiOperational);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Unexpected error"))
                .verify();
    }
}
