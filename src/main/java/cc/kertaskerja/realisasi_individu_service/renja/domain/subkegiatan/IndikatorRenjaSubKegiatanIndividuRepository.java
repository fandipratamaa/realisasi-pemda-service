package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaSubKegiatanIndividuRepository extends ReactiveCrudRepository<IndikatorRenjaSubKegiatanIndividu, Long> {
    Mono<IndikatorRenjaSubKegiatanIndividu> findByRenjaSubKegiatanIndividuIdAndTahunAndBulan(Long renjaSubKegiatanIndividuId, String tahun, String bulan);
}
