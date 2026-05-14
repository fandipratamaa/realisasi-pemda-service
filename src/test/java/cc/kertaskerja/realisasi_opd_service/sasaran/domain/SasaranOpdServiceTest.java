package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

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
public class SasaranOpdServiceTest {
    @Mock
    private SasaranOpdRepository sasaranOpdRepository;

    @InjectMocks
    private SasaranOpdService sasaranOpdService;

@Test
    void submitRealisasiSasaranOpd_ShouldReturnSavedEntity_WhenValidInputProvided() {
        // Arrange
        String sasaranId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "JANUARI";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String kodeOpd = "OPD001";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";
        String definisiOperational = "Definisi indikator sasaran";

        SasaranOpd expectedSasaranOpd = SasaranOpd.of(
                sasaranId,
                "Realisasi Sasaran Opd " + sasaranId,
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
                SasaranOpdStatus.UNCHECKED
        );

        when(sasaranOpdRepository.save(any(SasaranOpd.class))).thenReturn(Mono.just(expectedSasaranOpd));

        // Act
        Mono<SasaranOpd> result = sasaranOpdService.submitRealisasiSasaranOpd(
                sasaranId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData, definisiOperational);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(sasaranOpd ->
                        sasaranOpd.renjaId().equals(expectedSasaranOpd.renjaId()) &&
                                sasaranOpd.indikatorId().equals(expectedSasaranOpd.indikatorId()) &&
                                sasaranOpd.target().equals(expectedSasaranOpd.target()) &&
                                sasaranOpd.realisasi().equals(expectedSasaranOpd.realisasi()) &&
                                sasaranOpd.capaian().equals(expectedSasaranOpd.capaian()) &&
                                sasaranOpd.satuan().equals(expectedSasaranOpd.satuan()) &&
                                sasaranOpd.tahun().equals(expectedSasaranOpd.tahun()) &&
                                sasaranOpd.bulan().equals(expectedSasaranOpd.bulan()) &&
                                sasaranOpd.jenisRealisasi() == expectedSasaranOpd.jenisRealisasi() &&
                                sasaranOpd.kodeOpd().equals(expectedSasaranOpd.kodeOpd()) &&
                                sasaranOpd.rumusPerhitungan().equals(expectedSasaranOpd.rumusPerhitungan()) &&
                                sasaranOpd.sumberData().equals(expectedSasaranOpd.sumberData()) &&
                                sasaranOpd.definisiOperational().equals(expectedSasaranOpd.definisiOperational()) &&
                                sasaranOpd.status() == SasaranOpdStatus.UNCHECKED)
                .verifyComplete();
    }

@Test
    void submitRealisasiSasaranOpd_ShouldThrowError_WhenRepositoryFails() {
        // Arrange
        String sasaranId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "JANUARI";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String kodeOpd = "OPD001";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";
        String definisiOperational = "Definisi indikator sasaran";

        when(sasaranOpdRepository.save(any(SasaranOpd.class))).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // Act
        Mono<SasaranOpd> result = sasaranOpdService.submitRealisasiSasaranOpd(
                sasaranId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData, definisiOperational);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Unexpected error"))
                .verify();
    }
}
