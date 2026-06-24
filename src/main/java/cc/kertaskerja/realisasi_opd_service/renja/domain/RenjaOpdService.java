package cc.kertaskerja.realisasi_opd_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaProgramOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaProgramOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.RenjaSubKegiatanOpdRepository;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_opd_service.renja.web.LaporanRealisasiRenjaOpdResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdPenetapanResponse;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenghambatTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.program.FaktorPenunjangTargetRenjaProgramOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanOpdRequest;
import cc.kertaskerja.realisasi_opd_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanOpdRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RenjaOpdService {
    private final PenetapanRenjaOpdClient penetapanClient;
    private final RenjaProgramOpdRepository targetProgramRepo;
    private final RenjaKegiatanOpdRepository targetKegiatanRepo;
    private final RenjaSubKegiatanOpdRepository targetSubKegiatanRepo;

    record RealisasiData(Double realisasi, String faktorPenunjang, String faktorPenghambat) {}

    record CapaianResult(Double capaian, String keteranganCapaian) {}

    public RenjaOpdService(
            PenetapanRenjaOpdClient penetapanClient,
            RenjaProgramOpdRepository targetProgramRepo,
            RenjaKegiatanOpdRepository targetKegiatanRepo,
            RenjaSubKegiatanOpdRepository targetSubKegiatanRepo
    ) {
        this.penetapanClient = penetapanClient;
        this.targetProgramRepo = targetProgramRepo;
        this.targetKegiatanRepo = targetKegiatanRepo;
        this.targetSubKegiatanRepo = targetSubKegiatanRepo;
    }

    public Mono<RenjaOpdPenetapanResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRenjaOpd(kodeOpd, tahun)
                .flatMap(root -> {
                    String effectiveKodeOpd = root.kodeOpd() != null ? root.kodeOpd() : kodeOpd;
                    Integer effectiveTahun = root.tahunAktif() != null ? root.tahunAktif() : tahun;

                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(mapWithoutRealisasi(root, effectiveKodeOpd, effectiveTahun));
                    }

                    return fetchRealisasiAndMerge(root, effectiveKodeOpd, effectiveTahun, bulan);
                })
                .defaultIfEmpty(new RenjaOpdPenetapanResponse(
                        kodeOpd, tahun, parseInteger(bulan),
                        List.of(), List.of(), List.of()
                ));
    }

    public Mono<RenjaProgramOpd> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramOpdRequest req) {
        return targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program OPD tidak ditemukan")))
                .flatMap(existing -> targetProgramRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaProgramOpd> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramOpdRequest req) {
        return targetProgramRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program OPD tidak ditemukan")))
                .flatMap(existing -> targetProgramRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanOpdRequest req) {
        return targetKegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanOpdRequest req) {
        return targetKegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenunjangSubKegiatan(FaktorPenunjangTargetRenjaSubKegiatanOpdRequest req) {
        return targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubkegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetSubKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenghambatSubKegiatan(FaktorPenghambatTargetRenjaSubKegiatanOpdRequest req) {
        return targetSubKegiatanRepo.findByKodeOpdAndKodeSubkegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubkegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan OPD tidak ditemukan")))
                .flatMap(existing -> targetSubKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<LaporanRealisasiRenjaOpdResponse> getLaporanRealisasi(String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        record PeriodeRealisasi(String bulan, BigDecimal realisasi) {}

        Flux<PeriodeRealisasi> programFlux = targetProgramRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun)
                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()));
        Flux<PeriodeRealisasi> kegiatanFlux = targetKegiatanRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun)
                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()));
        Flux<PeriodeRealisasi> subkegiatanFlux = targetSubKegiatanRepo.findAllByKodeOpdAndTahun(kodeOpd, tahun)
                .map(t -> new PeriodeRealisasi(t.bulan(), t.realisasi()));

        return Flux.merge(programFlux, kegiatanFlux, subkegiatanFlux)
                .collectList()
                .map(list -> {
                    Map<String, Double> listData = switch (jenisLaporan) {
                        case BULANAN -> {
                            if (bulan == null || bulan.isBlank()) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                            }
                            double total = list.stream()
                                    .filter(p -> bulan.equals(p.bulan()))
                                    .filter(p -> p.realisasi() != null)
                                    .mapToDouble(p -> p.realisasi().doubleValue())
                                    .sum();
                            yield Map.of(bulan, total);
                        }
                        case TRIWULAN -> {
                            Map<String, Double> triwulanMap = new HashMap<>();
                            for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                            for (PeriodeRealisasi p : list) {
                                if (p.realisasi() == null) continue;
                                int noBulan = Integer.parseInt(p.bulan());
                                String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                triwulanMap.merge(triwulan, p.realisasi().doubleValue(), Double::sum);
                            }
                            yield triwulanMap;
                        }
                        case TAHUNAN -> {
                            Map<String, Double> bulanMap = new HashMap<>();
                            for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                            for (PeriodeRealisasi p : list) {
                                if (p.realisasi() == null) continue;
                                bulanMap.merge(p.bulan(), p.realisasi().doubleValue(), Double::sum);
                            }
                            yield bulanMap;
                        }
                    };
                    return new LaporanRealisasiRenjaOpdResponse(tahun, kodeOpd, jenisLaporan, listData);
                });
    }

    // ========================================================================
    // Private - Fetch realisasi data and merge with penetapan
    // ========================================================================

    private Mono<RenjaOpdPenetapanResponse> fetchRealisasiAndMerge(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);

        Mono<Map<String, RealisasiData>> programRealisasiMap = targetProgramRepo.findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(RenjaProgramOpd::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat()));

        Mono<Map<String, RealisasiData>> kegiatanRealisasiMap = targetKegiatanRepo.findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(RenjaKegiatanOpd::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat()));

        Mono<Map<String, RealisasiData>> subKegiatanRealisasiMap = targetSubKegiatanRepo.findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(RenjaSubKegiatanOpd::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(), t.faktorPenghambat()));

        return Mono.zip(programRealisasiMap, kegiatanRealisasiMap, subKegiatanRealisasiMap)
                .map(tuple -> {
                    Map<String, RealisasiData> progMap = tuple.getT1();
                    Map<String, RealisasiData> kegMap = tuple.getT2();
                    Map<String, RealisasiData> subMap = tuple.getT3();

                    List<RenjaOpdPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                            .map(p -> mergeProgramWithRealisasi(p, progMap))
                            .filter(p -> !p.indikators().isEmpty())
                            .toList();

                    List<RenjaOpdPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                            .map(k -> mergeKegiatanWithRealisasi(k, kegMap))
                            .filter(k -> !k.indikators().isEmpty())
                            .toList();

                    List<RenjaOpdPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                            .map(s -> mergeSubKegiatanWithRealisasi(s, subMap))
                            .filter(s -> !s.indikators().isEmpty())
                            .toList();

                    return new RenjaOpdPenetapanResponse(
                            kodeOpd, tahun, parseInteger(bulan),
                            programs, kegiatans, subkegiatans
                    );
                });
    }

    // ========================================================================
    // Private - Map penetapan response tanpa realisasi
    // ========================================================================

    private RenjaOpdPenetapanResponse mapWithoutRealisasi(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun
    ) {
        List<RenjaOpdPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                .map(this::toProgramPenetapan)
                .toList();

        List<RenjaOpdPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                .map(this::toKegiatanPenetapan)
                .toList();

        List<RenjaOpdPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                .map(this::toSubKegiatanPenetapan)
                .toList();

        return new RenjaOpdPenetapanResponse(kodeOpd, tahun, null, programs, kegiatans, subkegiatans);
    }

    // ========================================================================
    // Private - Entity to Response mapping
    // ========================================================================

    private RenjaOpdPenetapanResponse.ProgramPenetapan toProgramPenetapan(
            PenetapanRenjaOpd.ProgramPenetapanData p
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), p.isLocked(),
                indikators, p.paguAnggaran()
        );
    }

    private RenjaOpdPenetapanResponse.KegiatanPenetapan toKegiatanPenetapan(
            PenetapanRenjaOpd.KegiatanPenetapanData k
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = k.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), k.isLocked(),
                indikators, k.paguAnggaran()
        );
    }

    private RenjaOpdPenetapanResponse.SubkegiatanPenetapan toSubKegiatanPenetapan(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = s.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null,
                                        null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), s.isLocked(),
                indikators, s.paguAnggaran()
        );
    }

    // ========================================================================
    // Private - Merge realisasi data ke penetapan response
    // ========================================================================

    private RenjaOpdPenetapanResponse.ProgramPenetapan mergeProgramWithRealisasi(
            PenetapanRenjaOpd.ProgramPenetapanData p,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        data != null ? data.faktorPenunjang() : null,
                                        data != null ? data.faktorPenghambat() : null
                                );
                            })
                            .toList();

                    if (targets.isEmpty()) return null;

                    return new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), targets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaOpdPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), p.isLocked(),
                indikators, p.paguAnggaran()
        );
    }

    private RenjaOpdPenetapanResponse.KegiatanPenetapan mergeKegiatanWithRealisasi(
            PenetapanRenjaOpd.KegiatanPenetapanData k,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = k.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        data != null ? data.faktorPenunjang() : null,
                                        data != null ? data.faktorPenghambat() : null
                                );
                            })
                            .toList();

                    if (targets.isEmpty()) return null;

                    return new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), targets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaOpdPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), k.isLocked(),
                indikators, k.paguAnggaran()
        );
    }

    private RenjaOpdPenetapanResponse.SubkegiatanPenetapan mergeSubKegiatanWithRealisasi(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s,
            Map<String, RealisasiData> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = s.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        data != null ? data.faktorPenunjang() : null,
                                        data != null ? data.faktorPenghambat() : null
                                );
                            })
                            .toList();

                    if (targets.isEmpty()) return null;

                    return new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), targets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaOpdPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), s.isLocked(),
                indikators, s.paguAnggaran()
        );
    }

    // ========================================================================
    // Private - Helpers sementara karena belum ada endpoint di renja individu
    // ========================================================================

    static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (realisasi == null || target == null || target == 0) {
            return new CapaianResult(null, null);
        }
        double calculated = realisasi / target * 100;
        String keterangan = null;
        if (calculated > 100) {
            keterangan = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculated) + ")";
        }
        return new CapaianResult(Math.min(calculated, 100), keterangan);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
