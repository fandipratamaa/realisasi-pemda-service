package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiIndividuRepository extends ReactiveCrudRepository<RenaksiIndividu, Long> {
    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndKodeSasaran(String kodeOpd, String nip, String kodeSasaran);

    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);

    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);

    Mono<RenaksiIndividu> findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
            String kodeOpd, String nip, String kodeSasaran,
            String kodeRenaksi, String kodeIndikator, String kodeTarget,
            String tahun, String bulan
    );
}
