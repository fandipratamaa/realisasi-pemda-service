package cc.kertaskerja.realisasi_opd_service.renja.web;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguRepository;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguStatus;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetRepository;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetStatus;
import cc.kertaskerja.renja.domain.JenisRenja;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RenjaOpdHierarkiServiceTests {
    @Mock
    private RenjaTargetRepository renjaTargetRepository;

    @Mock
    private RenjaPaguRepository renjaPaguRepository;

    @InjectMocks
    private RenjaOpdHierarkiService renjaOpdHierarkiService;

    @Test
    void getHierarkiByKodeOpdTahunBulan_ShouldSeparateTreesByJenisRenjaId() {
        RenjaTarget renjaTargetSatu = new RenjaTarget(
                1L,
                "ID-REN01",
                JenisRenja.PROGRAM,
                "IND-1",
                "Indikator 1",
                "TAR-1",
                "100",
                80,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "5.01.5.05.0.00.01.0000",
                "5.01.03",
                RenjaTargetStatus.CHECKED,
                "maker-1",
                Instant.now(),
                Instant.now(),
                "reviewer-1",
                1
        );
        RenjaTarget renjaTargetDua = new RenjaTarget(
                2L,
                "ID-REN02",
                JenisRenja.PROGRAM,
                "IND-2",
                "Indikator 2",
                "TAR-2",
                "100",
                60,
                "%",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "5.01.5.05.0.00.01.0000",
                "5.01.03",
                RenjaTargetStatus.CHECKED,
                "maker-2",
                Instant.now(),
                Instant.now(),
                "reviewer-2",
                1
        );

        RenjaPagu renjaPaguSatu = new RenjaPagu(
                1L,
                "ID-REN01",
                JenisRenja.PROGRAM,
                4006000,
                1000000,
                "Rp",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "5.01.5.05.0.00.01.0000",
                "5.01.03",
                RenjaPaguStatus.CHECKED,
                "maker-1",
                Instant.now(),
                Instant.now(),
                "reviewer-1",
                1
        );
        RenjaPagu renjaPaguDua = new RenjaPagu(
                2L,
                "ID-REN02",
                JenisRenja.PROGRAM,
                5007000,
                2000000,
                "Rp",
                "2025",
                "1",
                JenisRealisasi.NAIK,
                "5.01.5.05.0.00.01.0000",
                "5.01.03",
                RenjaPaguStatus.CHECKED,
                "maker-2",
                Instant.now(),
                Instant.now(),
                "reviewer-2",
                1
        );

        when(renjaTargetRepository.findAllByTahunAndBulanAndKodeOpd("2025", "1", "5.01.5.05.0.00.01.0000"))
                .thenReturn(Flux.just(renjaTargetSatu, renjaTargetDua));
        when(renjaPaguRepository.findAllByTahunAndBulanAndKodeOpd("2025", "1", "5.01.5.05.0.00.01.0000"))
                .thenReturn(Flux.just(renjaPaguSatu, renjaPaguDua));

        StepVerifier.create(renjaOpdHierarkiService.getHierarkiByKodeOpdTahunBulan(
                        "5.01.5.05.0.00.01.0000",
                        "2025",
                        "1",
                        RenjaOpdHierarkiService.DataSource.TARGET
                ))
                .assertNext(response -> {
                    org.junit.jupiter.api.Assertions.assertEquals(2, response.data().size());

                    RenjaOpdHierarkiResponse.DataItem first = response.data().get(0);
                    RenjaOpdHierarkiResponse.DataItem second = response.data().get(1);

                    org.junit.jupiter.api.Assertions.assertEquals(List.of("ID-REN01", "ID-REN02"),
                            response.data().stream().map(RenjaOpdHierarkiResponse.DataItem::idRenja).toList());
                    org.junit.jupiter.api.Assertions.assertEquals(1000000L, first.paguTotalRealisasi());
                    org.junit.jupiter.api.Assertions.assertEquals(2000000L, second.paguTotalRealisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("5.01.03", first.program().getFirst().kodeRenja());
                    org.junit.jupiter.api.Assertions.assertEquals("5.01.03", second.program().getFirst().kodeRenja());
                    org.junit.jupiter.api.Assertions.assertEquals("80", first.program().getFirst().target().getFirst().realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("60", second.program().getFirst().target().getFirst().realisasi());
                    org.junit.jupiter.api.Assertions.assertEquals("maker-1", first.program().getFirst().target().getFirst().createdBy());
                    org.junit.jupiter.api.Assertions.assertEquals("maker-2", second.program().getFirst().target().getFirst().createdBy());
                    org.junit.jupiter.api.Assertions.assertEquals(4006000, first.program().getFirst().pagu().getFirst().pagu());
                    org.junit.jupiter.api.Assertions.assertEquals(5007000, second.program().getFirst().pagu().getFirst().pagu());
                })
                .verifyComplete();
    }
}
