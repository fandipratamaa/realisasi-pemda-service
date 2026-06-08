package cc.kertaskerja.realisasi_opd_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaProgramOpdRepository extends ReactiveCrudRepository<RenjaProgramOpd, Long> {
    Mono<RenjaProgramOpd> findByKodeTarget(String kodeTarget);
    Mono<RenjaProgramOpd> findByIndikatorRenjaProgramOpdIdAndKodeTargetAndTahunAndBulan(
            Long indikatorRenjaProgramOpdId, String kodeTarget, String tahun, String bulan);
    Flux<RenjaProgramOpd> findAllByIndikatorRenjaProgramOpdId(Long indikatorRenjaProgramOpdId);
    Flux<RenjaProgramOpd> findAllByTahunAndBulan(String tahun, String bulan);
}
