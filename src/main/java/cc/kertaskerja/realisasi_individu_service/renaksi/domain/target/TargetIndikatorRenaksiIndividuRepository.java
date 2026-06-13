package cc.kertaskerja.realisasi_individu_service.renaksi.domain.target;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TargetIndikatorRenaksiIndividuRepository extends ReactiveCrudRepository<TargetIndikatorRenaksiIndividu, Long> {
    Flux<TargetIndikatorRenaksiIndividu> findAllByIndikatorRenaksiId(Long indikatorRenaksiId);

    Flux<TargetIndikatorRenaksiIndividu> findAllByIndikatorRenaksiIdIn(List<Long> indikatorRenaksiIds);

    Mono<TargetIndikatorRenaksiIndividu> findFirstByIndikatorRenaksiIdAndKodeTarget(Long indikatorRenaksiId, String kodeTarget);
}
