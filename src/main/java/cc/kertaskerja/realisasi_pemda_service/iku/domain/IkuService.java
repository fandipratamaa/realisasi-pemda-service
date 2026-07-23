package cc.kertaskerja.realisasi_pemda_service.iku.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_pemda_service.iku.web.FaktorPenghambatIkuRequest;
import cc.kertaskerja.realisasi_pemda_service.iku.web.FaktorPenunjangIkuRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.SasaranRepository;
import cc.kertaskerja.realisasi_pemda_service.sasaran.domain.SasaranService;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenghambatSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.sasaran.web.FaktorPenunjangSasaranRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanRepository;
import cc.kertaskerja.realisasi_pemda_service.tujuan.domain.TujuanService;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenghambatRequest;
import cc.kertaskerja.realisasi_pemda_service.tujuan.web.FaktorPenunjangRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IkuService {
    private final TujuanRepository tujuanRepository;
    private final SasaranRepository sasaranRepository;
    private final TujuanService tujuanService;
    private final SasaranService sasaranService;

    public IkuService(TujuanRepository tujuanRepository, SasaranRepository sasaranRepository, TujuanService tujuanService, SasaranService sasaranService) {
        this.tujuanRepository = tujuanRepository;
        this.sasaranRepository = sasaranRepository;
        this.tujuanService = tujuanService;
        this.sasaranService = sasaranService;
    }

    public Flux<Iku> getAllIku() {
        Flux<Iku> ikuTujuan = tujuanRepository.findAll()
                .map(t -> buildIkuTujuan(
                        t.kodeIndikator(),
                        "",
                        t.kodeTarget(),
                        "",
                        t.realisasi(),
                        t.satuan(),
                        "",
                        t.tahun(),
                        t.faktorPenunjang(),
                        t.faktorPenghambat(),
                        t.jenisRealisasi(),
                        "",
                        "",
                        t.bulan()
                ));

        Flux<Iku> ikuSasaran = sasaranRepository.findAll()
                .map(s -> buildIkuSasaran(
                        s.indikatorId(),
                        s.indikator(),
                        s.targetId(),
                        s.target(),
                        s.realisasi(),
                        s.satuan(),
                        s.capaian(),
                        s.tahun(),
                        s.faktorPenunjang(),
                        s.faktorPenghambat(),
                        s.jenisRealisasi(),
                        s.rumusPerhitungan(),
                        s.sumberData(),
                        s.bulan()
                ));

        return Flux.merge(ikuTujuan, ikuSasaran);
    }

    public Flux<Iku> getAllIkuByTahunAndBulan(String tahun, String bulan) {
        Flux<Iku> ikuTujuan = tujuanRepository.findAll()
                .filter(t -> tahun.equals(t.tahun()) && bulan.equals(t.bulan()))
                .map(t -> buildIkuTujuan(
                        t.kodeIndikator(),
                        "",
                        t.kodeTarget(),
                        "",
                        t.realisasi(),
                        t.satuan(),
                        "",
                        t.tahun(),
                        t.faktorPenunjang(),
                        t.faktorPenghambat(),
                        t.jenisRealisasi(),
                        "",
                        "",
                        t.bulan()
                ));

        Flux<Iku> ikuSasaran = sasaranRepository.findAll()
                .filter(s -> tahun.equals(s.tahun()) && bulan.equals(s.bulan()))
                .map(s -> buildIkuSasaran(
                        s.indikatorId(),
                        s.indikator(),
                        s.targetId(),
                        s.target(),
                        s.realisasi(),
                        s.satuan(),
                        s.capaian(),
                        s.tahun(),
                        s.faktorPenunjang(),
                        s.faktorPenghambat(),
                        s.jenisRealisasi(),
                        s.rumusPerhitungan(),
                        s.sumberData(),
                        s.bulan()
                ));

        return Flux.merge(ikuTujuan, ikuSasaran);
    }


    public static Iku buildIkuTujuan(String indikatorId, String indikator, String targetId, String target, Double realisasi, String satuan, String capaian, String tahun, String faktorPenunjang, String faktorPenghambat, JenisRealisasi jenisRealisasi, String rumusPerhitungan, String sumberData, String bulan) {
        return Iku.of(indikatorId, indikator, targetId, target, realisasi, satuan, capaian, tahun, faktorPenunjang, faktorPenghambat, jenisRealisasi, JenisIku.TUJUAN, rumusPerhitungan, sumberData, bulan);
    }

    public static Iku buildIkuSasaran(String indikatorId, String indikator, String targetId, String target, Double realisasi, String satuan, String capaian, String tahun, String faktorPenunjang, String faktorPenghambat, JenisRealisasi jenisRealisasi, String rumusPerhitungan, String sumberData, String bulan) {
        return Iku.of(indikatorId, indikator, targetId, target, realisasi, satuan, capaian, tahun, faktorPenunjang, faktorPenghambat, jenisRealisasi, JenisIku.SASARAN, rumusPerhitungan, sumberData, bulan);
    }

    public Mono<Iku> updateFaktorPenunjang(FaktorPenunjangIkuRequest req) {
        if (req.jenisIku() == JenisIku.TUJUAN) {
            return tujuanService.updateFaktorPenunjang(new FaktorPenunjangRequest(
                    req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenunjang()
            )).map(t -> buildIkuTujuan(
                    t.kodeIndikator(), "", t.kodeTarget(), "",
                    t.realisasi(), t.satuan(), "", t.tahun(),
                    t.faktorPenunjang(), t.faktorPenghambat(), t.jenisRealisasi(),
                    "", "", t.bulan()
            ));
        }
        return sasaranService.updateFaktorPenunjang(new FaktorPenunjangSasaranRequest(
                req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenunjang()
        )).map(s -> buildIkuSasaran(
                s.indikatorId(), s.indikator(), s.targetId(), s.target(),
                s.realisasi(), s.satuan(), s.capaian(), s.tahun(),
                s.faktorPenunjang(), s.faktorPenghambat(), s.jenisRealisasi(),
                s.rumusPerhitungan(), s.sumberData(), s.bulan()
        ));
    }

    public Mono<Iku> updateFaktorPenghambat(FaktorPenghambatIkuRequest req) {
        if (req.jenisIku() == JenisIku.TUJUAN) {
            return tujuanService.updateFaktorPenghambat(new FaktorPenghambatRequest(
                    req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenghambat()
            )).map(t -> buildIkuTujuan(
                    t.kodeIndikator(), "", t.kodeTarget(), "",
                    t.realisasi(), t.satuan(), "", t.tahun(),
                    t.faktorPenunjang(), t.faktorPenghambat(), t.jenisRealisasi(),
                    "", "", t.bulan()
            ));
        }
        return sasaranService.updateFaktorPenghambat(new FaktorPenghambatSasaranRequest(
                req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenghambat()
        )).map(s -> buildIkuSasaran(
                s.indikatorId(), s.indikator(), s.targetId(), s.target(),
                s.realisasi(), s.satuan(), s.capaian(), s.tahun(),
                s.faktorPenunjang(), s.faktorPenghambat(), s.jenisRealisasi(),
                s.rumusPerhitungan(), s.sumberData(), s.bulan()
        ));
    }
}
