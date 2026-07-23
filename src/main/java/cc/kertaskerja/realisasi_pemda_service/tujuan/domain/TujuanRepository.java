package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TujuanRepository extends ReactiveCrudRepository<Tujuan, Long> {
    Flux<Tujuan> findAllByTahun(String tahun);

    Flux<Tujuan> findAllByTahunAndBulan(String tahun, String bulan);

    Mono<Tujuan> findFirstByKodeTujuanPemdaAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeTujuanPemda, String kodeIndikator, String kodeTarget, String tahun, String bulan);
}
