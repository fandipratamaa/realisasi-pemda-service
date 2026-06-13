package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RenaksiIndividuRepository extends ReactiveCrudRepository<RenaksiIndividu, Long> {
    Flux<RenaksiIndividu> findAllBySasaranId(Long sasaranId);

    Flux<RenaksiIndividu> findAllBySasaranIdIn(List<Long> sasaranIds);

    Mono<RenaksiIndividu> findFirstBySasaranIdAndKodeRenaksi(Long sasaranId, String kodeRenaksi);
}
