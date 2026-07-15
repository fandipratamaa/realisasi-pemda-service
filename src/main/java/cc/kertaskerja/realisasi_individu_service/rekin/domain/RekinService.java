package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRekinIndividuClient;
import cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu;
import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.LaporanRealisasiRekinIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinResponse;
import cc.kertaskerja.integration.kepegawaian.PegawaiClient;
import cc.kertaskerja.integration.upload.UploadClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;

@Service
public class RekinService {
    private static final Logger log = LoggerFactory.getLogger(RekinService.class);
    private final RekinIndividuRepository repository;
    private final PenetapanRekinIndividuClient penetapanClient;
    private final PegawaiClient pegawaiClient;
    private final UploadClient uploadClient;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public RekinService(
            RekinIndividuRepository repository,
            PenetapanRekinIndividuClient penetapanClient,
            PegawaiClient pegawaiClient,
            UploadClient uploadClient
    ) {
        this.repository = repository;
        this.penetapanClient = penetapanClient;
        this.pegawaiClient = pegawaiClient;
        this.uploadClient = uploadClient;
    }

    public Mono<RekinResponse> createRekin(RekinRequest req) {
        return upsert(req)
                .map(this::toResponse)
                .flatMap(response -> applyPenetapan(Mono.just(response), req));
    }

