package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetRenjaSubKegiatanIndividuRepository extends ReactiveCrudRepository<TargetRenjaSubKegiatanIndividu, Long> {
    Flux<TargetRenjaSubKegiatanIndividu> findAllByTahunAndBulan(String tahun, String bulan);
    Mono<TargetRenjaSubKegiatanIndividu> findByIndikatorRenjaSubKegiatanIndividuIdAndTahunAndBulan(Long indikatorRenjaSubKegiatanIndividuId, String tahun, String bulan);
}
