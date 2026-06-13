package cc.kertaskerja.realisasi_opd_service.sasaran.domain.target;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TargetIndikatorSasaranOpdRepository extends ReactiveCrudRepository<TargetIndikatorSasaranOpd, Long> {
    Mono<TargetIndikatorSasaranOpd> findFirstByIndikatorSasaranIdAndKodeTargetAndTahunAndBulan(
            Long indikatorSasaranId,
            String kodeTarget,
            String tahun,
            String bulan
    );

    Flux<TargetIndikatorSasaranOpd> findAllByIndikatorSasaranId(Long indikatorSasaranId);

    @Query("""
            SELECT t.* FROM target_indikator_sasaran_opd t
            JOIN indikator_sasaran_opd i ON t.indikator_sasaran_id = i.id
            JOIN sasaran_opd s ON i.sasaran_opd_id = s.id
            WHERE s.kode_opd = :kodeOpd
              AND s.kode_sasaran_opd = :kodeSasaranOpd
              AND i.kode_indikator = :kodeIndikator
              AND t.kode_target = :kodeTarget
              AND t.tahun = :tahun
              AND t.bulan = :bulan
            LIMIT 1
            """)
    Mono<TargetIndikatorSasaranOpd> findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorSasaranOpdAndKodeTargetSasaranOpdAndTahunAndBulan(
            String kodeOpd,
            String kodeSasaranOpd,
            String kodeIndikator,
            String kodeTarget,
            String tahun,
            String bulan
    );
}
