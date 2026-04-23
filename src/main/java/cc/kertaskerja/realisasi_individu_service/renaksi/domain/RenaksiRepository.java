package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiRepository extends ReactiveCrudRepository<Renaksi, Long> {
    Mono<Renaksi> findFirstByNipAndRenaksiIdAndTahun(String nip, String renaksiId, String tahun);

    Mono<Renaksi> findFirstByNipAndBulanAndRekinIdAndRenaksiId(String nip, String bulan, String rekinId, String renaksiId);

    Flux<Renaksi> findAllByNipAndBulan(String nip, String bulan);

    Flux<Renaksi> findAllByNipAndTahunAndBulan(String nip, String tahun, String bulan);

    Flux<Renaksi> findAllByKodeOpdAndBulan(String kodeOpd, String bulan);
}
