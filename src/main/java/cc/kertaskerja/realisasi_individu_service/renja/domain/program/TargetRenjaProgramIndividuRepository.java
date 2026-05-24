package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetRenjaProgramIndividuRepository extends ReactiveCrudRepository<TargetRenjaProgramIndividu, Long> {
    Flux<TargetRenjaProgramIndividu> findAllByTahunAndBulan(String tahun, String bulan);
    Mono<TargetRenjaProgramIndividu> findByIndikatorRenjaProgramIndividuIdAndTahunAndBulan(Long indikatorRenjaProgramIndividuId, String tahun, String bulan);
}
