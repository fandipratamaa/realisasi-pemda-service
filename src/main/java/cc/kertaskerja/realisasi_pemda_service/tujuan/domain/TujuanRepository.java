package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TujuanRepository extends ReactiveCrudRepository<Tujuan, Long> {
    Flux<Tujuan> findAllByTahunAndBulan(String tahun, String bulan);

    Mono<Tujuan> findFirstByTujuanIdAndIndikatorIdAndTargetIdAndTahunAndBulan(
            String tujuanId, String indikatorId, String targetId, String tahun, String bulan);
}
