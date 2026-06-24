package cc.kertaskerja.realisasi_opd_service.renja.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaProgramOpdRepository extends ReactiveCrudRepository<RenjaProgramOpd, Long> {
    Mono<RenjaProgramOpd> findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeProgram, String kodeIndikator, String kodeTarget, String tahun, String bulan);

    Flux<RenjaProgramOpd> findAllByTahunAndBulan(String tahun, String bulan);

    Flux<RenjaProgramOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

    Mono<RenjaProgramOpd> findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahun(
            String kodeOpd, String kodeProgram, String kodeIndikator, String kodeTarget, String tahun);
}
