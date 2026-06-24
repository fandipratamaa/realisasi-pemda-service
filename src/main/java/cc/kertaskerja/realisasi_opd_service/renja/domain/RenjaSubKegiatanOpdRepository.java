package cc.kertaskerja.realisasi_opd_service.renja.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanOpdRepository extends ReactiveCrudRepository<RenjaSubKegiatanOpd, Long> {
    Mono<RenjaSubKegiatanOpd> findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeSubkegiatan, String kodeIndikator, String kodeTarget, String tahun, String bulan);

    Flux<RenjaSubKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);

    Flux<RenjaSubKegiatanOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

    Mono<RenjaSubKegiatanOpd> findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahun(
            String kodeOpd, String kodeSubkegiatan, String kodeIndikator, String kodeTarget, String tahun);
}
