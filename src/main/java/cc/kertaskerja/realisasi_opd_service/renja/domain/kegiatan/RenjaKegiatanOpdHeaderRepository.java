package cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RenjaKegiatanOpdHeaderRepository extends ReactiveCrudRepository<RenjaKegiatanOpdHeader, Long> {
    Mono<RenjaKegiatanOpdHeader> findByKodeOpdAndKodeKegiatanAndTahunAndBulan(
            String kodeOpd, String kodeKegiatan, String tahun, String bulan);
}
