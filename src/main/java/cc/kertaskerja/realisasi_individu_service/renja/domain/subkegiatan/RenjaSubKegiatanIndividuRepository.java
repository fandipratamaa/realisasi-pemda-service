package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanIndividuRepository extends ReactiveCrudRepository<RenjaSubKegiatanIndividu, Long> {
    Mono<RenjaSubKegiatanIndividu> findByKodeSubKegiatan(String kodeSubKegiatan);
    Flux<RenjaSubKegiatanIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
}
