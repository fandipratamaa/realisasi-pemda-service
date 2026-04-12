package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiRepository extends ReactiveCrudRepository<Renaksi, Long> {
    Flux<Renaksi> findAllByTahun(String tahun);

    Flux<Renaksi> findAllByTahunBetween(String tahunAwal, String tahunAkhir);

    Mono<Renaksi> findFirstByNipAndRenaksiIdAndTahun(String nip, String renaksiId, String tahun);

    Mono<Renaksi> findFirstByNipAndBulanAndRekinId(String nip, String bulan, String rekinId);
}
