package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanOpdIndividuRepository extends ReactiveCrudRepository<RenjaSubKegiatanOpd, Long> {
    Mono<RenjaSubKegiatanOpd> findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeSubkegiatan, String kodeIndikator, String kodeTarget, String tahun, String bulan);
    Flux<RenjaSubKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);
    Flux<RenjaSubKegiatanOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
    Flux<RenjaSubKegiatanOpd> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);
    Flux<RenjaSubKegiatanOpd> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);
}
