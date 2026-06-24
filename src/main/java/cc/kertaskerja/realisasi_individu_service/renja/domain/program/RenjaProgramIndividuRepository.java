package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaProgramIndividuRepository extends ReactiveCrudRepository<RenjaProgramIndividu, Long> {
    Mono<RenjaProgramIndividu> findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeProgram, String kodeIndikator, String kodeTarget, String tahun, String bulan);
    Flux<RenjaProgramIndividu> findAllByTahunAndBulan(String tahun, String bulan);
    Flux<RenjaProgramIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
    Flux<RenjaProgramIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);
    Flux<RenjaProgramIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);
    Flux<RenjaProgramIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);
}
