package cc.kertaskerja.realisasi_opd_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiOpdRepository extends ReactiveCrudRepository<RenaksiOpd, Long> {
    Flux<RenaksiOpd> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);

    Flux<RenaksiOpd> findAllByKodeOpdAndNipAndTahunAndRenaksiIdAndTargetId(
            String kodeOpd,
            String nip,
            String tahun,
            String renaksiId,
            String targetId
    );

    Mono<RenaksiOpd> findFirstByNipAndBulanAndRekinIdAndRenaksiId(String nip, String bulan, String rekinId, String renaksiId);
}
