package cc.kertaskerja.realisasi_opd_service.renaksi.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenaksiOpdClient;
import cc.kertaskerja.integration.penetapan.renaksi.PenetapanRenaksiOpd;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.FaktorPenghambatRenaksiOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.FaktorPenunjangRenaksiOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.LaporanRealisasiRenaksiOpdResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.PenetapanRenaksiOpdListResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.RenaksiOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.renaksi.web.RenaksiOpdRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

@Service
public class RenaksiOpdService {
    private final RenaksiOpdRepository renaksiOpdRepository;
    private final PenetapanRenaksiOpdClient penetapanRenaksiOpdClient;

    private record RealisasiData(BigDecimal realisasi, String faktorPenunjang, String faktorPenghambat) {
        static RealisasiData merge(RealisasiData a, RealisasiData b) {
            return new RealisasiData(
                    a.realisasi().add(b.realisasi()),
                    b.faktorPenunjang() != null && !b.faktorPenunjang().isBlank()
                            ? b.faktorPenunjang() : a.faktorPenunjang(),
                    b.faktorPenghambat() != null && !b.faktorPenghambat().isBlank()
                            ? b.faktorPenghambat() : a.faktorPenghambat()
            );
        }
    }

    public RenaksiOpdService(
            RenaksiOpdRepository renaksiOpdRepository,
            PenetapanRenaksiOpdClient penetapanRenaksiOpdClient
    ) {
        this.renaksiOpdRepository = renaksiOpdRepository;
        this.penetapanRenaksiOpdClient = penetapanRenaksiOpdClient;
    }

    public Mono<PenetapanRenaksiOpdListResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        String tahunStr = String.valueOf(tahun);

