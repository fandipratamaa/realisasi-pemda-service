package cc.kertaskerja.realisasi_pemda_service.sasaran.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SasaranRepository extends ReactiveCrudRepository<Sasaran, Long> {
    Flux<Sasaran> findAllByTahun(String tahun);

    Flux<Sasaran> findAllBySasaranId(String sasaranId);

    Flux<Sasaran> findAllByTahunAndSasaranId(String tahun, String sasaranId);

    Flux<Sasaran> findAllByTahunBetween(String tahunAfter, String tahunBefore);

    Flux<Sasaran> findAllByTahunAndBulan(String tahun, String bulan);
}
