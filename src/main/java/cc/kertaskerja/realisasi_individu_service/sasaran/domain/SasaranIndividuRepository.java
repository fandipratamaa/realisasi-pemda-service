package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SasaranIndividuRepository extends ReactiveCrudRepository<SasaranIndividu, Long> {
    Mono<SasaranIndividu> findFirstByKodeOpdAndKodeSasaranOpdAndTahunAndBulan(
            String kodeOpd,
            String kodeSasaranOpd,
            String tahun,
            String bulan
    );

    Flux<SasaranIndividu> findAllByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan);
}
