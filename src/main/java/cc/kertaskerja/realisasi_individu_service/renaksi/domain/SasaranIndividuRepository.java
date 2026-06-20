package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SasaranIndividuRepository extends ReactiveCrudRepository<SasaranIndividu, Long> {
    Flux<SasaranIndividu> findAllByNipAndTahun(String nip, String tahun);

    Flux<SasaranIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

    Flux<SasaranIndividu> findAllByNipAndKodeOpdAndTahunAndBulan(String nip, String kodeOpd, String tahun, String bulan);

    Mono<SasaranIndividu> findFirstByNipAndTahunAndBulanAndKodeSasaran(String nip, String tahun, String bulan, String kodeSasaran);

    Mono<SasaranIndividu> findFirstByKodeOpdAndNipAndTahunAndBulanAndKodeSasaran(String kodeOpd, String nip, String tahun, String bulan, String kodeSasaran);
}
