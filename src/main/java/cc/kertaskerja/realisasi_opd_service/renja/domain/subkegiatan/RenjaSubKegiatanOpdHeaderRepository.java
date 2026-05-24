package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanOpdHeaderRepository extends ReactiveCrudRepository<RenjaSubKegiatanOpdHeader, Long> {
    Mono<RenjaSubKegiatanOpdHeader> findByKodeOpdAndKodeSubKegiatanAndTahunAndBulan(
            String kodeOpd, String kodeSubKegiatan, String tahun, String bulan);
}
