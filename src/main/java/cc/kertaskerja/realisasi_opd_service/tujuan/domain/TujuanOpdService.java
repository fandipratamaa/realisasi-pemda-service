package cc.kertaskerja.realisasi_opd_service.tujuan.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TujuanOpdService {
    private final TujuanOpdRepository tujuanOpdRepository;

    public TujuanOpdService(TujuanOpdRepository tujuanOpdRepository) {
        this.tujuanOpdRepository = tujuanOpdRepository;
    }

    public Flux<TujuanOpd> getAllRealisasiTujuanOpd() {
        return tujuanOpdRepository.findAll();
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndKodeOpd(String tahun, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunAndKodeOpd(tahun, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByKodeOpd(String kodeOpd) {
        return tujuanOpdRepository.findAllByKodeOpd(kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTujuanId(String tujuanId) {
        return tujuanOpdRepository.findAllByTujuanId(tujuanId);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByIndikatorId(String indikatorId) {
        return tujuanOpdRepository.findAllByIndikatorId(indikatorId);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByPeriodeRpjmd(String tahunAwal, String tahunAkhir, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunBetweenAndKodeOpd(tahunAwal, tahunAkhir, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndTujuanIdAndKodeOpd(String tahun, String tujuanId, String kodeOpd) {
        return tujuanOpdRepository.findAllByTahunAndTujuanIdAndKodeOpd(tahun, tujuanId, kodeOpd);
    }

    public Flux<TujuanOpd> getRealisasiTujuanOpdByTahunAndKodeOpdAndBulan(String tahun, String kodeOpd, String bulan) {
        return tujuanOpdRepository.findAllByTahunAndKodeOpdAndBulan(tahun, kodeOpd, bulan);
    }

    public Mono<TujuanOpd> getRealisasiTujuanOpdById(Long id) {
        return tujuanOpdRepository.findById(id);
    }

    public Mono<TujuanOpd> submitRealisasiTujuanOpd(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd, String rumusPerhitungan, String sumberData) {
        return Mono.just(buildUncheckedRealisasiTujuanOpd(tujuanId, indikatorId, targetId, target, realisasi, satuan, tahun, bulan, jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData))
                .flatMap(tujuanOpdRepository::save);
    }

    public static TujuanOpd buildUncheckedRealisasiTujuanOpd(String tujuanId, String indikatorId, String targetId, String target, Double realisasi, String satuan, String tahun, String bulan, JenisRealisasi jenisRealisasi, String kodeOpd, String rumusPerhitungan, String sumberData) {
        return TujuanOpd.of(
                tujuanId,
                "Realisasi Tujuan Opd " + tujuanId,
                indikatorId,
                "Realisasi Indikator Opd " + indikatorId,
                targetId, target, realisasi, satuan, tahun, bulan,
                jenisRealisasi, kodeOpd, rumusPerhitungan, sumberData,
                TujuanOpdStatus.UNCHECKED
        );
    }

}
