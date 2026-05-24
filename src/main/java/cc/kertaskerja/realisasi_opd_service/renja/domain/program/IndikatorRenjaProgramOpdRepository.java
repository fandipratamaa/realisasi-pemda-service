package cc.kertaskerja.realisasi_opd_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaProgramOpdRepository extends ReactiveCrudRepository<IndikatorRenjaProgramOpd, Long> {
    Mono<IndikatorRenjaProgramOpd> findByRenjaProgramOpdIdAndKodeIndikatorAndTahunAndBulan(
            Long renjaProgramOpdId, String kodeIndikator, String tahun, String bulan);

    Flux<IndikatorRenjaProgramOpd> findAllByRenjaProgramOpdId(Long renjaProgramOpdId);
}
