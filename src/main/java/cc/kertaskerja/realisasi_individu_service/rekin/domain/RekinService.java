package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRekinIndividuClient;
import cc.kertaskerja.integration.penetapan.rekin.PenetapanRekinIndividu;
import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenghambatRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.FaktorPenunjangRekinRequest;
import cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse;
import cc.kertaskerja.realisasi_individu_service.rekin.web.RekinRequest;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpd;
import cc.kertaskerja.realisasi_opd_service.sasaran.domain.SasaranOpdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RekinService {
    private static final Logger log = LoggerFactory.getLogger(RekinService.class);
    private final RekinIndividuRepository repository;
    private final SasaranOpdRepository sasaranOpdRepository;
    private final PenetapanRekinIndividuClient penetapanClient;

    public RekinService(
            RekinIndividuRepository repository,
            SasaranOpdRepository sasaranOpdRepository,
            PenetapanRekinIndividuClient penetapanClient
    ) {
        this.repository = repository;
        this.sasaranOpdRepository = sasaranOpdRepository;
        this.penetapanClient = penetapanClient;
    }

    public Mono<RekinIndividu> createRekin(RekinRequest req) {
        JenisRealisasi jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : JenisRealisasi.NAIK;
        return repository
                .findFirstByKodeOpdAndNipAndTahunAndBulanAndKodePkRekinAndKodeIndikatorPkRekinAndKodeTargetPkRekin(
                        req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                        req.kodePkRekin(), req.kodeIndikatorPKrekin(), req.kodeTargetPKrekin())
                .flatMap(existing -> {
                    RekinIndividu updated = new RekinIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.tahun(), existing.bulan(),
                            existing.kodePkRekin(), existing.kodeIndikatorPkRekin(), existing.kodeTargetPkRekin(),
                            req.realisasi(), jenisRealisasi,
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return repository.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RekinIndividu baru = RekinIndividu.of(
                            req.kodeOpd(), req.nip(), req.tahun(), req.bulan(),
                            req.kodePkRekin(), req.kodeIndikatorPKrekin(), req.kodeTargetPKrekin(),
                            req.realisasi(), jenisRealisasi, "", "");
                    return repository.save(baru);
                }))
                .flatMap(saved -> syncToSasaranOpd(saved, req).then(Mono.just(saved)));
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
                            existing.realisasi(), existing.jenisRealisasi(),
                            req.faktorPenunjang(),
                            existing.faktorPenghambat(),
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
                            existing.realisasi(), existing.jenisRealisasi(),
                            existing.faktorPenunjang(),
                            req.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return repository.save(updated);
                });
    }

    // --- Sync ke Sasaran OPD ---

    private Mono<Void> syncToSasaranOpd(RekinIndividu saved, RekinRequest req) {
        if (req.kodeSasaranOpd() == null || req.kodeSasaranOpd().isBlank()) {
            return Mono.empty();
        }
        return sasaranOpdRepository
                .findFirstByKodeOpdAndKodeSasaranOpdAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        saved.kodeOpd(), req.kodeSasaranOpd(), saved.kodeIndikatorPkRekin(),
                        saved.kodeTargetPkRekin(), saved.tahun(), saved.bulan())
                .flatMap(existing -> {
                    SasaranOpd updated = new SasaranOpd(
                            existing.id(), existing.kodeOpd(), existing.tahun(), existing.bulan(),
                            existing.kodeSasaranOpd(), existing.kodeIndikator(), existing.kodeTarget(),
                            saved.realisasi(), existing.jenisRealisasi(),
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdBy(), existing.createdDate(), null, null);
                    return sasaranOpdRepository.save(updated);
                })
                .then();
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
                rekin.id(), rekin.kodePk(), rekin.rekin(), rekin.versi(), indikators
        );
    }

    private PenetapanRekinIndividuResponse.IndikatorPenetapanResponse mapIndikatorToResponse(
            PenetapanRekinIndividu.IndikatorRekinData indikator
    ) {
        List<PenetapanRekinIndividuResponse.TargetPenetapanResponse> targets = indikator.targetPk().stream()
                .map(t -> new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                        t.id(), t.kodeTargetPk(), t.tahun(), t.target(), t.satuan(),
                        null, null, null, null, null, null
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
                rekin.id(), rekin.kodePk(), rekin.rekin(),
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
        CapaianResult capaianResult = hitungCapaian(realisasiValue, target.target());
        String faktorPenunjang = local != null ? local.faktorPenunjang() : null;
        String faktorPenghambat = local != null ? local.faktorPenghambat() : null;
        String jenisRealisasi = "NAIK";
        return new PenetapanRekinIndividuResponse.TargetPenetapanResponse(
                target.id(), target.kodeTargetPk(), target.tahun(), target.target(), target.satuan(),
                realisasiValue, capaianResult.capaian(), capaianResult.keteranganCapaian(),
                faktorPenunjang, faktorPenghambat, jenisRealisasi
        );
    }

    private record CapaianResult(Double capaian, String keteranganCapaian) {}

    private static CapaianResult hitungCapaian(Double realisasi, Double target) {
        if (realisasi == null || target == null || target == 0) {
            return new CapaianResult(null, null);
        }
        double calculatedCapaian = realisasi / target * 100;
        String keteranganCapaian = null;
        if (calculatedCapaian > 100) {
            keteranganCapaian = "nilai capaian lebih dari 100% (" + String.format("%.2f%%", calculatedCapaian) + ")";
        }
        return new CapaianResult(Math.min(calculatedCapaian, 100), keteranganCapaian);
    }

    private Integer parseInteger(String value) {
        return value == null ? null : Integer.parseInt(value);
    }
}
