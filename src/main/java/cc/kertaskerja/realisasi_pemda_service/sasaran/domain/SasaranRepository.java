package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SasaranRepository extends ReactiveCrudRepository<Sasaran, Long> {
    Flux<Sasaran> findAllByTahunAndBulan(String tahun, String bulan);

    Mono<Sasaran> findFirstBySasaranIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
            String sasaranId, String indikatorId, String targetId, String tahun, String bulan);
}
