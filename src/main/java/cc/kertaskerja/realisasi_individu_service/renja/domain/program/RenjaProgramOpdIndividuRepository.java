package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaProgramOpdIndividuRepository extends ReactiveCrudRepository<RenjaProgramOpd, Long> {
    Mono<RenjaProgramOpd> findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeProgram, String kodeIndikator, String kodeTarget, String tahun, String bulan);
    Flux<RenjaProgramOpd> findAllByTahunAndBulan(String tahun, String bulan);
    Flux<RenjaProgramOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
    Flux<RenjaProgramOpd> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);
    Flux<RenjaProgramOpd> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);
}
