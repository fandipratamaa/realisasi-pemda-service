package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TujuanOpdRepository extends ReactiveCrudRepository<TujuanOpd, Long> {
    Flux<TujuanOpd> findAllByTujuanId(String tujuanId);

    Flux<TujuanOpd> findAllByTahunAndKodeOpd(String tahun, String kodeOpd);

    Flux<TujuanOpd> findAllByIndikatorId(String indikatorId);

    Flux<TujuanOpd> findAllByTahunBetweenAndKodeOpd(String tahunAwal, String tahunAkhir, String kodeOpd);

    Flux<TujuanOpd> findAllByKodeOpd(String kodeOpd);

    Flux<TujuanOpd> findAllByTahunAndTujuanIdAndKodeOpd(String tahun, String tujuanId, String kodeOpd);

    Flux<TujuanOpd> findAllByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan);
}
