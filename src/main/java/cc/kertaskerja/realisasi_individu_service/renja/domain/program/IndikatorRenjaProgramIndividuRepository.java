package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaProgramIndividuRepository extends ReactiveCrudRepository<IndikatorRenjaProgramIndividu, Long> {
    Mono<IndikatorRenjaProgramIndividu> findByRenjaProgramIndividuIdAndTahunAndBulan(Long renjaProgramIndividuId, String tahun, String bulan);

    Mono<IndikatorRenjaProgramIndividu> findByRenjaProgramIndividuIdAndKodeIndikatorAndTahunAndBulan(Long renjaProgramIndividuId, String kodeIndikator, String tahun, String bulan);
}
