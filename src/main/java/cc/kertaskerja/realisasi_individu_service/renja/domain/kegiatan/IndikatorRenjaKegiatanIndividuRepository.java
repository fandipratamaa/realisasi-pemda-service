package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaKegiatanIndividuRepository extends ReactiveCrudRepository<IndikatorRenjaKegiatanIndividu, Long> {
    Mono<IndikatorRenjaKegiatanIndividu> findByRenjaKegiatanIndividuIdAndTahunAndBulan(Long renjaKegiatanIndividuId, String tahun, String bulan);

    Mono<IndikatorRenjaKegiatanIndividu> findByRenjaKegiatanIndividuIdAndKodeIndikatorAndTahunAndBulan(Long renjaKegiatanIndividuId, String kodeIndikator, String tahun, String bulan);
}
