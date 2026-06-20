package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiIndividuRepository extends ReactiveCrudRepository<RenaksiIndividu, Long> {
    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndKodeSasaran(String kodeOpd, String nip, String kodeSasaran);

    Mono<RenaksiIndividu> findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTarget(
            String kodeOpd, String nip, String kodeSasaran,
            String kodeRenaksi, String kodeIndikator, String kodeTarget
    );
}
