package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetRenjaKegiatanIndividuRepository extends ReactiveCrudRepository<TargetRenjaKegiatanIndividu, Long> {
    Flux<TargetRenjaKegiatanIndividu> findAllByTahunAndBulan(String tahun, String bulan);
    Mono<TargetRenjaKegiatanIndividu> findByIndikatorRenjaKegiatanIndividuIdAndTahunAndBulan(Long indikatorRenjaKegiatanIndividuId, String tahun, String bulan);
    Mono<TargetRenjaKegiatanIndividu> findByKodeTarget(String kodeTarget);

    Mono<TargetRenjaKegiatanIndividu> findByIndikatorRenjaKegiatanIndividuIdAndKodeTargetAndTahunAndBulan(Long indikatorRenjaKegiatanIndividuId, String kodeTarget, String tahun, String bulan);
}