    private Mono<RekinIndividu> upsert(RekinRequest req) {
        JenisRealisasi jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : JenisRealisasi.NAIK;
        String bukti = req.buktiPendukung() != null ? req.buktiPendukung() : "";
        return repository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                        req.kodePkRekin(), req.kodeIndikatorPKrekin(), req.kodeTargetPKrekin())
                .flatMap(existing -> {
                    RekinIndividu updated = new RekinIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.tahun(), existing.bulan(),
                            existing.kodePkRekin(), existing.kodeIndikatorPkRekin(), existing.kodeTargetPkRekin(),
                            req.kodeSasaranOpd(),
                            req.realisasi(), jenisRealisasi,
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            bukti != null && !bukti.isBlank() ? bukti : existing.buktiPendukung(),
                            req.keteranganBuktiPendukung() != null ? req.keteranganBuktiPendukung() : existing.keteranganBuktiPendukung(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return repository.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RekinIndividu newEntity = RekinIndividu.of(
                            req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                            req.kodePkRekin(), req.kodeIndikatorPKrekin(), req.kodeTargetPKrekin(),
                            req.kodeSasaranOpd(),
                            req.realisasi(), jenisRealisasi,
                            "", "", bukti, req.keteranganBuktiPendukung());
                    return repository.save(newEntity);
                }));
    }

    private RekinResponse toResponse(RekinIndividu entity) {
        return RekinResponse.from(entity, null, null, null);
    }

    private Mono<RekinResponse> applyPenetapan(Mono<RekinResponse> responseMono, RekinRequest req) {
        Mono<PenetapanRekinIndividu.RekinIndividuData> penetapanData =
                penetapanClient.fetchRekinIndividu(req.nip(), req.kodeOpd(), Integer.parseInt(req.tahun()));

        return responseMono.flatMap(response -> penetapanData
                .map(data -> enrichWithPenetapan(response, data))
                .switchIfEmpty(Mono.just(response))
        ).onErrorResume(e -> {
            log.warn("Gagal menghubungi penetapan untuk nip={}, kodeOpd={}, tahun={}: {}",
                    req.nip(), req.kodeOpd(), req.tahun(), e.getMessage());
            return responseMono;
        });
    }

    private RekinResponse enrichWithPenetapan(
            RekinResponse response,
            PenetapanRekinIndividu.RekinIndividuData data
    ) {
        if (data.rekins() == null) {
            return response;
        }
        PenetapanRekinIndividu.RekinData matchingRekin = data.rekins().stream()
                .filter(r -> r.kodePk().equals(response.kodePkRekin()))
                .findFirst()
                .orElse(null);
        if (matchingRekin == null) {
            return response;
        }
        PenetapanRekinIndividu.IndikatorRekinData matchingIndikator = matchingRekin.indikatorPk().stream()
                .filter(i -> i.kodeIndikatorPk().equals(response.kodeIndikatorPkRekin()))
                .findFirst()
                .orElse(null);
        if (matchingIndikator == null) {
            return response;
        }
        PenetapanRekinIndividu.TargetRekinData matchingTarget = matchingIndikator.targetPk().stream()
                .filter(t -> t.kodeTargetPk().equals(response.kodeTargetPkRekin()))
                .findFirst()
                .orElse(null);
        if (matchingTarget == null) {
            return response;
        }
        Double realisasiValue = response.realisasi() != null
                ? response.realisasi().doubleValue() : null;
        var capaianResult = RekinIndividu.hitungCapaian(realisasiValue, matchingTarget.target());
        return new RekinResponse(
                response.id(), response.kodeOpd(), response.nip(),
                response.tahun(), response.bulan(),
                response.kodePkRekin(), response.kodeIndikatorPkRekin(), response.kodeTargetPkRekin(),
                response.kodeSasaranOpd(),
                response.realisasi(), response.jenisRealisasi(),
                response.faktorPenunjang(), response.faktorPenghambat(), response.buktiPendukung(),
                response.createdBy(), response.lastModifiedBy(),
                response.createdDate(), response.lastModifiedDate(),
                matchingTarget.target(), capaianResult.capaian(), capaianResult.keteranganCapaian(), response.keteranganBuktiPendukung()
        );
    }

    public Mono<RekinIndividu> updateFaktorPenunjang(FaktorPenunjangRekinRequest req) {
        return repository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                        req.kodePkRekin(), req.kodeIndikator(), req.kodeTarget())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RekinIndividu updated = new RekinIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.tahun(), existing.bulan(),
                            existing.kodePkRekin(), existing.kodeIndikatorPkRekin(), existing.kodeTargetPkRekin(),
                            existing.kodeSasaranOpd(),
                            existing.realisasi(), existing.jenisRealisasi(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(), existing.buktiPendukung(), existing.keteranganBuktiPendukung(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return repository.save(updated);
                });
    }

    public Mono<RekinIndividu> updateFaktorPenghambat(FaktorPenghambatRekinRequest req) {
        return repository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                        req.kodePkRekin(), req.kodeIndikator(), req.kodeTarget())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RekinIndividu updated = new RekinIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.tahun(), existing.bulan(),
                            existing.kodePkRekin(), existing.kodeIndikatorPkRekin(), existing.kodeTargetPkRekin(),
                            existing.kodeSasaranOpd(),
                            existing.realisasi(), existing.jenisRealisasi(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(), existing.buktiPendukung(), existing.keteranganBuktiPendukung(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return repository.save(updated);
                });
    }

    public reactor.core.publisher.Flux<RekinResponse> getAllByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return repository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan)
                .collectList()
                .flatMapMany(list -> {
                    Map<String, List<RekinIndividu>> byNip = list.stream().collect(Collectors.groupingBy(RekinIndividu::nip));
                    return reactor.core.publisher.Flux.fromIterable(byNip.entrySet())
                            .flatMap(entry -> {
                                String nip = entry.getKey();
                                List<RekinIndividu> rekins = entry.getValue();
                                return penetapanClient.fetchRekinIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                                        .map(penetapanData -> {
                                            return rekins.stream().map(rekin -> {
                                                Double target = null;
                                                Double capaian = null;
                                                String ket = null;
                                                
                                                if (penetapanData.rekins() != null) {
                                                    for (var r : penetapanData.rekins()) {
                                                        if (r.kodePk().equals(rekin.kodePkRekin()) && r.indikatorPk() != null) {
                                                            for (var ind : r.indikatorPk()) {
                                                                if (ind.kodeIndikatorPk().equals(rekin.kodeIndikatorPkRekin()) && ind.targetPk() != null) {
                                                                    for (var t : ind.targetPk()) {
                                                                        if (t.kodeTargetPk().equals(rekin.kodeTargetPkRekin())) {
                                                                            target = t.target();
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (target != null) {
                                                    Double realisasiValue = rekin.realisasi() != null ? rekin.realisasi().doubleValue() : null;
                                                    var capaianResult = RekinIndividu.hitungCapaian(realisasiValue, target);
                                                    capaian = capaianResult.capaian();
                                                    ket = capaianResult.keteranganCapaian();
                                                }
                                                
                                                return RekinResponse.from(rekin, target, capaian, ket);
                                            }).toList();
                                        })
                                        .defaultIfEmpty(rekins.stream().map(rekin -> RekinResponse.from(rekin, null, null, null)).toList());
                            })
                            .flatMapIterable(Function.identity());
                });
    }

    // --- Laporan ---

    public reactor.core.publisher.Flux<LaporanRealisasiRekinIndividuResponse> getLaporanRealisasi(
            String nip, String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        
        Mono<cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinIndividuData> penetapanMono = 
                penetapanClient.fetchRekinIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .onErrorResume(e -> Mono.empty());

        return Mono.zip(repository.findAllByKodeOpdAndNipAndTahun(kodeOpd, nip, tahun).collectList(), penetapanMono.defaultIfEmpty(new cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinIndividuData(null, null, null, null, List.of())))
                .flatMapMany(tuple -> {
                    List<RekinIndividu> list = tuple.getT1();
                    cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu.RekinIndividuData penetapanData = tuple.getT2();

                    Map<String, List<RekinIndividu>> grouped = list.stream()
                            .collect(Collectors.groupingBy(t -> t.kodeIndikatorPkRekin() + "|" + t.kodeTargetPkRekin()));

                    return reactor.core.publisher.Flux.fromIterable(grouped.values()).map(groupList -> {
                        RekinIndividu first = groupList.get(0);
                        
                        String indikatorName = first.kodeIndikatorPkRekin();
                        String targetName = first.kodeTargetPkRekin();

                        if (penetapanData.rekins() != null) {
                            for (var r : penetapanData.rekins()) {
                                if (r.indikatorPk() != null) {
                                    for (var ind : r.indikatorPk()) {
                                        if (ind.kodeIndikatorPk().equals(first.kodeIndikatorPkRekin())) {
                                            indikatorName = ind.namaIndikatorPk();
                                            if (ind.targetPk() != null) {
                                                for (var tgt : ind.targetPk()) {
                                                    if (tgt.kodeTargetPk().equals(first.kodeTargetPkRekin())) {
                                                        targetName = tgt.target() != null ? String.valueOf(tgt.target()) : first.kodeTargetPkRekin();
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(t -> bulan.equals(t.bulan()))
                                        .filter(t -> t.realisasi() != null)
                                        .mapToDouble(t -> t.realisasi().doubleValue())
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (RekinIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi().doubleValue(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (RekinIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    bulanMap.merge(t.bulan(), t.realisasi().doubleValue(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }

                        return new LaporanRealisasiRekinIndividuResponse(tahun, kodeOpd, nip, indikatorName, targetName, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    public reactor.core.publisher.Flux<LaporanRealisasiRekinIndividuResponse> getLaporanRealisasiByOpd(
            String kodeOpd, String tahun, JenisLaporan jenisLaporan, String bulan) {
        
        return repository.findAllByKodeOpdAndTahun(kodeOpd, tahun).collectList()
                .flatMapMany(list -> {
                    Map<String, List<RekinIndividu>> grouped = list.stream()
                            .collect(Collectors.groupingBy(t -> t.nip() + "|" + t.kodeIndikatorPkRekin() + "|" + t.kodeTargetPkRekin()));

                    return reactor.core.publisher.Flux.fromIterable(grouped.values()).map(groupList -> {
                        RekinIndividu first = groupList.get(0);
                        
                        String indikatorName = first.kodeIndikatorPkRekin();
                        String targetName = first.kodeTargetPkRekin();

                        Map<String, Double> listData = switch (jenisLaporan) {
                            case BULANAN -> {
                                if (bulan == null || bulan.isBlank()) {
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter bulan wajib diisi untuk laporan BULANAN");
                                }
                                double total = groupList.stream()
                                        .filter(t -> bulan.equals(t.bulan()))
                                        .filter(t -> t.realisasi() != null)
                                        .mapToDouble(t -> t.realisasi().doubleValue())
                                        .sum();
                                yield Map.of(bulan, total);
                            }
                            case TRIWULAN -> {
                                Map<String, Double> triwulanMap = new HashMap<>();
                                for (int i = 1; i <= 4; i++) triwulanMap.put(String.valueOf(i), 0.0);
                                for (RekinIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    int noBulan = Integer.parseInt(t.bulan());
                                    String triwulan = String.valueOf((noBulan - 1) / 3 + 1);
                                    triwulanMap.merge(triwulan, t.realisasi().doubleValue(), Double::sum);
                                }
                                yield triwulanMap;
                            }
                            case TAHUNAN -> {
                                Map<String, Double> bulanMap = new HashMap<>();
                                for (int i = 1; i <= 12; i++) bulanMap.put(String.valueOf(i), 0.0);
                                for (RekinIndividu t : groupList) {
                                    if (t.realisasi() == null) continue;
                                    bulanMap.merge(t.bulan(), t.realisasi().doubleValue(), Double::sum);
                                }
                                yield bulanMap;
                            }
                        };
                        
                        Double totalRealisasi = null;
                        if (jenisLaporan == JenisLaporan.TRIWULAN || jenisLaporan == JenisLaporan.TAHUNAN) {
                            totalRealisasi = listData.values().stream().mapToDouble(Double::doubleValue).sum();
                        }

                        return new LaporanRealisasiRekinIndividuResponse(tahun, kodeOpd, first.nip(), indikatorName, targetName, jenisLaporan, listData, totalRealisasi);
                    });
                });
    }

    // --- Pegawai Integration ---
    public Mono<List<cc.kertaskerja.integration.kepegawaian.PegawaiClient.PegawaiData>> getAllPegawai() {
        return pegawaiClient.fetchAllPegawai();
    }

    public Mono<PenetapanRekinIndividuResponse> searchRekin(String kodeOpd, String tahun, String bulan, String levelRole, String nip) {
        List<String> validRoles = List.of("LEVEL_1", "LEVEL_2", "LEVEL_3", "LEVEL_4");
        if (!validRoles.contains(levelRole.toUpperCase())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "levelRole tidak valid"));
        }

        return pegawaiClient.fetchAllPegawai()
                .flatMap(pegawais -> {
                    boolean nipExists = pegawais.stream()
                            .anyMatch(p -> nip.equals(p.nip()));
                    
                    if (!nipExists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pegawai dengan NIP tersebut tidak ditemukan di service Kepegawaian"));
                    }
                    
                    return getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan);
                });
    }

    // --- Penetapan Integration ---
    public Mono<PenetapanRekinIndividuResponse> getPenetapanByNip(String nip, String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRekinIndividu(nip, kodeOpd, tahun)
                .flatMap(data -> {
                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(toResponseWithoutBulan(data));
                    }
                    return buildResponseWithBulan(data, nip, kodeOpd, tahun, bulan);
                });
    }

    private PenetapanRekinIndividuResponse toResponseWithoutBulan(PenetapanRekinIndividu.RekinIndividuData data) {
        List<PenetapanRekinIndividuResponse.RekinPenetapanResponse> rekins = data.rekins().stream()
                .map(this::mapRekinToResponse)
                .toList();
        return new PenetapanRekinIndividuResponse(
                data.pegawaiId(), data.nama(), data.kodeOpd(), data.tahunAktif(), null, rekins
        );
    }

    private PenetapanRekinIndividuResponse.RekinPenetapanResponse mapRekinToResponse(
            PenetapanRekinIndividu.RekinData rekin
    ) {
        List<PenetapanRekinIndividuResponse.IndikatorPenetapanResponse> indikators = rekin.indikatorPk().stream()
                .map(this::mapIndikatorToResponse)
                .toList();
        return new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                rekin.id(), rekin.kodeSasaranOpd(), rekin.kodePk(), rekin.rekin(), rekin.versi(), indikators
        );
    }

    private PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mapIndikatorToResponse(
            PenetapanRekinIndividu.IndikatorRekinData indikator
    ) {
        List<PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                        t.id(), t.kodeTargetPk(), t.tahun(), t.target(), t.satuan(),
                        null, null, null, null, null, null, null, null
                ))
                .toList();
        return new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                indikator.id(), indikator.kodeIndikatorPk(), indikator.namaIndikatorPk(), targets
        );
    }

    private Mono<PenetapanRekinIndividuResponse> buildResponseWithBulan(
            PenetapanRekinIndividu.RekinIndividuData data,
            String nip, String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);
        return repository.findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahunStr, bulan)
                .collectList()
                .map(localList -> {
                    Map<String, RekinIndividu> localTargetMap = buildLocalTargetMap(localList);
                    List<PenetapanRekinIndividuResponse.RekinPenetapanResponse> rekins = data.rekins().stream()
                            .map(r -> mergeRekinWithRealisasi(r, localTargetMap))
                            .toList();
                    return new PenetapanRekinIndividuResponse(
                            data.pegawaiId(), data.nama(), data.kodeOpd(), data.tahunAktif(),
                            parseInteger(bulan), rekins
                    );
                });
    }

    private Map<String, RekinIndividu> buildLocalTargetMap(List<RekinIndividu> localList) {
        return localList.stream()
                .collect(Collectors.toMap(
                        r -> buildTargetKey(r.kodePkRekin(), r.kodeIndikatorPkRekin(), r.kodeTargetPkRekin()),
                        Function.identity()
                ));
    }

    private String buildTargetKey(String kodePkRekin, String kodeIndikatorPkRekin, String kodeTargetPkRekin) {
        return kodePkRekin + "|" + kodeIndikatorPkRekin + "|" + kodeTargetPkRekin;
    }

    private PenetapanRekinIndividuResponse.RekinPenetapanResponse mergeRekinWithRealisasi(
            PenetapanRekinIndividu.RekinData rekin,
            Map<String, RekinIndividu> localTargetMap
    ) {
        List<PenetapanRekinIndividuResponse.IndikatorPenetapanResponse> indikators = rekin.indikatorPk().stream()
                .map(ind -> mergeIndikatorWithRealisasi(rekin.kodePk(), ind, localTargetMap))
                .toList();
        return new PenetapanRekinIndividuResponse.RekinPenetapanResponse(
                rekin.id(), rekin.kodeSasaranOpd(), rekin.kodePk(), rekin.rekin(),
                rekin.versi(), indikators
        );
    }

    private PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mergeIndikatorWithRealisasi(
            String kodePk,
            PenetapanRekinIndividu.IndikatorRekinData indikator,
            Map<String, RekinIndividu> localTargetMap
    ) {
        List<PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> mergeTargetWithRealisasi(kodePk, indikator.kodeIndikatorPk(), t, localTargetMap))
                .toList();
        return new PenetapanRekinIndividuResponse.IndikatorPenetapanResponse(
                indikator.id(), indikator.kodeIndikatorPk(), indikator.namaIndikatorPk(), targets
        );
    }

    private PenetapanRekinIndividuResponse.TargetPenetapanResponse mergeTargetWithRealisasi(
            String kodePk, String kodeIndikatorPk,
            PenetapanRekinIndividu.TargetRekinData target,
            Map<String, RekinIndividu> localTargetMap
    ) {
        String key = buildTargetKey(kodePk, kodeIndikatorPk, target.kodeTargetPk());
        RekinIndividu local = localTargetMap.get(key);
        Double realisasiValue = local != null && local.realisasi() != null
                ? local.realisasi().doubleValue() : null;
        RekinIndividu.CapaianResult capaianResult = RekinIndividu.hitungCapaian(realisasiValue, target.target());
        String faktorPenunjang = local != null ? local.faktorPenunjang() : null;
        String faktorPenghambat = local != null ? local.faktorPenghambat() : null;
        String buktiPendukung = local != null ? local.buktiPendukung() : null;
        String keteranganBuktiPendukung = local != null ? local.keteranganBuktiPendukung() : null;
        String jenisRealisasi = "NAIK";
        return new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                target.id(), target.kodeTargetPk(), target.tahun(), target.target(), target.satuan(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat, buktiPendukung, keteranganBuktiPendukung, jenisRealisasi
        );
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    public Mono<String> uploadFile(FilePart file) {
        return uploadClient.uploadFile(file)
                .map(UploadClient.UploadMetadata::url);
    }
}
