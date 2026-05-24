package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanIndividuRepository extends ReactiveCrudRepository<RenjaKegiatanIndividu, Long> {
    Mono<RenjaKegiatanIndividu> findByKodeKegiatan(String kodeKegiatan);
    Flux<RenjaKegiatanIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
}
