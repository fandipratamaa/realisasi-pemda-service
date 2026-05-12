package cc.kertaskerja.realisasi_individu_service.sasaran.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SasaranIndividuRepository extends ReactiveCrudRepository<SasaranIndividu, Long> {
    Flux<SasaranIndividu> findAllByTahunAndBulanAndNip(String tahun, String bulan, String nip);

    Flux<SasaranIndividu> findAllByTahunAndBulanAndNipAndRenjaId(String tahun, String bulan, String nip, String renjaId);

    Mono<Void> deleteByRenjaId(String renjaId);
}
