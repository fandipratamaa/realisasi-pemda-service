package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaProgramIndividuRepository extends ReactiveCrudRepository<RenjaProgramIndividu, Long> {
    Mono<RenjaProgramIndividu> findByKodeProgram(String kodeProgram);
    Flux<RenjaProgramIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
}
