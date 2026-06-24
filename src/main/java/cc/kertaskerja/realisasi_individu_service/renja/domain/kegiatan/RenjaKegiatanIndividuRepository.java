package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanIndividuRepository extends ReactiveCrudRepository<RenjaKegiatanIndividu, Long> {
    Mono<RenjaKegiatanIndividu> findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeKegiatan, String kodeIndikator, String kodeTarget, String tahun, String bulan);
    Flux<RenjaKegiatanIndividu> findAllByTahunAndBulan(String tahun, String bulan);
    Flux<RenjaKegiatanIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
    Flux<RenjaKegiatanIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);
    Flux<RenjaKegiatanIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);
    Flux<RenjaKegiatanIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);
}
