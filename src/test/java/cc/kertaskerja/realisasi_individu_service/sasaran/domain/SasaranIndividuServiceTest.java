package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SasaranIndividuServiceTest {
    @Mock
    private SasaranIndividuRepository sasaranIndividuRepository;

    @InjectMocks
    private SasaranIndividuService sasaranIndividuService;

    @Test
    void submitRealisasiSasaranIndividu_ShouldReturnSavedEntity_WhenValidInputProvided() {
        String renjaId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "JANUARI";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String nip = "198012312005011001";
        String namaPegawai = "Anon";
        String kodeOpd = "1.01.0.00.0.00.01.0000";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";

        SasaranIndividu expected = SasaranIndividu.of(
                renjaId,
                "Realisasi Renja Individu " + renjaId,
                indikatorId,
                "Realisasi Indikator Individu " + indikatorId,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
                nip,
                namaPegawai,
                kodeOpd,
                rumusPerhitungan,
                sumberData,
                SasaranIndividuStatus.UNCHECKED
        );

        when(sasaranIndividuRepository.save(any(SasaranIndividu.class))).thenReturn(Mono.just(expected));

        Mono<SasaranIndividu> result = sasaranIndividuService.submitRealisasiSasaranIndividu(
                renjaId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan,
                jenisRealisasi, nip, namaPegawai, kodeOpd, rumusPerhitungan, sumberData);

        StepVerifier.create(result)
                .expectNextMatches(sasaranIndividu ->
                        sasaranIndividu.renjaId().equals(expected.renjaId()) &&
                                sasaranIndividu.indikatorId().equals(expected.indikatorId()) &&
                                sasaranIndividu.target().equals(expected.target()) &&
                                sasaranIndividu.realisasi().equals(expected.realisasi()) &&
                                sasaranIndividu.capaian().equals(expected.capaian()) &&
                                sasaranIndividu.satuan().equals(expected.satuan()) &&
                                sasaranIndividu.tahun().equals(expected.tahun()) &&
                                sasaranIndividu.bulan().equals(expected.bulan()) &&
                                sasaranIndividu.jenisRealisasi() == expected.jenisRealisasi() &&
                                sasaranIndividu.nip().equals(expected.nip()) &&
                                sasaranIndividu.namaPegawai().equals(expected.namaPegawai()) &&
                                sasaranIndividu.rumusPerhitungan().equals(expected.rumusPerhitungan()) &&
                                sasaranIndividu.sumberData().equals(expected.sumberData()) &&
                                sasaranIndividu.status() == SasaranIndividuStatus.UNCHECKED)
                .verifyComplete();
    }

    @Test
    void submitRealisasiSasaranIndividu_ShouldThrowError_WhenRepositoryFails() {
        String renjaId = UUID.randomUUID().toString();
        String indikatorId = UUID.randomUUID().toString();
        String targetId = UUID.randomUUID().toString();
        String target = "100";
        Double realisasi = 80.0;
        String satuan = "Unit";
        String tahun = "2025";
        String bulan = "JANUARI";
        JenisRealisasi jenisRealisasi = JenisRealisasi.NAIK;
        String nip = "198012312005011001";
        String namaPegawai = "Anon";
        String kodeOpd = "1.01.0.00.0.00.01.0000";
        String rumusPerhitungan = "(realisasi/target)*100";
        String sumberData = "SIMDA";

        when(sasaranIndividuRepository.save(any(SasaranIndividu.class))).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        Mono<SasaranIndividu> result = sasaranIndividuService.submitRealisasiSasaranIndividu(
                renjaId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan,
                jenisRealisasi, nip, namaPegawai, kodeOpd, rumusPerhitungan, sumberData);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Unexpected error"))
                .verify();
    }

    @Test
    void getByTahunBulanNipAndRenjaId_ShouldReturnRepositoryResult() {
        String tahun = "2025";
        String bulan = "1";
        String nip = "198012312005011001";
        String renjaId = "REN-001";

        SasaranIndividu expected = SasaranIndividu.of(
                renjaId,
                "Realisasi Renja Individu " + renjaId,
                "IND-001",
                "Realisasi Indikator Individu IND-001",
                "TAR-001",
                "100",
                80.0,
                "%",
                tahun,
                bulan,
                JenisRealisasi.NAIK,
                nip,
                "Anon",
                "1.01.0.00.0.00.01.0000",
                "(realisasi/target)*100",
                "SIMDA",
                SasaranIndividuStatus.UNCHECKED
        );

        when(sasaranIndividuRepository.findAllByTahunAndBulanAndNipAndRenjaId(tahun, bulan, nip, renjaId))
                .thenReturn(reactor.core.publisher.Flux.fromIterable(List.of(expected)));

        StepVerifier.create(sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndNipAndRenjaId(tahun, bulan, nip, renjaId))
                .expectNext(expected)
                .verifyComplete();

        verify(sasaranIndividuRepository).findAllByTahunAndBulanAndNipAndRenjaId(tahun, bulan, nip, renjaId);
    }
}
