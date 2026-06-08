package cc.kertaskerja.realisasi_individu_service.renja.domain;

import cc.kertaskerja.integration.penetapan.PenetapanRenjaOpdClient;
import cc.kertaskerja.integration.penetapan.renja.PenetapanRenjaOpd;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.IndikatorRenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.IndikatorRenjaKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.TargetRenjaKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.TargetRenjaKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.IndikatorRenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.IndikatorRenjaProgramIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.TargetRenjaProgramIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.TargetRenjaProgramIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.IndikatorRenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.IndikatorRenjaSubKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.TargetRenjaSubKegiatanIndividu;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.TargetRenjaSubKegiatanIndividuRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.IndikatorRenjaProgramOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.IndikatorRenjaProgramOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpdHeaderRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.program.RenjaProgramOpdHeader;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.IndikatorRenjaKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.IndikatorRenjaKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpdHeader;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpdHeaderRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.kegiatan.RenjaKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.IndikatorRenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.IndikatorRenjaSubKegiatanOpdRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpdHeader;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpdHeaderRepository;
import cc.kertaskerja.realisasi_opd_service.renja.domain.subkegiatan.RenjaSubKegiatanOpdRepository;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenghambatTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenunjangTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.RenjaIndividuPenetapanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanResponse;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RenjaIndividuService {
    private final PenetapanRenjaOpdClient penetapanClient;
    private final RenjaProgramIndividuRepository programRepo;
    private final IndikatorRenjaProgramIndividuRepository indikatorProgramRepo;
    private final TargetRenjaProgramIndividuRepository targetProgramRepo;
    private final RenjaKegiatanIndividuRepository kegiatanRepo;
    private final IndikatorRenjaKegiatanIndividuRepository indikatorKegiatanRepo;
    private final TargetRenjaKegiatanIndividuRepository targetKegiatanRepo;
    private final RenjaSubKegiatanIndividuRepository subKegiatanRepo;
    private final IndikatorRenjaSubKegiatanIndividuRepository indikatorSubKegiatanRepo;
    private final TargetRenjaSubKegiatanIndividuRepository targetSubKegiatanRepo;

    private final RenjaProgramOpdHeaderRepository programOpdHeaderRepo;
    private final IndikatorRenjaProgramOpdRepository indikatorProgramOpdRepo;
    private final RenjaProgramOpdRepository targetProgramOpdRepo;
    private final RenjaKegiatanOpdHeaderRepository kegiatanOpdHeaderRepo;
    private final IndikatorRenjaKegiatanOpdRepository indikatorKegiatanOpdRepo;
    private final RenjaKegiatanOpdRepository targetKegiatanOpdRepo;
    private final RenjaSubKegiatanOpdHeaderRepository subKegiatanOpdHeaderRepo;
    private final IndikatorRenjaSubKegiatanOpdRepository indikatorSubKegiatanOpdRepo;
    private final RenjaSubKegiatanOpdRepository targetSubKegiatanOpdRepo;

    public RenjaIndividuService(
            PenetapanRenjaOpdClient penetapanClient,
            RenjaProgramIndividuRepository programRepo,
            IndikatorRenjaProgramIndividuRepository indikatorProgramRepo,
            TargetRenjaProgramIndividuRepository targetProgramRepo,
            RenjaKegiatanIndividuRepository kegiatanRepo,
            IndikatorRenjaKegiatanIndividuRepository indikatorKegiatanRepo,
            TargetRenjaKegiatanIndividuRepository targetKegiatanRepo,
            RenjaSubKegiatanIndividuRepository subKegiatanRepo,
            IndikatorRenjaSubKegiatanIndividuRepository indikatorSubKegiatanRepo,
            TargetRenjaSubKegiatanIndividuRepository targetSubKegiatanRepo,
            RenjaProgramOpdHeaderRepository programOpdHeaderRepo,
            IndikatorRenjaProgramOpdRepository indikatorProgramOpdRepo,
            RenjaProgramOpdRepository targetProgramOpdRepo,
            RenjaKegiatanOpdHeaderRepository kegiatanOpdHeaderRepo,
            IndikatorRenjaKegiatanOpdRepository indikatorKegiatanOpdRepo,
            RenjaKegiatanOpdRepository targetKegiatanOpdRepo,
            RenjaSubKegiatanOpdHeaderRepository subKegiatanOpdHeaderRepo,
            IndikatorRenjaSubKegiatanOpdRepository indikatorSubKegiatanOpdRepo,
            RenjaSubKegiatanOpdRepository targetSubKegiatanOpdRepo
    ) {
        this.penetapanClient = penetapanClient;
        this.programRepo = programRepo;
        this.indikatorProgramRepo = indikatorProgramRepo;
        this.targetProgramRepo = targetProgramRepo;
        this.kegiatanRepo = kegiatanRepo;
        this.indikatorKegiatanRepo = indikatorKegiatanRepo;
        this.targetKegiatanRepo = targetKegiatanRepo;
        this.subKegiatanRepo = subKegiatanRepo;
        this.indikatorSubKegiatanRepo = indikatorSubKegiatanRepo;
        this.targetSubKegiatanRepo = targetSubKegiatanRepo;
        this.programOpdHeaderRepo = programOpdHeaderRepo;
        this.indikatorProgramOpdRepo = indikatorProgramOpdRepo;
        this.targetProgramOpdRepo = targetProgramOpdRepo;
        this.kegiatanOpdHeaderRepo = kegiatanOpdHeaderRepo;
        this.indikatorKegiatanOpdRepo = indikatorKegiatanOpdRepo;
        this.targetKegiatanOpdRepo = targetKegiatanOpdRepo;
        this.subKegiatanOpdHeaderRepo = subKegiatanOpdHeaderRepo;
        this.indikatorSubKegiatanOpdRepo = indikatorSubKegiatanOpdRepo;
        this.targetSubKegiatanOpdRepo = targetSubKegiatanOpdRepo;
    }

    record PegawaiInfo(String nip, String namaPegawai) {}

    record RealisasiData(Double realisasi, String faktorPenunjang, String faktorPenghambat) {}

    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasi(String kodeOpd, int tahun, String bulan) {
        return penetapanClient.fetchRenjaOpd(kodeOpd, tahun)
                .flatMap(root -> {
                    String effectiveKodeOpd = root.kodeOpd() != null ? root.kodeOpd() : kodeOpd;
                    Integer effectiveTahun = root.tahunAktif() != null ? root.tahunAktif() : tahun;

                    if (bulan == null || bulan.isBlank()) {
                        return Mono.just(mapWithoutRealisasi(root, effectiveKodeOpd, effectiveTahun));
                    }

                    return fetchRealisasiAndMerge(root, effectiveKodeOpd, effectiveTahun, bulan);
                })
                .defaultIfEmpty(new RenjaIndividuPenetapanResponse(
                        kodeOpd, tahun, parseInteger(bulan),
                        List.of(), List.of(), List.of()
                ));
    }

    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiProgram(String kodeOpd, int tahun, String bulan) {
        return getPenetapanWithRealisasi(kodeOpd, tahun, bulan)
                .map(r -> new RenjaIndividuPenetapanResponse(
                        r.kodeOpd(), r.tahun(), r.bulan(),
                        r.programs(), List.of(), List.of()));
    }

    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiKegiatan(String kodeOpd, int tahun, String bulan) {
        return getPenetapanWithRealisasi(kodeOpd, tahun, bulan)
                .map(r -> new RenjaIndividuPenetapanResponse(
                        r.kodeOpd(), r.tahun(), r.bulan(),
                        List.of(), r.kegiatans(), List.of()));
    }

    public Mono<RenjaIndividuPenetapanResponse> getPenetapanWithRealisasiSubKegiatan(String kodeOpd, int tahun, String bulan) {
        return getPenetapanWithRealisasi(kodeOpd, tahun, bulan)
                .map(r -> new RenjaIndividuPenetapanResponse(
                        r.kodeOpd(), r.tahun(), r.bulan(),
                        List.of(), List.of(), r.subkegiatans()));
    }

    @Transactional
    public Mono<RenjaIndividuProgramResponse> submitProgram(RenjaIndividuProgramRequest req) {
        return upsertProgramHierarchy(req)
                .flatMap(target -> upsertProgramOpdHierarchy(req).thenReturn(target))
                .flatMap(target -> enrichProgramResponse(req, target));
    }

    @Transactional
    public Mono<RenjaIndividuKegiatanResponse> submitKegiatan(RenjaIndividuKegiatanRequest req) {
        return upsertKegiatanHierarchy(req)
                .flatMap(target -> upsertKegiatanOpdHierarchy(req).thenReturn(target))
                .flatMap(target -> enrichKegiatanResponse(req, target));
    }

    @Transactional
    public Mono<RenjaIndividuSubKegiatanResponse> submitSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        return upsertSubKegiatanHierarchy(req)
                .flatMap(target -> upsertSubKegiatanOpdHierarchy(req).thenReturn(target))
                .flatMap(target -> enrichSubKegiatanResponse(req, target));
    }

    private Mono<RenjaIndividuProgramResponse> enrichProgramResponse(
            RenjaIndividuProgramRequest req, TargetRenjaProgramIndividu target) {
        return penetapanClient.fetchRenjaOpd(req.kodeOpd(), Integer.parseInt(req.tahun()))
                .map(root -> toProgramResponse(req, target,
                        findProgram(root, req),
                        findIndikator(root, req.kodeProgram(), req.kodeIndikator()),
                        findTarget(root, req.kodeProgram(), req.kodeIndikator(), target.kodeTarget())))
                .defaultIfEmpty(toProgramResponse(req, target, null, null, null))
                .onErrorResume(e -> Mono.just(toProgramResponse(req, target, null, null, null)));
    }

    private Mono<RenjaIndividuKegiatanResponse> enrichKegiatanResponse(
            RenjaIndividuKegiatanRequest req, TargetRenjaKegiatanIndividu target) {
        return penetapanClient.fetchRenjaOpd(req.kodeOpd(), Integer.parseInt(req.tahun()))
                .map(root -> toKegiatanResponse(req, target,
                        findKegiatan(root, req),
                        findIndikatorKegiatan(root, req.kodeKegiatan(), req.kodeIndikator()),
                        findTargetKegiatan(root, req.kodeKegiatan(), req.kodeIndikator(), target.kodeTarget())))
                .defaultIfEmpty(toKegiatanResponse(req, target, null, null, null))
                .onErrorResume(e -> Mono.just(toKegiatanResponse(req, target, null, null, null)));
    }

    private Mono<RenjaIndividuSubKegiatanResponse> enrichSubKegiatanResponse(
            RenjaIndividuSubKegiatanRequest req, TargetRenjaSubKegiatanIndividu target) {
        return penetapanClient.fetchRenjaOpd(req.kodeOpd(), Integer.parseInt(req.tahun()))
                .map(root -> toSubKegiatanResponse(req, target,
                        findSubKegiatan(root, req),
                        findIndikatorSubKegiatan(root, req.kodeSubKegiatan(), req.kodeIndikator()),
                        findTargetSubKegiatan(root, req.kodeSubKegiatan(), req.kodeIndikator(), target.kodeTarget())))
                .defaultIfEmpty(toSubKegiatanResponse(req, target, null, null, null))
                .onErrorResume(e -> Mono.just(toSubKegiatanResponse(req, target, null, null, null)));
    }

    private static String findProgram(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, RenjaIndividuProgramRequest req) {
        if (root.programs() == null) return null;
        return root.programs().stream()
                .filter(p -> req.kodeProgram().equals(p.kodeProgram()))
                .findFirst()
                .map(PenetapanRenjaOpd.ProgramPenetapanData::program)
                .orElse(null);
    }

    private static String findKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, RenjaIndividuKegiatanRequest req) {
        if (root.kegiatans() == null) return null;
        return root.kegiatans().stream()
                .filter(k -> req.kodeKegiatan().equals(k.kodeKegiatan()))
                .findFirst()
                .map(PenetapanRenjaOpd.KegiatanPenetapanData::kegiatan)
                .orElse(null);
    }

    private static String findSubKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, RenjaIndividuSubKegiatanRequest req) {
        if (root.subkegiatans() == null) return null;
        return root.subkegiatans().stream()
                .filter(s -> req.kodeSubKegiatan().equals(s.kodeSubkegiatan()))
                .findFirst()
                .map(PenetapanRenjaOpd.SubkegiatanPenetapanData::subkegiatan)
                .orElse(null);
    }

    private static String findIndikator(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeProgram, String kodeIndikator) {
        if (root.programs() == null) return null;
        return root.programs().stream()
                .filter(p -> kodeProgram.equals(p.kodeProgram()))
                .findFirst()
                .map(p -> p.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(PenetapanRenjaOpd.IndikatorPenetapanData::indikator)
                .orElse(null);
    }

    private static String findIndikatorKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeKegiatan, String kodeIndikator) {
        if (root.kegiatans() == null) return null;
        return root.kegiatans().stream()
                .filter(k -> kodeKegiatan.equals(k.kodeKegiatan()))
                .findFirst()
                .map(k -> k.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(PenetapanRenjaOpd.IndikatorPenetapanData::indikator)
                .orElse(null);
    }

    private static String findIndikatorSubKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeSubKegiatan, String kodeIndikator) {
        if (root.subkegiatans() == null) return null;
        return root.subkegiatans().stream()
                .filter(s -> kodeSubKegiatan.equals(s.kodeSubkegiatan()))
                .findFirst()
                .map(s -> s.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(PenetapanRenjaOpd.IndikatorPenetapanData::indikator)
                .orElse(null);
    }

    private static Double findTarget(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeProgram, String kodeIndikator, String kodeTarget) {
        if (root.programs() == null) return null;
        return root.programs().stream()
                .filter(p -> kodeProgram.equals(p.kodeProgram()))
                .findFirst()
                .map(p -> p.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(i -> i.targets())
                .flatMap(targets -> targets.stream()
                        .filter(t -> kodeTarget.equals(t.kodeTarget()))
                        .findFirst())
                .map(PenetapanRenjaOpd.TargetPenetapanData::target)
                .orElse(null);
    }

    private static Double findTargetKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeKegiatan, String kodeIndikator, String kodeTarget) {
        if (root.kegiatans() == null) return null;
        return root.kegiatans().stream()
                .filter(k -> kodeKegiatan.equals(k.kodeKegiatan()))
                .findFirst()
                .map(k -> k.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(i -> i.targets())
                .flatMap(targets -> targets.stream()
                        .filter(t -> kodeTarget.equals(t.kodeTarget()))
                        .findFirst())
                .map(PenetapanRenjaOpd.TargetPenetapanData::target)
                .orElse(null);
    }

    private static Double findTargetSubKegiatan(PenetapanRenjaOpd.PenetapanRenjaOpdRoot root, String kodeSubKegiatan, String kodeIndikator, String kodeTarget) {
        if (root.subkegiatans() == null) return null;
        return root.subkegiatans().stream()
                .filter(s -> kodeSubKegiatan.equals(s.kodeSubkegiatan()))
                .findFirst()
                .map(s -> s.indikators())
                .flatMap(ind -> ind.stream()
                        .filter(i -> kodeIndikator.equals(i.kodeIndikator()))
                        .findFirst())
                .map(i -> i.targets())
                .flatMap(targets -> targets.stream()
                        .filter(t -> kodeTarget.equals(t.kodeTarget()))
                        .findFirst())
                .map(PenetapanRenjaOpd.TargetPenetapanData::target)
                .orElse(null);
    }

    private RenjaIndividuProgramResponse toProgramResponse(RenjaIndividuProgramRequest req, TargetRenjaProgramIndividu target, String program, String indikator, Double targetNilai) {
        Double realisasi = target.realisasi() != null ? target.realisasi().doubleValue() : null;
        var capaianResult = hitungCapaian(realisasi, targetNilai);
        return new RenjaIndividuProgramResponse(
                target.id(), req.kodeOpd(), req.tahun(), req.bulan(), req.nip(), req.namaPegawai(),
                req.kodeProgram(), program, req.kodeIndikator(), indikator, target.kodeTarget(),
                targetNilai, realisasi,
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                target.faktorPenunjang(), target.faktorPenghambat()
        );
    }

    private RenjaIndividuKegiatanResponse toKegiatanResponse(RenjaIndividuKegiatanRequest req, TargetRenjaKegiatanIndividu target, String kegiatan, String indikator, Double targetNilai) {
        Double realisasi = target.realisasi() != null ? target.realisasi().doubleValue() : null;
        var capaianResult = hitungCapaian(realisasi, targetNilai);
        return new RenjaIndividuKegiatanResponse(
                target.id(), req.kodeOpd(), req.tahun(), req.bulan(), req.nip(), req.namaPegawai(),
                req.kodeKegiatan(), kegiatan, req.kodeIndikator(), indikator, target.kodeTarget(),
                targetNilai, realisasi,
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                target.faktorPenunjang(), target.faktorPenghambat()
        );
    }

    private RenjaIndividuSubKegiatanResponse toSubKegiatanResponse(RenjaIndividuSubKegiatanRequest req, TargetRenjaSubKegiatanIndividu target, String subkegiatan, String indikator, Double targetNilai) {
        Double realisasi = target.realisasi() != null ? target.realisasi().doubleValue() : null;
        var capaianResult = hitungCapaian(realisasi, targetNilai);
        return new RenjaIndividuSubKegiatanResponse(
                target.id(), req.kodeOpd(), req.tahun(), req.bulan(), req.nip(), req.namaPegawai(),
                req.kodeSubKegiatan(), subkegiatan, req.kodeIndikator(), indikator, target.kodeTarget(),
                targetNilai, realisasi,
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                target.faktorPenunjang(), target.faktorPenghambat()
        );
    }

    private Mono<TargetRenjaProgramIndividu> upsertProgramHierarchy(RenjaIndividuProgramRequest req) {
        return programRepo.findByKodeProgram(req.kodeProgram())
                .switchIfEmpty(Mono.defer(() -> programRepo.save(new RenjaProgramIndividu(
                        null, req.kodeOpd(), req.kodeProgram(),
                        req.nip(), req.namaPegawai(),
                        req.tahun(), req.bulan(),
                        null, null, null, null
                ))))
                .flatMap(program -> indikatorProgramRepo
                        .findByRenjaProgramIndividuIdAndTahunAndBulan(program.id(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorProgramRepo.save(new IndikatorRenjaProgramIndividu(
                                null, program.id(), req.kodeIndikator(),
                                req.tahun(), req.bulan(),
                                null, null, null, null
                        ))))
                        .flatMap(indikator -> upsertTargetProgram(indikator, req))
                );
    }

    private Mono<TargetRenjaProgramIndividu> upsertTargetProgram(IndikatorRenjaProgramIndividu indikator, RenjaIndividuProgramRequest req) {
        return targetProgramRepo
                .findByIndikatorRenjaProgramIndividuIdAndTahunAndBulan(indikator.id(), req.tahun(), req.bulan())
                .flatMap(existing -> targetProgramRepo.save(new TargetRenjaProgramIndividu(
                        existing.id(), existing.indikatorRenjaProgramIndividuId(),
                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null,
                        existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> targetProgramRepo.save(new TargetRenjaProgramIndividu(
                        null, indikator.id(), req.kodeTarget(),
                        req.tahun(), req.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<TargetRenjaKegiatanIndividu> upsertKegiatanHierarchy(RenjaIndividuKegiatanRequest req) {
        return kegiatanRepo.findByKodeKegiatan(req.kodeKegiatan())
                .switchIfEmpty(Mono.defer(() -> kegiatanRepo.save(new RenjaKegiatanIndividu(
                        null, req.kodeOpd(), req.kodeProgram(), req.kodeKegiatan(),
                        req.nip(), req.namaPegawai(),
                        req.tahun(), req.bulan(),
                        null, null, null, null
                ))))
                .flatMap(kegiatan -> indikatorKegiatanRepo
                        .findByRenjaKegiatanIndividuIdAndTahunAndBulan(kegiatan.id(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorKegiatanRepo.save(new IndikatorRenjaKegiatanIndividu(
                                null, kegiatan.id(), req.kodeIndikator(),
                                req.tahun(), req.bulan(),
                                null, null, null, null
                        ))))
                        .flatMap(indikator -> upsertTargetKegiatan(indikator, req))
                );
    }

    private Mono<TargetRenjaKegiatanIndividu> upsertTargetKegiatan(IndikatorRenjaKegiatanIndividu indikator, RenjaIndividuKegiatanRequest req) {
        return targetKegiatanRepo
                .findByIndikatorRenjaKegiatanIndividuIdAndTahunAndBulan(indikator.id(), req.tahun(), req.bulan())
                .flatMap(existing -> targetKegiatanRepo.save(new TargetRenjaKegiatanIndividu(
                        existing.id(), existing.indikatorRenjaKegiatanIndividuId(),
                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null,
                        existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> targetKegiatanRepo.save(new TargetRenjaKegiatanIndividu(
                        null, indikator.id(), req.kodeTarget(),
                        req.tahun(), req.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<TargetRenjaSubKegiatanIndividu> upsertSubKegiatanHierarchy(RenjaIndividuSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeSubKegiatan(req.kodeSubKegiatan())
                .switchIfEmpty(Mono.defer(() -> subKegiatanRepo.save(new RenjaSubKegiatanIndividu(
                        null, req.kodeOpd(), req.kodeKegiatan(), req.kodeSubKegiatan(),
                        req.nip(), req.namaPegawai(),
                        req.tahun(), req.bulan(),
                        null, null, null, null
                ))))
                .flatMap(subKegiatan -> indikatorSubKegiatanRepo
                        .findByRenjaSubKegiatanIndividuIdAndTahunAndBulan(subKegiatan.id(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorSubKegiatanRepo.save(new IndikatorRenjaSubKegiatanIndividu(
                                null, subKegiatan.id(), req.kodeIndikator(),
                                req.tahun(), req.bulan(),
                                null, null, null, null
                        ))))
                        .flatMap(indikator -> upsertTargetSubKegiatan(indikator, req))
                );
    }

    private Mono<TargetRenjaSubKegiatanIndividu> upsertTargetSubKegiatan(IndikatorRenjaSubKegiatanIndividu indikator, RenjaIndividuSubKegiatanRequest req) {
        return targetSubKegiatanRepo
                .findByIndikatorRenjaSubKegiatanIndividuIdAndTahunAndBulan(indikator.id(), req.tahun(), req.bulan())
                .flatMap(existing -> targetSubKegiatanRepo.save(new TargetRenjaSubKegiatanIndividu(
                        existing.id(), existing.indikatorRenjaSubKegiatanIndividuId(),
                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null,
                        existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> targetSubKegiatanRepo.save(new TargetRenjaSubKegiatanIndividu(
                        null, indikator.id(), req.kodeTarget(),
                        req.tahun(), req.bulan(),
                        BigDecimal.valueOf(req.realisasi()),
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<Void> upsertProgramOpdHierarchy(RenjaIndividuProgramRequest req) {
        return programOpdHeaderRepo.findByKodeOpdAndKodeProgramAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> programOpdHeaderRepo.save(
                        RenjaProgramOpdHeader.of(req.kodeOpd(), req.kodeProgram(), req.tahun(), req.bulan()))))
                .flatMap(header -> indikatorProgramOpdRepo
                        .findByRenjaProgramOpdIdAndKodeIndikatorAndTahunAndBulan(
                                header.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorProgramOpdRepo.save(
                                new IndikatorRenjaProgramOpd(null, header.id(), req.kodeIndikator(),
                                        req.tahun(), req.bulan(), null, null, null, null))))
                        .flatMap(indikator -> targetProgramOpdRepo
                                .findByIndikatorRenjaProgramOpdIdAndKodeTargetAndTahunAndBulan(
                                        indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .flatMap(existing -> targetProgramOpdRepo.save(new RenjaProgramOpd(
                                        existing.id(), existing.indikatorRenjaProgramOpdId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        BigDecimal.valueOf(req.realisasi()),
                                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null, existing.createdBy(), null)))
                                .switchIfEmpty(Mono.defer(() -> targetProgramOpdRepo.save(new RenjaProgramOpd(
                                        null, indikator.id(),
                                        req.kodeTarget(),
                                        req.tahun(), req.bulan(), BigDecimal.valueOf(req.realisasi()),
                                        "", "",
                                        null, null, null, null))))
                                .then()));
    }

    private Mono<Void> upsertKegiatanOpdHierarchy(RenjaIndividuKegiatanRequest req) {
        return kegiatanOpdHeaderRepo.findByKodeOpdAndKodeKegiatanAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> kegiatanOpdHeaderRepo.save(
                        RenjaKegiatanOpdHeader.of(req.kodeOpd(), req.kodeProgram(), req.kodeKegiatan(), req.tahun(), req.bulan()))))
                .flatMap(header -> indikatorKegiatanOpdRepo
                        .findByRenjaKegiatanOpdIdAndKodeIndikatorAndTahunAndBulan(
                                header.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorKegiatanOpdRepo.save(
                                new IndikatorRenjaKegiatanOpd(null, header.id(), req.kodeIndikator(),
                                        req.tahun(), req.bulan(), null, null, null, null))))
                        .flatMap(indikator -> targetKegiatanOpdRepo
                                .findByIndikatorRenjaKegiatanOpdIdAndKodeTargetAndTahunAndBulan(
                                        indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .flatMap(existing -> targetKegiatanOpdRepo.save(new RenjaKegiatanOpd(
                                        existing.id(), existing.indikatorRenjaKegiatanOpdId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        BigDecimal.valueOf(req.realisasi()),
                                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null, existing.createdBy(), null)))
                                .switchIfEmpty(Mono.defer(() -> targetKegiatanOpdRepo.save(new RenjaKegiatanOpd(
                                        null, indikator.id(),
                                        req.kodeTarget(),
                                        req.tahun(), req.bulan(), BigDecimal.valueOf(req.realisasi()),
                                        "", "",
                                        null, null, null, null))))
                                .then()));
    }

    private Mono<Void> upsertSubKegiatanOpdHierarchy(RenjaIndividuSubKegiatanRequest req) {
        return subKegiatanOpdHeaderRepo.findByKodeOpdAndKodeSubKegiatanAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.defer(() -> subKegiatanOpdHeaderRepo.save(
                        RenjaSubKegiatanOpdHeader.of(req.kodeOpd(), req.kodeKegiatan(), req.kodeSubKegiatan(), req.tahun(), req.bulan()))))
                .flatMap(header -> indikatorSubKegiatanOpdRepo
                        .findByRenjaSubKegiatanOpdIdAndKodeIndikatorAndTahunAndBulan(
                                header.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.defer(() -> indikatorSubKegiatanOpdRepo.save(
                                new IndikatorRenjaSubKegiatanOpd(null, header.id(), req.kodeIndikator(),
                                        req.tahun(), req.bulan(), null, null, null, null))))
                        .flatMap(indikator -> targetSubKegiatanOpdRepo
                                .findByIndikatorRenjaSubKegiatanOpdIdAndKodeTargetAndTahunAndBulan(
                                        indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .flatMap(existing -> targetSubKegiatanOpdRepo.save(new RenjaSubKegiatanOpd(
                                        existing.id(), existing.indikatorRenjaSubKegiatanOpdId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        BigDecimal.valueOf(req.realisasi()),
                                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null, existing.createdBy(), null)))
                                .switchIfEmpty(Mono.defer(() -> targetSubKegiatanOpdRepo.save(new RenjaSubKegiatanOpd(
                                        null, indikator.id(),
                                        req.kodeTarget(),
                                        req.tahun(), req.bulan(), BigDecimal.valueOf(req.realisasi()),
                                        "", "",
                                        null, null, null, null))))
                                .then()));
    }

    public Mono<TargetRenjaProgramIndividu> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramRequest req) {
        return programRepo.findByKodeProgram(req.kodeProgram())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Program individu tidak ditemukan")))
                .flatMap(program -> indikatorProgramRepo
                        .findByRenjaProgramIndividuIdAndKodeIndikatorAndTahunAndBulan(program.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator program individu tidak ditemukan")))
                        .flatMap(indikator -> targetProgramRepo
                                .findByIndikatorRenjaProgramIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                                .flatMap(existing -> targetProgramRepo.save(new TargetRenjaProgramIndividu(
                                        existing.id(), existing.indikatorRenjaProgramIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        req.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    public Mono<TargetRenjaProgramIndividu> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramRequest req) {
        return programRepo.findByKodeProgram(req.kodeProgram())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Program individu tidak ditemukan")))
                .flatMap(program -> indikatorProgramRepo
                        .findByRenjaProgramIndividuIdAndKodeIndikatorAndTahunAndBulan(program.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator program individu tidak ditemukan")))
                        .flatMap(indikator -> targetProgramRepo
                                .findByIndikatorRenjaProgramIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                                .flatMap(existing -> targetProgramRepo.save(new TargetRenjaProgramIndividu(
                                        existing.id(), existing.indikatorRenjaProgramIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        existing.faktorPenunjang(), req.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    public Mono<TargetRenjaKegiatanIndividu> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeKegiatan(req.kodeKegiatan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Kegiatan individu tidak ditemukan")))
                .flatMap(kegiatan -> indikatorKegiatanRepo
                        .findByRenjaKegiatanIndividuIdAndKodeIndikatorAndTahunAndBulan(kegiatan.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator kegiatan individu tidak ditemukan")))
                        .flatMap(indikator -> targetKegiatanRepo
                                .findByIndikatorRenjaKegiatanIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                                .flatMap(existing -> targetKegiatanRepo.save(new TargetRenjaKegiatanIndividu(
                                        existing.id(), existing.indikatorRenjaKegiatanIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        req.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    public Mono<TargetRenjaKegiatanIndividu> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeKegiatan(req.kodeKegiatan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Kegiatan individu tidak ditemukan")))
                .flatMap(kegiatan -> indikatorKegiatanRepo
                        .findByRenjaKegiatanIndividuIdAndKodeIndikatorAndTahunAndBulan(kegiatan.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator kegiatan individu tidak ditemukan")))
                        .flatMap(indikator -> targetKegiatanRepo
                                .findByIndikatorRenjaKegiatanIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                                .flatMap(existing -> targetKegiatanRepo.save(new TargetRenjaKegiatanIndividu(
                                        existing.id(), existing.indikatorRenjaKegiatanIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        existing.faktorPenunjang(), req.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    public Mono<TargetRenjaSubKegiatanIndividu> updateFaktorPenunjangSubKegiatan(FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeSubKegiatan(req.kodeSubKegiatan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Subkegiatan individu tidak ditemukan")))
                .flatMap(subKegiatan -> indikatorSubKegiatanRepo
                        .findByRenjaSubKegiatanIndividuIdAndKodeIndikatorAndTahunAndBulan(subKegiatan.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator subkegiatan individu tidak ditemukan")))
                        .flatMap(indikator -> targetSubKegiatanRepo
                                .findByIndikatorRenjaSubKegiatanIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                                .flatMap(existing -> targetSubKegiatanRepo.save(new TargetRenjaSubKegiatanIndividu(
                                        existing.id(), existing.indikatorRenjaSubKegiatanIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        req.faktorPenunjang(), existing.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    public Mono<TargetRenjaSubKegiatanIndividu> updateFaktorPenghambatSubKegiatan(FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeSubKegiatan(req.kodeSubKegiatan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Subkegiatan individu tidak ditemukan")))
                .flatMap(subKegiatan -> indikatorSubKegiatanRepo
                        .findByRenjaSubKegiatanIndividuIdAndKodeIndikatorAndTahunAndBulan(subKegiatan.id(), req.kodeIndikator(), req.tahun(), req.bulan())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Indikator subkegiatan individu tidak ditemukan")))
                        .flatMap(indikator -> targetSubKegiatanRepo
                                .findByIndikatorRenjaSubKegiatanIndividuIdAndKodeTargetAndTahunAndBulan(indikator.id(), req.kodeTarget(), req.tahun(), req.bulan())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                                .flatMap(existing -> targetSubKegiatanRepo.save(new TargetRenjaSubKegiatanIndividu(
                                        existing.id(), existing.indikatorRenjaSubKegiatanIndividuId(),
                                        existing.kodeTarget(), existing.tahun(), existing.bulan(),
                                        existing.realisasi(),
                                        existing.faktorPenunjang(), req.faktorPenghambat(),
                                        existing.createdDate(), null,
                                        existing.createdBy(), null
                                )))
                        )
                );
    }

    private Mono<RenjaIndividuPenetapanResponse> fetchRealisasiAndMerge(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun, String bulan
    ) {
        String tahunStr = String.valueOf(tahun);

        Mono<Map<String, RealisasiData>> programRealisasiMap = targetProgramRepo
                .findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(TargetRenjaProgramIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(),
                                t.faktorPenghambat()));

        Mono<Map<String, RealisasiData>> kegiatanRealisasiMap = targetKegiatanRepo
                .findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(TargetRenjaKegiatanIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(),
                                t.faktorPenghambat()));

        Mono<Map<String, RealisasiData>> subKegiatanRealisasiMap = targetSubKegiatanRepo
                .findAllByTahunAndBulan(tahunStr, bulan)
                .collectMap(TargetRenjaSubKegiatanIndividu::kodeTarget,
                        t -> new RealisasiData(
                                t.realisasi() != null ? t.realisasi().doubleValue() : null,
                                t.faktorPenunjang(),
                                t.faktorPenghambat()));

        Mono<Map<String, PegawaiInfo>> programPegawaiMap = programRepo
                .findAllByKodeOpdAndTahun(kodeOpd, tahunStr)
                .collectMap(RenjaProgramIndividu::kodeProgram,
                        h -> new PegawaiInfo(h.nip(), h.namaPegawai()));

        Mono<Map<String, PegawaiInfo>> kegiatanPegawaiMap = kegiatanRepo
                .findAllByKodeOpdAndTahun(kodeOpd, tahunStr)
                .collectMap(RenjaKegiatanIndividu::kodeKegiatan,
                        h -> new PegawaiInfo(h.nip(), h.namaPegawai()));

        Mono<Map<String, PegawaiInfo>> subKegiatanPegawaiMap = subKegiatanRepo
                .findAllByKodeOpdAndTahun(kodeOpd, tahunStr)
                .collectMap(RenjaSubKegiatanIndividu::kodeSubKegiatan,
                        h -> new PegawaiInfo(h.nip(), h.namaPegawai()));

        return Mono.zip(programRealisasiMap, kegiatanRealisasiMap, subKegiatanRealisasiMap,
                        programPegawaiMap, kegiatanPegawaiMap, subKegiatanPegawaiMap)
                .map(tuple -> {
                    Map<String, RealisasiData> progMap = tuple.getT1();
                    Map<String, RealisasiData> kegMap = tuple.getT2();
                    Map<String, RealisasiData> subMap = tuple.getT3();
                    Map<String, PegawaiInfo> progPeg = tuple.getT4();
                    Map<String, PegawaiInfo> kegPeg = tuple.getT5();
                    Map<String, PegawaiInfo> subPeg = tuple.getT6();

                    List<RenjaIndividuPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                            .map(p -> mergeProgramWithRealisasi(p, progMap, progPeg, bulan))
                            .filter(p -> !p.indikators().isEmpty())
                            .toList();

                    List<RenjaIndividuPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                            .map(k -> mergeKegiatanWithRealisasi(k, kegMap, kegPeg, bulan))
                            .filter(k -> !k.indikators().isEmpty())
                            .toList();

                    List<RenjaIndividuPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                            .map(s -> mergeSubKegiatanWithRealisasi(s, subMap, subPeg, bulan))
                            .filter(s -> !s.indikators().isEmpty())
                            .toList();

                    return new RenjaIndividuPenetapanResponse(
                            kodeOpd, tahun, parseInteger(bulan),
                            programs, kegiatans, subkegiatans
                    );
                });
    }

    private RenjaIndividuPenetapanResponse mapWithoutRealisasi(
            PenetapanRenjaOpd.PenetapanRenjaOpdRoot root,
            String kodeOpd, int tahun
    ) {
        List<RenjaIndividuPenetapanResponse.ProgramPenetapan> programs = safeList(root.programs()).stream()
                .map(this::toProgramPenetapan)
                .toList();

        List<RenjaIndividuPenetapanResponse.KegiatanPenetapan> kegiatans = safeList(root.kegiatans()).stream()
                .map(this::toKegiatanPenetapan)
                .toList();

        List<RenjaIndividuPenetapanResponse.SubkegiatanPenetapan> subkegiatans = safeList(root.subkegiatans()).stream()
                .map(this::toSubKegiatanPenetapan)
                .toList();

        return new RenjaIndividuPenetapanResponse(kodeOpd, tahun, null, programs, kegiatans, subkegiatans);
    }

    private RenjaIndividuPenetapanResponse.ProgramPenetapan toProgramPenetapan(
            PenetapanRenjaOpd.ProgramPenetapanData p
    ) {
        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> indikators = safeList(p.indikators()).stream()
                .map(ind -> new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), null, t.satuan(), null, null,
                                        "", ""
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaIndividuPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), null, null,
                indikators, p.paguAnggaran()
        );
    }

    private RenjaIndividuPenetapanResponse.KegiatanPenetapan toKegiatanPenetapan(
            PenetapanRenjaOpd.KegiatanPenetapanData k
    ) {
        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> indikators = safeList(k.indikators()).stream()
                .map(ind -> new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), null, t.satuan(), null, null,
                                        "", ""
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaIndividuPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), null, null,
                indikators, k.paguAnggaran()
        );
    }

    private RenjaIndividuPenetapanResponse.SubkegiatanPenetapan toSubKegiatanPenetapan(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s
    ) {
        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> indikators = safeList(s.indikators()).stream()
                .map(ind -> new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                        ind.id(), ind.kodeIndikator(), ind.indikator(),
                        ind.targets().stream()
                                .map(t -> new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), null, t.satuan(), null, null,
                                        "", ""
                                ))
                                .toList()
                ))
                .toList();

        return new RenjaIndividuPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), null, null,
                indikators, s.paguAnggaran()
        );
    }

    private RenjaIndividuPenetapanResponse.ProgramPenetapan mergeProgramWithRealisasi(
            PenetapanRenjaOpd.ProgramPenetapanData p,
            Map<String, RealisasiData> realisasiMap,
            Map<String, PegawaiInfo> pegawaiMap,
            String bulan
    ) {
        PegawaiInfo info = p.kodeProgram() != null ? pegawaiMap.get(p.kodeProgram()) : null;
        String nip = info != null ? info.nip() : null;
        String namaPegawai = info != null ? info.namaPegawai() : null;

        RenjaIndividuPenetapanResponse.ProgramPenetapan result = toProgramPenetapan(p);

        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> mergedIndikators = result.indikators().stream()
                .map(ind -> {
                    List<RenjaIndividuPenetapanResponse.TargetPenetapan> mergedTargets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                String faktorPenunjang = data != null ? data.faktorPenunjang() : "";
                                String faktorPenghambat = data != null ? data.faktorPenghambat() : "";
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        faktorPenunjang, faktorPenghambat
                                );
                            })
                            .toList();

                    if (mergedTargets.isEmpty()) return null;

                    return new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), mergedTargets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaIndividuPenetapanResponse.ProgramPenetapan(
                p.id(), p.kodeProgram(), p.program(), nip, namaPegawai,
                mergedIndikators, p.paguAnggaran()
        );
    }

    private RenjaIndividuPenetapanResponse.KegiatanPenetapan mergeKegiatanWithRealisasi(
            PenetapanRenjaOpd.KegiatanPenetapanData k,
            Map<String, RealisasiData> realisasiMap,
            Map<String, PegawaiInfo> pegawaiMap,
            String bulan
    ) {
        PegawaiInfo info = k.kodeKegiatan() != null ? pegawaiMap.get(k.kodeKegiatan()) : null;
        String nip = info != null ? info.nip() : null;
        String namaPegawai = info != null ? info.namaPegawai() : null;

        RenjaIndividuPenetapanResponse.KegiatanPenetapan result = toKegiatanPenetapan(k);

        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> mergedIndikators = result.indikators().stream()
                .map(ind -> {
                    List<RenjaIndividuPenetapanResponse.TargetPenetapan> mergedTargets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                String faktorPenunjang = data != null ? data.faktorPenunjang() : "";
                                String faktorPenghambat = data != null ? data.faktorPenghambat() : "";
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        faktorPenunjang, faktorPenghambat
                                );
                            })
                            .toList();

                    if (mergedTargets.isEmpty()) return null;

                    return new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), mergedTargets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaIndividuPenetapanResponse.KegiatanPenetapan(
                k.id(), k.kodeKegiatan(), k.kegiatan(), nip, namaPegawai,
                mergedIndikators, k.paguAnggaran()
        );
    }

    private RenjaIndividuPenetapanResponse.SubkegiatanPenetapan mergeSubKegiatanWithRealisasi(
            PenetapanRenjaOpd.SubkegiatanPenetapanData s,
            Map<String, RealisasiData> realisasiMap,
            Map<String, PegawaiInfo> pegawaiMap,
            String bulan
    ) {
        PegawaiInfo info = s.kodeSubkegiatan() != null ? pegawaiMap.get(s.kodeSubkegiatan()) : null;
        String nip = info != null ? info.nip() : null;
        String namaPegawai = info != null ? info.namaPegawai() : null;

        RenjaIndividuPenetapanResponse.SubkegiatanPenetapan result = toSubKegiatanPenetapan(s);

        List<RenjaIndividuPenetapanResponse.IndikatorPenetapan> mergedIndikators = result.indikators().stream()
                .map(ind -> {
                    List<RenjaIndividuPenetapanResponse.TargetPenetapan> mergedTargets = ind.targets().stream()
                            .map(t -> {
                                RealisasiData data = realisasiMap.get(t.kodeTarget());
                                Double realisasi = data != null ? data.realisasi() : null;
                                String faktorPenunjang = data != null ? data.faktorPenunjang() : "";
                                String faktorPenghambat = data != null ? data.faktorPenghambat() : "";
                                var capaianResult = hitungCapaian(realisasi, t.target());
                                return new RenjaIndividuPenetapanResponse.TargetPenetapan(
                                        t.id(), t.kodeTarget(), t.tahun(),
                                        t.target(), realisasi, t.satuan(),
                                        capaianResult.capaian(), capaianResult.keteranganCapaian(),
                                        faktorPenunjang, faktorPenghambat
                                );
                            })
                            .toList();

                    if (mergedTargets.isEmpty()) return null;

                    return new RenjaIndividuPenetapanResponse.IndikatorPenetapan(
                            ind.id(), ind.kodeIndikator(), ind.indikator(), mergedTargets
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new RenjaIndividuPenetapanResponse.SubkegiatanPenetapan(
                s.id(), s.kodeSubkegiatan(), s.subkegiatan(), nip, namaPegawai,
                mergedIndikators, s.paguAnggaran()
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
