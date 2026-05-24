package cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaSubKegiatanOpdRepository extends ReactiveCrudRepository<IndikatorRenjaSubKegiatanOpd, Long> {
    Mono<IndikatorRenjaSubKegiatanOpd> findByRenjaSubKegiatanOpdIdAndKodeIndikatorAndTahunAndBulan(
            Long renjaSubKegiatanOpdId, String kodeIndikator, String tahun, String bulan);

    Flux<IndikatorRenjaSubKegiatanOpd> findAllByRenjaSubKegiatanOpdId(Long renjaSubKegiatanOpdId);
}
