package cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanOpdRepository extends ReactiveCrudRepository<RenjaKegiatanOpd, Long> {
    Mono<RenjaKegiatanOpd> findByKodeTarget(String kodeTarget);
    Mono<RenjaKegiatanOpd> findByIndikatorRenjaKegiatanOpdIdAndKodeTargetAndTahunAndBulan(
            Long indikatorRenjaKegiatanOpdId, String kodeTarget, String tahun, String bulan);
    Flux<RenjaKegiatanOpd> findAllByIndikatorRenjaKegiatanOpdId(Long indikatorRenjaKegiatanOpdId);
    Flux<RenjaKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);
}
