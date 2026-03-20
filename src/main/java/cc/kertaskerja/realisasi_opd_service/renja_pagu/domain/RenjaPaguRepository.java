package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface RenjaPaguRepository extends ReactiveCrudRepository<RenjaPagu, Long> {
    Flux<RenjaPagu> findAllByRenjaPaguId(String renjaPaguId);

    Flux<RenjaPagu> findAllByTahunAndKodeOpd(String tahun, String kodeOpd);

    Flux<RenjaPagu> findAllByTahunBetweenAndKodeOpd(String tahunAwal, String tahunAkhir, String kodeOpd);

    Flux<RenjaPagu> findAllByKodeOpd(String kodeOpd);

    Flux<RenjaPagu> findAllByTahunAndRenjaPaguIdAndKodeOpd(String tahun, String renjaPaguId, String kodeOpd);
}
