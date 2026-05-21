package cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IndikatorSasaranIndividuRepository extends ReactiveCrudRepository<IndikatorSasaranIndividu, Long> {
    Mono<cc.kertaskerja.realisasi_individu_service.sasaran.domain.indikator.IndikatorSasaranIndividu> findFirstBySasaranOpdIdAndKodeIndikatorAndKodeOpdAndTahunAndBulan(
            Long sasaranIndividuId,
            String kodeIndikator,
            String kodeOpd,
            String tahun,
            String bulan
    );

    Flux<IndikatorSasaranIndividu> findAllBySasaranOpdId(Long sasaranOpdId);
}
