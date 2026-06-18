package cc.kertaskerja.realisasi_opd_service.sasaran.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SasaranOpdRepository extends ReactiveCrudRepository<SasaranOpd, Long> {

    Flux<SasaranOpd> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

    Mono<SasaranOpd> findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd,
            String kodeSasaranOpd,
            String kodeIndikator,
            String kodeTarget,
            String tahun,
            String bulan
    );

    Flux<SasaranOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
}
