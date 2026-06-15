package cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IndikatorRekinRepository extends ReactiveCrudRepository<IndikatorRekin, Long> {
    Flux<IndikatorRekin> findAllByRekinId(Long rekinId);

    Flux<IndikatorRekin> findAllByRekinIdIn(List<Long> rekinIds);

    Mono<IndikatorRekin> findFirstByRekinIdAndKodeIndikatorPkRekin(Long rekinId, String kodeIndikatorPkRekin);
}
