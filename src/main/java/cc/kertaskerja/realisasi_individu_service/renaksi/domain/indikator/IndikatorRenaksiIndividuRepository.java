package cc.kertaskerja.realisasi_individu_service.renaksi.domain.indikator;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IndikatorRenaksiIndividuRepository extends ReactiveCrudRepository<IndikatorRenaksiIndividu, Long> {
    Flux<IndikatorRenaksiIndividu> findAllByRenaksiId(Long renaksiId);

    Flux<IndikatorRenaksiIndividu> findAllByRenaksiIdIn(List<Long> renaksiIds);

    Mono<IndikatorRenaksiIndividu> findFirstByRenaksiIdAndKodeIndikator(Long renaksiId, String kodeIndikator);
}
