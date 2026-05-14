package cc.kertaskerja.realisasi_opd_service.renja_pagu.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.renja.domain.JenisRenja;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class RenjaPaguService {
    private final RenjaPaguRepository renjaPaguRepository;

    public RenjaPaguService(RenjaPaguRepository renjaPaguRepository) {
        this.renjaPaguRepository = renjaPaguRepository;
    }

    public Flux<RenjaPagu> getAllRealisasiRenjaPagu() {
        return renjaPaguRepository.findAll();
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByTahunAndBulanAndKodeOpd(String tahun, String bulan, String kodeOpd) {
        return renjaPaguRepository.findAllByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    public Flux<RenjaPagu> getRealisasiRenjaPaguByKodeOpdAndTahunAndBulanAndJenisRenjaAndKodeRenjaAndRenjaId(
            String kodeOpd, String tahun, String bulan, String jenisRenja, String kodeRenja, String jenisRenjaId) {
        return renjaPaguRepository.findAllByKodeOpdAndTahunAndBulanAndJenisRenjaPaguAndKodeRenjaAndJenisRenjaId(
                kodeOpd, tahun, bulan, jenisRenja, kodeRenja, jenisRenjaId);
    }

    public Mono<Void> deleteRealisasiRenjaPaguByRenjaId(String jenisRenjaId) {
        return renjaPaguRepository.deleteByJenisRenjaId(jenisRenjaId);
    }

    public Mono<RenjaPagu> submitRealisasiRenjaPagu(String jenisRenjaId, JenisRenja jenisRenja, Integer pagu, Integer realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd, String kodeRenja) {
        return Mono.just(buildUncheckedRealisasiRenjaPagu(jenisRenjaId, jenisRenja, pagu, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, kodeRenja))
                .flatMap(renjaPaguRepository::save);
    }

    public static RenjaPagu buildUncheckedRealisasiRenjaPagu(String jenisRenjaId, JenisRenja jenisRenjaPagu, Integer pagu, Integer realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd, String kodeRenja) {
        return RenjaPagu.of(
                jenisRenjaId,
                jenisRenjaPagu, pagu, realisasi, satuan, tahun, bulan,
                jenisRealisasi, kodeOpd, kodeRenja,
                RenjaPaguStatus.UNCHECKED
        );
    }

}
