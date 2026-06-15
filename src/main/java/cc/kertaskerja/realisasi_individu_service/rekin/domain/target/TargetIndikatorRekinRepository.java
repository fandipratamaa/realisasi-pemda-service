package cc.kertaskerja.realisasi_individu_service.rekin.domain.target;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TargetIndikatorRekinRepository extends ReactiveCrudRepository<TargetIndikatorRekin, Long> {
    Flux<TargetIndikatorRekin> findAllByIndikatorRekinId(Long indikatorRekinId);

    Flux<TargetIndikatorRekin> findAllByIndikatorRekinIdIn(List<Long> indikatorRekinIds);

    Mono<TargetIndikatorRekin> findFirstByIndikatorRekinIdAndKodeTargetPkRekin(Long indikatorRekinId, String kodeTargetPkRekin);
}
