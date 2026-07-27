package cc.kertaskerja.realisasi_individu_service.renja.domain;

import cc.kertaskerja.integration.kepegawaian.PegawaiClient;
import cc.kertaskerja.integration.penetapan.PenetapanRenjaIndividuClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaIndividu;
import cc.kertaskerja.integration.upload.UploadClient;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenjaIndividuServiceTest {

    @Mock
    private RenjaProgramIndividuRepository programRepo;
    @Mock
    private RenjaKegiatanIndividuRepository kegiatanRepo;
    @Mock
    private RenjaSubKegiatanIndividuRepository subKegiatanRepo;
    @Mock
    private UploadClient uploadClient;
    @Mock
    private PegawaiClient pegawaiClient;
    @Mock
    private PenetapanRenjaIndividuClient penetapanClient;

    @InjectMocks
    private RenjaIndividuService renjaIndividuService;

    private PenetapanRenjaIndividu.RenjaIndividuData createPenetapanData() {
        var target = new PenetapanRenjaIndividu.TargetPenetapanData(1L, "TGT-001", 2026, 100.0, "%");
        var indikator = new PenetapanRenjaIndividu.IndikatorPenetapanData(1L, "IND-001", "Indikator Prog", List.of(target));
        var renja = new PenetapanRenjaIndividu.RenjaData(
                10L, "REKIN-PEG-2026-001", 6, "198701252015051001", "PAULUS",
                "8.01.03", "PROGRAM PENINGKATAN PERAN", "PAGU-1", 1000L, List.of(indikator),
                "8.01.03.2.01", "Kegiatan 1", "PAGU-2", 500L, List.of(),
                "8.01.03.2.01.0003", "Subkegiatan 1", "PAGU-3", 200L, List.of()
        );
        return new PenetapanRenjaIndividu.RenjaIndividuData(
                "198701252015051001", "PAULUS", "8.01.0.00.0.00.01.0000", 2026, List.of(renja)
        );
    }

    @Test
    void getPenetapanByNip_WithoutBulan_ShouldReturnWithoutRealisasi() {
        when(penetapanClient.fetchRenjaIndividu("198701252015051001", "8.01.0.00.0.00.01.0000", 2026))
                .thenReturn(Mono.just(createPenetapanData()));

        StepVerifier.create(renjaIndividuService.getPenetapanByNip("198701252015051001", "8.01.0.00.0.00.01.0000", 2026, null))
                .assertNext(res -> {
                    assertEquals("198701252015051001", res.pegawaiId());
                    assertEquals("PAULUS", res.nama());
                    assertEquals("8.01.0.00.0.00.01.0000", res.kodeOpd());
                    assertEquals(2026, res.tahunAktif());
                    assertNull(res.bulan());
                    assertEquals(1, res.renjas().size());
                    var tgt = res.renjas().get(0).indikatorPrograms().get(0).targets().get(0);
                    assertNull(tgt.realisasiTarget());
                    assertEquals(0.0, tgt.capaianTarget());
                })
                .verifyComplete();
    }

    @Test
    void getPenetapanByNip_WithBulan_ShouldMergeWithRealisasi() {
        when(penetapanClient.fetchRenjaIndividu("198701252015051001", "8.01.0.00.0.00.01.0000", 2026))
                .thenReturn(Mono.just(createPenetapanData()));

        RenjaProgramIndividu realisasiProg = new RenjaProgramIndividu(
                1L, "8.01.0.00.0.00.01.0000", "198701252015051001", "2026", "1",
                "8.01.03", "IND-001", "TGT-001", "PAGU-1",
                BigDecimal.valueOf(50.0), "NAIK",
                "Faktor Penunjang Test", "Faktor Penghambat Test",
                "bukti.pdf", null, null, null, null, null
        );

        when(programRepo.findAllByKodeOpdAndNipAndTahunAndBulan("8.01.0.00.0.00.01.0000", "198701252015051001", "2026", "1"))
                .thenReturn(Flux.just(realisasiProg));
        when(kegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan("8.01.0.00.0.00.01.0000", "198701252015051001", "2026", "1"))
                .thenReturn(Flux.empty());
        when(subKegiatanRepo.findAllByKodeOpdAndNipAndTahunAndBulan("8.01.0.00.0.00.01.0000", "198701252015051001", "2026", "1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(renjaIndividuService.getPenetapanByNip("198701252015051001", "8.01.0.00.0.00.01.0000", 2026, "1"))
                .assertNext(res -> {
                    assertEquals(1, res.bulan());
                    var tgt = res.renjas().get(0).indikatorPrograms().get(0).targets().get(0);
                    assertEquals(50.0, tgt.realisasiTarget());
                    assertEquals(50.0, tgt.capaianTarget());
                    assertEquals("Faktor Penunjang Test", tgt.faktorPenunjang());
                })
                .verifyComplete();
    }

    @Test
    void syncPenetapanRenjaIndividu_ShouldCallClient() {
        when(penetapanClient.syncRenjaIndividu("198701252015051001", "8.01.0.00.0.00.01.0000", 2026))
                .thenReturn(Mono.just("SUCCESS"));

        StepVerifier.create(renjaIndividuService.syncPenetapanRenjaIndividu("198701252015051001", "8.01.0.00.0.00.01.0000", 2026))
                .expectNext("SUCCESS")
                .verifyComplete();
    }

    @Test
    void submitProgram_ShouldCalculateCapaianFromPenetapanTarget() {
        var req = new cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest(
                "8.01.0.00.0.00.01.0000", "2026", "1", "198701252015051001",
                "8.01.03", "IND-001", "TGT-001", "PAGU-1",
                50.0, "NAIK", "bukti.pdf", "keterangan"
        );

        when(penetapanClient.fetchRenjaIndividu("198701252015051001", "8.01.0.00.0.00.01.0000", 2026))
                .thenReturn(Mono.just(createPenetapanData()));

        when(programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                "8.01.0.00.0.00.01.0000", "8.01.03", "IND-001", "TGT-001", "2026", "1"))
                .thenReturn(Mono.empty());

        RenjaProgramIndividu savedProg = new RenjaProgramIndividu(
                1L, "8.01.0.00.0.00.01.0000", "198701252015051001", "2026", "1",
                "8.01.03", "IND-001", "TGT-001", "PAGU-1",
                BigDecimal.valueOf(50.0), "NAIK",
                "", "", "bukti.pdf", "keterangan",
                null, null, null, null
        );
        when(programRepo.save(any(RenjaProgramIndividu.class))).thenReturn(Mono.just(savedProg));

        StepVerifier.create(renjaIndividuService.submitProgram(req))
                .assertNext(res -> {
                    assertEquals(50.0, res.realisasiTarget());
                    assertEquals(100.0, res.target());
                    assertEquals(50.0, res.capaianTarget());
                    assertEquals("PROGRAM PENINGKATAN PERAN", res.program());
                })
                .verifyComplete();
    }
}
