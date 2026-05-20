package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TujuanOpdRepository extends ReactiveCrudRepository<TujuanOpd, Long> {

    Mono<TujuanOpd> findFirstByKodeOpdAndKodeTujuanOpdAndTahunAndBulan(
            String kodeOpd,
            String kodeTujuanOpd,
            String tahun,
            String bulan
    );

    Flux<TujuanOpd> findAllByTahunAndKodeOpd(String tahun, String kodeOpd);

    Flux<TujuanOpd> findAllByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan);
}
