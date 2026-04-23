package cc.kertaskerja.realisasi_pemda_service.tujuan.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TujuanRepository extends ReactiveCrudRepository<Tujuan, Long> {
    Flux<Tujuan> findAllByTahun(String tahun);

    Flux<Tujuan> findAllByTujuanId(String tujuanId);

    Flux<Tujuan> findAllByTahunAndTujuanId(String tahun, String tujuanId);

    Flux<Tujuan> findAllByIndikatorId(String indikatorId);

    Flux<Tujuan> findAllByTahunBetween(String tahunAwal, String tahunAkhir);

    Flux<Tujuan> findAllByTahunAndBulan(String tahun, String bulan);
}
