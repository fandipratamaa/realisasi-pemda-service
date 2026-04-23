package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import cc.kertaskerja.config.DataConfig;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
public class TujuanRepositoryR2dbcTests {
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TujuanRepository tujuanRepository;

    @DynamicPropertySource
    static void postgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", TujuanRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%s/%s", postgreSQLContainer.getHost(), postgreSQLContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgreSQLContainer.getDatabaseName());
    }

    @Test
    void findRealisasiTujuanByPeriodeRpjmd() {
        var realisasi2025 = TujuanService.buildUncheckedRealisasiTujuan("1", "123", "TAR-1", "10.0", 10.0, "%", "2025", "01", JenisRealisasi.NAIK);
        var realisasi2026 = TujuanService.buildUncheckedRealisasiTujuan("1", "123", "TAR-2", "12.0", 12.0, "%", "2026", "01", JenisRealisasi.NAIK);
        StepVerifier.create(
                tujuanRepository
                        .saveAll(Flux.just(realisasi2025, realisasi2026))
                        .thenMany(tujuanRepository.findAllByTahunBetween("2025", "2026")))
                .expectNextMatches(t -> t.tahun().equals("2025"))
                .expectNextMatches(t -> t.tahun().equals("2026"))
                .verifyComplete();
    }
}
