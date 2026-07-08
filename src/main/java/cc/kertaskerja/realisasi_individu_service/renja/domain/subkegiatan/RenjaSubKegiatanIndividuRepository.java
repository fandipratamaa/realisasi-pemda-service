package cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenjaSubKegiatanIndividuRepository extends ReactiveCrudRepository<RenjaSubKegiatanIndividu, Long> {
        Mono<RenjaSubKegiatanIndividu> findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        String kodeOpd, String kodeSubkegiatan, String kodeIndikator, String kodeTarget, String tahun,
                        String bulan);

        Flux<RenjaSubKegiatanIndividu> findAllByTahunAndBulan(String tahun, String bulan);

        Flux<RenjaSubKegiatanIndividu> findAllByKodeOpdAndTahun(String kodeOpd, String tahun);

        Flux<RenjaSubKegiatanIndividu> findAllByKodeOpdAndNipAndTahun(String kodeOpd, String nip, String tahun);

        Flux<RenjaSubKegiatanIndividu> findAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan);

        Flux<RenjaSubKegiatanIndividu> findAllByKodeOpdAndNipAndTahunAndBulan(String kodeOpd, String nip, String tahun,
                        String bulan);

    @Query("""
            SELECT COALESCE(SUM(pagu), 0)
            FROM realisasi_target_renja_subkegiatan_individu
            WHERE kode_opd = :kodeOpd
              AND tahun = :tahun
              AND bulan = :bulan
              AND kode_subkegiatan LIKE :kodeSubKegiatanPrefix
            """)
    Mono<java.math.BigDecimal> sumPaguByKodeSubKegiatanPrefix(
            String kodeOpd,
            String tahun,
            String bulan,
            String kodeSubKegiatanPrefix);
}
