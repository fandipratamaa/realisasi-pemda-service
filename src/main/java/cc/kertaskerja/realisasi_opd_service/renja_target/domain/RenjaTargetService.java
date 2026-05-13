package cc.kertaskerja.realisasi_opd_service.renja_target.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RenjaTargetService {
    private final RenjaTargetRepository renjaTargetRepository;

    public RenjaTargetService(RenjaTargetRepository renjaTargetRepository) {
        this.renjaTargetRepository = renjaTargetRepository;
    }

    public Flux<RenjaTarget> getAllRealisasiRenjaTarget() {
        return renjaTargetRepository.findAll();
    }

public Mono<RenjaTarget> submitRealisasiRenjaTarget(String jenisRenjaId,
            JenisRenja jenisRenjaTarget,
            String indikatorId, String indikator,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi,
            String kodeOpd, String kodeRenja) {
        return Mono.just(buildUncheckedRealisasiRenjaTarget(
                jenisRenjaId, jenisRenjaTarget, indikatorId, indikator, targetId, target,
                realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, kodeRenja))
                .flatMap(renjaTargetRepository::save);
    }

    public static RenjaTarget buildUncheckedRealisasiRenjaTarget(String jenisRenjaId,
            JenisRenja jenisRenjaTarget,
            String indikatorId, String indikator,
            String targetId, String target, Integer realisasi,
            String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi,
            String kodeOpd, String kodeRenja) {
        return RenjaTarget.of(
                jenisRenjaId,
                jenisRenjaTarget,
                indikatorId,
                indikator,
                targetId,
                target,
                realisasi,
                satuan,
                tahun,
                bulan,
                jenisRealisasi,
                kodeOpd,
                kodeRenja,
                RenjaTargetStatus.UNCHECKED);
    }

public Flux<RenjaTarget> getRealisasiRenjaTargetByFilters(String kodeOpd, String tahun, String bulan) {
        return renjaTargetRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    public Mono<Void> deleteRealisasiRenjaTarget(String jenisRenjaId) {
        return renjaTargetRepository.deleteByJenisRenjaId(jenisRenjaId);
    }

    public Mono<RenjaTarget> getRealisasiRenjaTargetByFilters(
            String kodeOpd, String tahun, String bulan, 
            JenisRenja jenisRenja, String kodeRenja, String jenisRenjaId) {
        return renjaTargetRepository.findFirstByKodeOpdAndTahunAndBulanAndJenisRenjaTargetAndKodeRenjaAndJenisRenjaId(
                kodeOpd, tahun, bulan, jenisRenja, kodeRenja, jenisRenjaId);
    }
}
