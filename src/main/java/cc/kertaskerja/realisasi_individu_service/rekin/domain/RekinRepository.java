package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RekinRepository extends ReactiveCrudRepository<Rekin, Long> {
    Flux<Rekin> findAllByRekinId(String rekinId);

    Flux<Rekin> findAllByTahun(String tahun);

    Flux<Rekin> findAllByIdSasaran(String idSasaran);

    Flux<Rekin> findAllByNip(String nip);

    Flux<Rekin> findAllByTahunBetween(String tahunAwal, String tahunAkhir);

    Mono<Rekin> findFirstByNipAndIdSasaranAndTahun(String nip, String idSasaran, String tahun);
}
