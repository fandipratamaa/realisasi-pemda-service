package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanOpdRepository extends ReactiveCrudRepository<RenjaSubKegiatanOpd, Long> {
    Mono<RenjaSubKegiatanOpd> findByKodeTarget(String kodeTarget);
    Mono<RenjaSubKegiatanOpd> findByIndikatorRenjaSubKegiatanOpdIdAndKodeTargetAndTahunAndBulan(
            Long indikatorRenjaSubKegiatanOpdId, String kodeTarget, String tahun, String bulan);
    Flux<RenjaSubKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);
}
