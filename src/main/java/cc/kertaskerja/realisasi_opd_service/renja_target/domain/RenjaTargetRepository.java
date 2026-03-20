package cc.kertaskerja.realisasi_opd_service.renja_target.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RenjaTargetRepository extends ReactiveCrudRepository<RenjaTarget, Long> {
    Flux<RenjaTarget> findAllByRenjaTargetId(String renjaTargetId);

    Flux<RenjaTarget> findAllByTahunAndKodeOpd(String tahun, String kodeOpd);

    Flux<RenjaTarget> findAllByIndikatorId(String indikatorId);

    Flux<RenjaTarget> findAllByTahunBetweenAndKodeOpd(String tahunAwal, String tahunAkhir, String kodeOpd);

    Flux<RenjaTarget> findAllByKodeOpd(String kodeOpd);

    Flux<RenjaTarget> findAllByTahunAndRenjaTargetIdAndKodeOpd(String tahun, String renjaTargetId, String kodeOpd);
}
