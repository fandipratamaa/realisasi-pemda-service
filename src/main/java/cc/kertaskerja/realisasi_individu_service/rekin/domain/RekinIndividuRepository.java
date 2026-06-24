package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RekinIndividuRepository extends ReactiveCrudRepository<RekinIndividu, Long> {
    Flux<RekinIndividu> findAllByNipAndTahun(String nip, String tahun);

    Flux<RekinIndividu> findAllByNipAndTahunAndBulan(String nip, String tahun, String bulan);

    Flux<RekinIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

    Flux<RekinIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);

    Flux<RekinIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);

    Mono<RekinIndividu> findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
            String kodeOpd, String nip, String tahun, String bulan,
            String kodePkRekin, String kodeIndikatorPkRekin, String kodeTargetPkRekin);
}