        return penetapanRenaksiOpdClient.fetchRenaksiOpd(kodeOpd, tahun)
                .flatMap(root -> {
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(buildPenetapanResponse(root, List.of(), kodeOpd, tahun, null));
                    }
                    return renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahunStr, bulan)
                            .collectList()
                            .map(realisasiList -> buildPenetapanResponse(root, realisasiList, kodeOpd, tahun, bulan));
                })
                .defaultIfEmpty(new PenetapanRenaksiOpdListResponse(
                        kodeOpd, tahun, parseInteger(bulan), null, null, List.of()));
    }

    public Mono<String> syncPenetapanRenaksiOpd(String kodeOpd, int tahun) {
        return penetapanRenaksiOpdClient.syncRenaksiOpd(kodeOpd, tahun);
    }

    public Mono<RenaksiOpd> syncRealisasiFromRenaksiIndividu(
            String kodeOpd,
            String tahun,
            String bulan,
            String kodeRencanaAksiOpd,
            BigDecimal realisasi
    ) {
        if (!hasText(kodeRencanaAksiOpd)) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Kode rencana aksi OPD tidak boleh kosong"));
        }
        BigDecimal nilaiRealisasi = realisasi != null ? realisasi : BigDecimal.ZERO;

        return renaksiOpdRepository
                .findFirstByKodeOpdAndTahunAndBulanAndKodeRencanaAksiOpd(
                        kodeOpd, tahun, bulan, kodeRencanaAksiOpd)
                .flatMap(existing -> renaksiOpdRepository.save(new RenaksiOpd(
                        existing.id(),
                        existing.kodeOpd(), existing.tahun(), existing.bulan(),
                        existing.kodeRencanaAksiOpd(), nilaiRealisasi,
                        existing.jenisRealisasi() != null ? existing.jenisRealisasi() : JenisRealisasi.NAIK,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdBy(), existing.createdDate(),
                        existing.lastModifiedDate(), existing.lastModifiedBy()
                )))
                .switchIfEmpty(Mono.defer(() -> renaksiOpdRepository.save(RenaksiOpd.of(
                        kodeOpd, tahun, bulan, kodeRencanaAksiOpd, nilaiRealisasi,
                        JenisRealisasi.NAIK, "", ""
                ))));
    }

    private PenetapanRenaksiOpdListResponse buildPenetapanResponse(
            PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot root,
            List<RenaksiOpd> realisasiList,
            String kodeOpd,
            int tahun,
            String bulan
    ) {
        // Realisasi disimpan per bulan dan satu baris per rencana aksi. Baris ganda digabung
        // menjadi satu ringkasan agar response tetap satu objek per rencana aksi.
        Map<String, RealisasiData> realisasiByRenaksi = new HashMap<>();
        for (RenaksiOpd item : realisasiList) {
            realisasiByRenaksi.merge(
                    item.kodeRencanaAksiOpd(),
                    new RealisasiData(
                            item.realisasi() == null ? BigDecimal.ZERO : item.realisasi(),
                            item.faktorPenunjang(),
                            item.faktorPenghambat()),
                    RealisasiData::merge
            );
        }

        String effectiveKodeOpd = hasText(root.kodeOpd()) ? root.kodeOpd() : kodeOpd;
        Integer effectiveTahun = root.tahunAktif() != null ? root.tahunAktif() : tahun;
        Integer effectiveBulan = parseInteger(bulan);

        List<RenaksiOpdPenetapanResponse> items = root.renaksiOpds().stream()
                .map(r -> {
                    Integer itemTahun = r.tahun() != null ? r.tahun() : effectiveTahun;
                    return new RenaksiOpdPenetapanResponse(
                            r.kodeRencanaAksiOpd(),
                            // Nama renaksi tidak tersedia di penetapan, gunakan nama_pk.
                            r.namaPk(),
                            hasText(r.kodeOpd()) ? r.kodeOpd() : effectiveKodeOpd,
                            r.kodeSasaranOpd(),
                            r.kodePk(),
                            r.namaPk(),
                            r.pegawaiId(),
                            r.kodeSubkegiatan(),
                            r.namaSubkegiatan(),
                            r.anggaranRenaksi(),
                            itemTahun,
                            r.tw1(), r.tw2(), r.tw3(), r.tw4(),
                            // Objek realisasi selalu dikembalikan, termasuk saat belum ada isian.
                            buildRealisasi(
                                    realisasiByRenaksi.get(r.kodeRencanaAksiOpd()),
                                    itemTahun,
                                    effectiveBulan,
                                    sumTriwulan(r.tw1(), r.tw2(), r.tw3(), r.tw4()))
                    );
                })
                .toList();

        return new PenetapanRenaksiOpdListResponse(
                effectiveKodeOpd,
                effectiveTahun,
                effectiveBulan,
                root.versi(),
                root.isLocked(),
                items
        );
    }

    private RenaksiOpdPenetapanResponse.Realisasi buildRealisasi(
            RealisasiData data,
            Integer tahun,
            Integer bulan,
            double target
    ) {
        double nilaiRealisasi = data == null ? 0.0 : data.realisasi().doubleValue();
        var capaian = RenaksiOpd.hitungCapaian(nilaiRealisasi, target);
        return new RenaksiOpdPenetapanResponse.Realisasi(
                tahun,
                bulan,
                target,
                nilaiRealisasi,
                capaian.capaian(),
                capaian.keteranganCapaian(),
                data == null ? null : data.faktorPenunjang(),
                data == null ? null : data.faktorPenghambat()
        );
    }

    private double sumTriwulan(Integer tw1, Integer tw2, Integer tw3, Integer tw4) {
        return (tw1 == null ? 0 : tw1)
                + (tw2 == null ? 0 : tw2)
                + (tw3 == null ? 0 : tw3)
                + (tw4 == null ? 0 : tw4);
    }

    public Mono<RenaksiOpd> submitRealisasiRenaksi(RenaksiOpdRequest req) {
        return Mono.just(buildUncheckedRealisasiRenaksi(req)).flatMap(renaksiOpdRepository::save);
    }

    public Flux<RenaksiOpd> batchSubmitRealisasiRenaksi(@Valid List<RenaksiOpdRequest> requests) {
        return Flux.fromIterable(requests)
                .flatMap(req -> renaksiOpdRepository
                        .findFirstByKodeOpdAndTahunAndBulanAndKodeRencanaAksiOpd(
                                req.kodeOpd(), req.tahun(), req.bulan(), req.kodeRencanaAksiOpd())
                        .flatMap(existing -> renaksiOpdRepository.save(buildUpdated(existing, req)))
                        .switchIfEmpty(Mono.defer(() -> renaksiOpdRepository.save(buildUncheckedRealisasiRenaksi(req)))));
    }

    public Mono<Void> deleteRealisasiRenaksi(Long id) {
        return renaksiOpdRepository.deleteById(id);
    }

    public Flux<RenaksiOpd> getRealisasiRenaksiByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return renaksiOpdRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Flux<LaporanRealisasiRenaksiOpdResponse> getLaporanRealisasi(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        if (jenisLaporan == null) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jenis laporan tidak boleh kosong"));
        }
        if (jenisLaporan == JenisLaporan.BULANAN && !hasText(bulan)) {
            return Flux.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN"));
        }

        int tahunInt = Integer.parseInt(tahun);
        Mono<PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot> penetapanMono =
                penetapanRenaksiOpdClient.fetchRenaksiOpd(kodeOpd, tahunInt)
                        .onErrorReturn(new PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot(
                                kodeOpd, tahunInt, null, null, List.of()));

        return Mono.zip(
                        renaksiOpdRepository.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList(),
                        penetapanMono.defaultIfEmpty(new PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot(
                                kodeOpd, tahunInt, null, null, List.of())))
                .flatMapMany(tuple -> {
                    List<RenaksiOpd> realisasiList = tuple.getT1();
                    PenetapanRenaksiOpd.PenetapanRenaksiOpdRoot root = tuple.getT2();

                    Map<String, PenetapanRenaksiOpd.RenaksiPenetapanData> penetapanByKode = new HashMap<>();
                    for (PenetapanRenaksiOpd.RenaksiPenetapanData item : root.renaksiOpds()) {
                        if (hasText(item.kodeRencanaAksiOpd())) {
                            penetapanByKode.putIfAbsent(item.kodeRencanaAksiOpd(), item);
                        }
                    }

                    Map<String, List<RenaksiOpd>> grouped = realisasiList.stream()
                            .filter(item -> hasText(item.kodeRencanaAksiOpd()))
                            .collect(java.util.stream.Collectors.groupingBy(RenaksiOpd::kodeRencanaAksiOpd));

                    return Flux.fromIterable(grouped.entrySet()).map(entry -> {
                        String kodeRencanaAksiOpd = entry.getKey();
                        List<RenaksiOpd> groupList = entry.getValue();
                        PenetapanRenaksiOpd.RenaksiPenetapanData penetapan = penetapanByKode.get(kodeRencanaAksiOpd);

                        String namaRenaksi = penetapan != null && hasText(penetapan.namaPk())
                                ? penetapan.namaPk() : kodeRencanaAksiOpd;
                        String target = penetapan == null ? null : String.valueOf(sumTriwulan(
                                penetapan.tw1(), penetapan.tw2(), penetapan.tw3(), penetapan.tw4()));

                        Map<String, Double> listData = buildLaporanData(groupList, jenisLaporan, bulan);
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.entrySet().stream()
                                    .max(Map.Entry.comparingByKey())
                                    .map(Map.Entry::getValue)
                                    .orElse(0.0);
                        }

                        return new LaporanRealisasiRenaksiOpdResponse(
                                tahun, kodeOpd, kodeRencanaAksiOpd, namaRenaksi, target,
                                jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    private Map<String, Double> buildLaporanData(
            List<RenaksiOpd> groupList, JenisLaporan jenisLaporan, String bulan) {
        return switch (jenisLaporan) {
            case BULANAN -> {
                double total = groupList.stream()
                        .filter(item -> bulan != null && bulan.equals(item.bulan()))
                        .filter(item -> item.realisasi() != null)
                        .mapToDouble(item -> item.realisasi().doubleValue())
                        .sum();
                yield Map.of(bulan, total);
            }
            case TRIWULAN -> hitungTriwulanKumulatif(groupList);
            case TAHUNAN -> hitungBulanKumulatif(groupList);
        };
    }

    private Map<String, Double> hitungTriwulanKumulatif(List<RenaksiOpd> groupList) {
        Map<String, Double> triwulanMap = new TreeMap<>();
        for (int triwulan = 1; triwulan <= 4; triwulan++) {
            int awalBulan = (triwulan - 1) * 3 + 1;
            int akhirBulan = triwulan * 3;
            boolean adaData = groupList.stream()
                    .filter(item -> item.realisasi() != null)
                    .anyMatch(item -> {
                        int b = Integer.parseInt(item.bulan());
                        return b >= awalBulan && b <= akhirBulan;
                    });
            if (adaData) {
                double kumulatif = groupList.stream()
                        .filter(item -> item.realisasi() != null)
                        .filter(item -> Integer.parseInt(item.bulan()) <= akhirBulan)
                        .mapToDouble(item -> item.realisasi().doubleValue())
                        .sum();
                triwulanMap.put(String.valueOf(triwulan), kumulatif);
            }
        }
        return triwulanMap;
    }

    private Map<String, Double> hitungBulanKumulatif(List<RenaksiOpd> groupList) {
        TreeMap<Integer, Double> bulanMap = new TreeMap<>();
        for (RenaksiOpd item : groupList) {
            if (item.realisasi() == null) continue;
            int noBulan = Integer.parseInt(item.bulan());
            bulanMap.merge(noBulan, item.realisasi().doubleValue(), Double::sum);
        }

        double akumulasi = 0.0;
        Map<String, Double> result = new TreeMap<>();
        for (Map.Entry<Integer, Double> entry : bulanMap.entrySet()) {
            akumulasi += entry.getValue();
            result.put(String.valueOf(entry.getKey()), akumulasi);
        }
        return result;
    }

    public Mono<RenaksiOpd> updateFaktorPenunjang(FaktorPenunjangRenaksiOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.tahun(), req.bulan(), req.kodeRencanaAksiOpd(),
                existing -> existing.withFaktorPenunjang(req.faktorPenunjang())
        );
    }

    public Mono<RenaksiOpd> updateFaktorPenghambat(FaktorPenghambatRenaksiOpdRequest req) {
        return findAndUpdateFaktor(
                req.kodeOpd(), req.tahun(), req.bulan(), req.kodeRencanaAksiOpd(),
                existing -> existing.withFaktorPenghambat(req.faktorPenghambat())
        );
    }

    private Mono<RenaksiOpd> findAndUpdateFaktor(
            String kodeOpd,
            String tahun,
            String bulan,
            String kodeRencanaAksiOpd,
            UnaryOperator<RenaksiOpd> updater
    ) {
        return renaksiOpdRepository
                .findFirstByKodeOpdAndTahunAndBulanAndKodeRencanaAksiOpd(kodeOpd, tahun, bulan, kodeRencanaAksiOpd)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Renaksi OPD tidak ditemukan")))
                .flatMap(existing -> renaksiOpdRepository.save(updater.apply(existing)));
    }

    private RenaksiOpd buildUncheckedRealisasiRenaksi(RenaksiOpdRequest req) {
        return RenaksiOpd.of(
                req.kodeOpd(),
                req.tahun(),
                req.bulan(),
                req.kodeRencanaAksiOpd(),
                req.realisasi() != null ? req.realisasi() : BigDecimal.ZERO,
                req.jenisRealisasi() != null ? req.jenisRealisasi() : JenisRealisasi.NAIK,
                req.faktorPenunjang() != null ? req.faktorPenunjang() : "",
                req.faktorPenghambat() != null ? req.faktorPenghambat() : ""
        );
    }

    private RenaksiOpd buildUpdated(RenaksiOpd existing, RenaksiOpdRequest req) {
        return new RenaksiOpd(
                existing.id(),
                req.kodeOpd() != null ? req.kodeOpd() : existing.kodeOpd(),
                req.tahun() != null ? req.tahun() : existing.tahun(),
                req.bulan() != null ? req.bulan() : existing.bulan(),
                req.kodeRencanaAksiOpd() != null ? req.kodeRencanaAksiOpd() : existing.kodeRencanaAksiOpd(),
                req.realisasi() != null ? req.realisasi() : existing.realisasi(),
                req.jenisRealisasi() != null ? req.jenisRealisasi() : existing.jenisRealisasi(),
                req.faktorPenunjang() != null ? req.faktorPenunjang() : existing.faktorPenunjang(),
                req.faktorPenghambat() != null ? req.faktorPenghambat() : existing.faktorPenghambat(),
                existing.createdBy(),
                existing.createdDate(),
                existing.lastModifiedDate(),
                existing.lastModifiedBy()
        );
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
