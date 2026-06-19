package cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanOpdIndividuRepository extends ReactiveCrudRepository<RenjaKegiatanOpd, Long> {
    Mono<RenjaKegiatanOpd> findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String kodeKegiatan, String kodeIndikator, String kodeTarget, String tahun, String bulan);
    Flux<RenjaKegiatanOpd> findAllByTahunAndBulan(String tahun, String bulan);
    Flux<RenjaKegiatanOpd> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);
    Flux<RenjaKegiatanOpd> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);
    Flux<RenjaKegiatanOpd> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);
}
