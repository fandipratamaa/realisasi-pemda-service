package cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain;

import cc.kertaskerja.renja.domain.JenisRenja;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaTargetIndividuRepository extends ReactiveCrudRepository<RenjaTargetIndividu, Long> {
    Flux<RenjaTargetIndividu> findAllByTahunAndNip(String tahun, String nip);

    Flux<RenjaTargetIndividu> findAllByTahunAndNipAndBulan(String tahun, String nip, String bulan);

    Flux<RenjaTargetIndividu> findAllByTahunBetweenAndNip(String tahunAwal, String tahunAkhir, String nip);

    Flux<RenjaTargetIndividu> findAllByNip(String nip);

    Flux<RenjaTargetIndividu> findAllByTahunAndJenisRenjaAndKodeRenjaAndNip(String tahun, JenisRenja jenisRenja, String kodeRenja, String nip);

    Flux<RenjaTargetIndividu> findAllByJenisRenjaAndKodeRenjaAndNip(JenisRenja jenisRenja, String kodeRenja, String nip);

    Mono<RenjaTargetIndividu> findFirstByNipAndTahunAndKodeRenjaAndJenisRenja(String nip, String tahun, String kodeRenja, JenisRenja jenisRenja);

    Mono<RenjaTargetIndividu> findFirstByTargetId(String targetId);

    Mono<Void> deleteByTahunAndNipAndJenisRenjaAndKodeRenja(String tahun, String nip, JenisRenja jenisRenja, String kodeRenja);
}
