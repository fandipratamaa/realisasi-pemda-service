package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenaksiIndividuRepository extends ReactiveCrudRepository<RenaksiIndividu, Long> {
    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndKodeRekin(String kodeOpd, String nip, String kodeRekin);

    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun, String bulan);

    Flux<RenaksiIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

    Flux<RenaksiIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);

    Flux<RenaksiIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

    Mono<RenaksiIndividu> findFirstByKodeOpdAndNipAndKodeRekinAndKodeRenaksiAndKodePelaksanaanAndTahunAndBulan(
            String kodeOpd, String nip, String kodeRekin,
            String kodeRenaksi, String kodePelaksanaan,
            String tahun, String bulan
    );
}
