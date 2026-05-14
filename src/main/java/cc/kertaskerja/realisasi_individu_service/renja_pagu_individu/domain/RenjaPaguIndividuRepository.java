package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import cc.kertaskerja.renja.domain.JenisRenja;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaPaguIndividuRepository extends ReactiveCrudRepository<RenjaPaguIndividu, Long> {
    Flux<RenjaPaguIndividu> findAllByNipAndTahun(String nip, String tahun);

    Flux<RenjaPaguIndividu> findAllByNipAndTahunAndBulan(String nip, String tahun, String bulan);

    Flux<RenjaPaguIndividu> findAllByTahunAndBulanAndKodeOpd(String tahun, String bulan, String kodeOpd);

    Flux<RenjaPaguIndividu> findAllByNipAndTahunAndBulanAndKodeOpd(String nip, String tahun, String bulan, String kodeOpd);

    Flux<RenjaPaguIndividu> findAllByTahunAndNipAndJenisRenjaAndKodeRenja(
            String tahun, String nip, JenisRenja jenisRenja, String kodeRenja);

    Mono<RenjaPaguIndividu> findFirstByNipAndTahunAndJenisRenjaAndKodeRenja(String nip, String tahun, JenisRenja jenisRenja, String kodeRenja);

    Mono<Void> deleteByTahunAndNipAndJenisRenjaAndKodeRenja(String tahun, String nip, JenisRenja jenisRenja, String kodeRenja);
}
