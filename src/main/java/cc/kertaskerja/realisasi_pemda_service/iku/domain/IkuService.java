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
import cc.kertaskerja.integration.penetapan.PenetapanSasaranPemdaClient;
import cc.kertaskerja.integration.penetapan.PenetapanTujuanPemdaClient;
import cc.kertaskerja.integration.penetapan.sasaran_pemda.PenetapanSasaranPemda;
import cc.kertaskerja.integration.penetapan.tujuan_pemda.PenetapanTujuanPemda;
import java.util.List;

@Service
public class IkuService {
    private final TujuanRepository tujuanRepository;
    private final SasaranRepository sasaranRepository;
    private final TujuanService tujuanService;
    private final SasaranService sasaranService;
    private final PenetapanTujuanPemdaClient penetapanTujuanPemdaClient;
    private final PenetapanSasaranPemdaClient penetapanSasaranPemdaClient;

    public IkuService(TujuanRepository tujuanRepository, SasaranRepository sasaranRepository,
            TujuanService tujuanService, SasaranService sasaranService,
            PenetapanTujuanPemdaClient penetapanTujuanPemdaClient,
            PenetapanSasaranPemdaClient penetapanSasaranPemdaClient) {
        this.tujuanRepository = tujuanRepository;
        this.sasaranRepository = sasaranRepository;
        this.tujuanService = tujuanService;
        this.sasaranService = sasaranService;
        this.penetapanTujuanPemdaClient = penetapanTujuanPemdaClient;
        this.penetapanSasaranPemdaClient = penetapanSasaranPemdaClient;
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
                        t.bulan()));

        Flux<Iku> ikuSasaran = sasaranRepository.findAll()
                .map(s -> buildIkuSasaran(
                        s.kodeIndikator(),
                        "",
                        s.kodeTarget(),
                        "",
                        s.realisasi(),
                        s.satuan(),
                        "",
                        s.tahun(),
                        s.faktorPenunjang(),
                        s.faktorPenghambat(),
                        s.jenisRealisasi(),
                        "",
                        "",
                        s.bulan()));

        return Flux.merge(ikuTujuan, ikuSasaran);
    }

    public Flux<Iku> getAllIkuByTahunAndBulan(String tahun, String bulan) {
        Mono<List<PenetapanTujuanPemda.TujuanPenetapanPemdaData>> penetapanTujuanData = penetapanTujuanPemdaClient
                .fetchTujuanPemda(Integer.parseInt(tahun)).defaultIfEmpty(List.of());
        Mono<List<PenetapanSasaranPemda.SasaranPenetapanPemdaData>> penetapanSasaranData = penetapanSasaranPemdaClient
                .fetchSasaranPemda(Integer.parseInt(tahun)).defaultIfEmpty(List.of());

        Flux<Iku> ikuTujuan = tujuanRepository.findAll()
                .filter(t -> tahun.equals(t.tahun()) && bulan.equals(t.bulan()))
                .collectList()
                .zipWith(penetapanTujuanData, (realisasiList, penetapanList) -> {
                    return realisasiList.stream().map(t -> {
                        String indikator = "";
                        String targetVal = "";
                        String rumusPerhitungan = "";
                        String sumberData = "";
                        for (var p : penetapanList) {
                            if (p.kodeTujuanPemda().equals(t.kodeTujuanPemda())) {
                                for (var ind : p.indikators()) {
                                    if (ind.kodeIndikator().equals(t.kodeIndikator())) {
                                        indikator = ind.indikator();
                                        rumusPerhitungan = ind.rumusPerhitungan();
                                        sumberData = ind.sumberData();
                                        for (var trg : ind.targets()) {
                                            if (trg.kodeTarget().equals(t.kodeTarget())) {
                                                targetVal = trg.target() != null ? String.valueOf(trg.target()) : "";
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        return buildIkuTujuan(
                                t.kodeIndikator(), indikator, t.kodeTarget(), targetVal,
                                t.realisasi(), t.satuan(), "", t.tahun(), t.faktorPenunjang(),
                                t.faktorPenghambat(), t.jenisRealisasi(), rumusPerhitungan, sumberData, t.bulan());
                    }).toList();
                }).flatMapIterable(list -> list);

        Flux<Iku> ikuSasaran = sasaranRepository.findAll()
                .filter(s -> tahun.equals(s.tahun()) && bulan.equals(s.bulan()))
                .collectList()
                .zipWith(penetapanSasaranData, (realisasiList, penetapanList) -> {
                    return realisasiList.stream().map(s -> {
                        String indikator = "";
                        String targetVal = "";
                        String rumusPerhitungan = "";
                        String sumberData = "";
                        for (var p : penetapanList) {
                            if (p.kodeSasaranPemda().equals(s.kodeSasaranPemda())) {
                                for (var ind : p.indikators()) {
                                    if (ind.kodeIndikator().equals(s.kodeIndikator())) {
                                        indikator = ind.indikator();
                                        rumusPerhitungan = ind.rumusPerhitungan();
                                        sumberData = ind.sumberData();
                                        for (var trg : ind.targets()) {
                                            if (trg.kodeTarget().equals(s.kodeTarget())) {
                                                targetVal = trg.target() != null ? String.valueOf(trg.target()) : "";
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        return buildIkuSasaran(
                                s.kodeIndikator(), indikator, s.kodeTarget(), targetVal,
                                s.realisasi(), s.satuan(), "", s.tahun(), s.faktorPenunjang(),
                                s.faktorPenghambat(), s.jenisRealisasi(), rumusPerhitungan, sumberData, s.bulan());
                    }).toList();
                }).flatMapIterable(list -> list);

        return Flux.merge(ikuTujuan, ikuSasaran);
    }

    public static Iku buildIkuTujuan(String indikatorId, String indikator, String targetId, String target,
            Double realisasi, String satuan, String capaian, String tahun, String faktorPenunjang,
            String faktorPenghambat, JenisRealisasi jenisRealisasi, String rumusPerhitungan, String sumberData,
            String bulan) {
        return Iku.of(indikatorId, indikator, targetId, target, realisasi, satuan, capaian, tahun, faktorPenunjang,
                faktorPenghambat, jenisRealisasi, JenisIku.TUJUAN, rumusPerhitungan, sumberData, bulan);
    }

    public static Iku buildIkuSasaran(String indikatorId, String indikator, String targetId, String target,
            Double realisasi, String satuan, String capaian, String tahun, String faktorPenunjang,
            String faktorPenghambat, JenisRealisasi jenisRealisasi, String rumusPerhitungan, String sumberData,
            String bulan) {
        return Iku.of(indikatorId, indikator, targetId, target, realisasi, satuan, capaian, tahun, faktorPenunjang,
                faktorPenghambat, jenisRealisasi, JenisIku.SASARAN, rumusPerhitungan, sumberData, bulan);
    }

    public Mono<Iku> updateFaktorPenunjang(FaktorPenunjangIkuRequest req) {
        if (req.jenisIku() == JenisIku.TUJUAN) {
            return tujuanService.updateFaktorPenunjang(new FaktorPenunjangRequest(
                    req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenunjang()))
                    .map(t -> buildIkuTujuan(
                            t.kodeIndikator(), "", t.kodeTarget(), "",
                            t.realisasi(), t.satuan(), "", t.tahun(),
                            t.faktorPenunjang(), t.faktorPenghambat(), t.jenisRealisasi(),
                            "", "", t.bulan()));
        }
        return sasaranService.updateFaktorPenunjang(new FaktorPenunjangSasaranRequest(
                req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenunjang()))
                .map(s -> buildIkuSasaran(
                        s.kodeIndikator(), "", s.kodeTarget(), "",
                        s.realisasi(), s.satuan(), "", s.tahun(),
                        s.faktorPenunjang(), s.faktorPenghambat(), s.jenisRealisasi(),
                        "", "", s.bulan()));
    }

    public Mono<Iku> updateFaktorPenghambat(FaktorPenghambatIkuRequest req) {
        if (req.jenisIku() == JenisIku.TUJUAN) {
            return tujuanService.updateFaktorPenghambat(new FaktorPenghambatRequest(
                    req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenghambat()))
                    .map(t -> buildIkuTujuan(
                            t.kodeIndikator(), "", t.kodeTarget(), "",
                            t.realisasi(), t.satuan(), "", t.tahun(),
                            t.faktorPenunjang(), t.faktorPenghambat(), t.jenisRealisasi(),
                            "", "", t.bulan()));
        }
        return sasaranService.updateFaktorPenghambat(new FaktorPenghambatSasaranRequest(
                req.jenisId(), req.indikatorId(), req.targetId(), req.tahun(), req.bulan(), req.faktorPenghambat()))
                .map(s -> buildIkuSasaran(
                        s.kodeIndikator(), "", s.kodeTarget(), "",
                        s.realisasi(), s.satuan(), "", s.tahun(),
                        s.faktorPenunjang(), s.faktorPenghambat(), s.jenisRealisasi(),
                        "", "", s.bulan()));
    }
}
