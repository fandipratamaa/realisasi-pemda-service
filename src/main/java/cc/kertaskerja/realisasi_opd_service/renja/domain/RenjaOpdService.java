package cc.kertaskerja.realisasi_opd_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.web.RenjaOpdPenetapanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RenjaOpdService {
    private static final Logger log = LoggerFactory.getLogger(RenjaOpdService.class);
    private final PenetapanRenjaOpdClient penetapanClient;
    private final RenjaProgramOpdRepository targetProgramRepo;
    private final RenjaKegiatanOpdRepository targetKegiatanRepo;
    private final RenjaSubKegiatanOpdRepository targetSubKegiatanRepo;

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

    private Mono<RenjaOpdPenetapanResponse> fetchRealisasiAndMerge(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun, String bulan
    ) {
        Mono<Map<String, Double>> programRealisasiMap = targetProgramRepo.findAllByTahunAndBulan(
                        String.valueOf(tahun), bulan
                )
                .collectMap(RenjaProgramOpd::kodeTarget,
                        t -> t.realisasi() != null ? t.realisasi().doubleValue() : null);

        Mono<Map<String, Double>> kegiatanRealisasiMap = targetKegiatanRepo.findAllByTahunAndBulan(
                        String.valueOf(tahun), bulan
                )
                .collectMap(RenjaKegiatanOpd::kodeTarget,
                        t -> t.realisasi() != null ? t.realisasi().doubleValue() : null);

        Mono<Map<String, Double>> subKegiatanRealisasiMap = targetSubKegiatanRepo.findAllByTahunAndBulan(
                        String.valueOf(tahun), bulan
                )
                .collectMap(RenjaSubKegiatanOpd::kodeTarget,
                        t -> t.realisasi() != null ? t.realisasi().doubleValue() : null);

        return Mono.zip(programRealisasiMap, kegiatanRealisasiMap, subKegiatanRealisasiMap)
                .map(tuple -> {
                    Map<String, Double> progMap = tuple.getT1();
                    Map<String, Double> kegMap = tuple.getT2();
                    Map<String, Double> subMap = tuple.getT3();

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

    private RenjaOpdPenetapanResponse.ProgramPenetapan toProgramPenetapan(
            PenetapanRenjaOpd.ProgramPenetapanData p
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> new RenjaOpdPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), null, t.satuan(), null, null
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
                                        t.target(), null, t.satuan(), null, null
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
                                        t.target(), null, t.satuan(), null, null
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaOpdPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), s.isLocked(),
                indikators, s.paguAnggaran()
        );
    }

    private RenjaOpdPenetapanResponse.ProgramPenetapan mergeProgramWithRealisasi(
            PenetapanRenjaOpd.ProgramPenetapanData p,
            Map<String, Double> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = p.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                Double realisasi = realisasiMap.get(t.kodeTarget());
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian()
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
            Map<String, Double> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = k.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                Double realisasi = realisasiMap.get(t.kodeTarget());
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian()
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
            Map<String, Double> realisasiMap
    ) {
        List<RenjaOpdPenetapanResponse.IndikatorPenetapan> indikators = s.indikators().stream()
                .map(ind -> {
                    List<RenjaOpdPenetapanResponse.TargetPenetapan> targets = ind.targets().stream()
                            .map(t -> {
                                Double realisasi = realisasiMap.get(t.kodeTarget());
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaOpdPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(), null,
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian()
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

    record CapaianResult(Double capaian, String keteranganCapaian) {}

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
