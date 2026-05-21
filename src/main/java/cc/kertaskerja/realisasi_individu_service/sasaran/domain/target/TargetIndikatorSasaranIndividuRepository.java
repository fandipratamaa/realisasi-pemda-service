package cc.kertaskerja.realisasi_individu_service.sasaran.domain.target;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetIndikatorSasaranIndividuRepository extends ReactiveCrudRepository<TargetIndikatorSasaranIndividu, Long> {
    Mono<TargetIndikatorSasaranIndividu> findFirstByIndikatorSasaranIdAndKodeTargetAndTahunAndBulan(
            Long indikatorSasaranId,
            String kodeTarget,
            String tahun,
            String bulan
    );

    Flux<TargetIndikatorSasaranIndividu> findAllByIndikatorSasaranId(Long indikatorSasaranId);
}
