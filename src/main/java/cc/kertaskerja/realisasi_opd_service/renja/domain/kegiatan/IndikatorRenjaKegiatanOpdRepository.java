package cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IndikatorRenjaKegiatanOpdRepository extends ReactiveCrudRepository<IndikatorRenjaKegiatanOpd, Long> {
    Mono<IndikatorRenjaKegiatanOpd> findByRenjaKegiatanOpdIdAndKodeIndikatorAndTahunAndBulan(
            Long renjaKegiatanOpdId, String kodeIndikator, String tahun, String bulan);

    Flux<IndikatorRenjaKegiatanOpd> findAllByRenjaKegiatanOpdId(Long renjaKegiatanOpdId);
}
