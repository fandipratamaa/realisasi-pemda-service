package cc.kertaskerja.realisasi_opd_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiOpdRepository extends ReactiveCrudRepository<RenaksiOpd, Long> {
    Flux<RenaksiOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

    Flux<RenaksiOpd> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

    Flux<RenaksiOpd> findAllByKodeOpdAndTahunAndRenaksiIdAndTargetId(
            String kodeOpd,
            String tahun,
            String renaksiId,
            String targetId
    );

    Mono<RenaksiOpd> findFirstByKodeOpdAndBulanAndRekinIdAndRenaksiId(String kodeOpd, String bulan, String rekinId, String renaksiId);
}
