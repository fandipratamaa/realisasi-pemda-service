package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RekinRepository extends ReactiveCrudRepository<Rekin, Long> {
    Flux<Rekin> findAllByRekinId(String rekinId);

    Flux<Rekin> findAllByNipAndTahun(String nip, String tahun);

    Flux<Rekin> findAllByTahunBetween(String tahunAwal, String tahunAkhir);

    Mono<Rekin> findFirstByNipAndIdSasaranAndTahunAndRekinId(String nip, String idSasaran, String tahun, String rekinId);
}
