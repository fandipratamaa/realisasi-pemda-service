package cc.kertaskerja.realisasi_opd_service.renja.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanOpdRepository extends ReactiveCrudRepository<RenjaKegiatanOpd, Long> {
    Mono<RenjaKegiatanOpd> findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeKegiatan, String kodeIndikator, String kodeTarget, String tahun, String bulan);

    Flux<RenjaKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);

    Flux<RenjaKegiatanOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

    Mono<RenjaKegiatanOpd> findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahun(
            String kodeOpd, String kodeKegiatan, String kodeIndikator, String kodeTarget, String tahun);
}
