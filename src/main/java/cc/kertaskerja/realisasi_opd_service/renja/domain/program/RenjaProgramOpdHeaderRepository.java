package cc.kertaskerja.realisasi_opd_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RenjaProgramOpdHeaderRepository extends ReactiveCrudRepository<RenjaProgramOpdHeader, Long> {
    Mono<RenjaProgramOpdHeader> findByKodeOpdAndKodeProgramAndTahunAndBulan(
            String kodeOpd, String kodeProgram, String tahun, String bulan);
}
