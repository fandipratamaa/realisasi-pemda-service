package cc.kertaskerja.realisasi_individu_service.renja.domain;

import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanOpd;
import cc.kertaskerja.realisasi_individu_service.renja.domain.kegiatan.RenjaKegiatanOpdIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramOpd;
import cc.kertaskerja.realisasi_individu_service.renja.domain.program.RenjaProgramOpdIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanOpd;
import cc.kertaskerja.realisasi_individu_service.renja.domain.subkegiatan.RenjaSubKegiatanOpdIndividuRepository;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenghambatTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.FaktorPenunjangTargetRenjaKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.kegiatan.RenjaIndividuKegiatanResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenghambatTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.FaktorPenunjangTargetRenjaProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.program.RenjaIndividuProgramResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.RenjaIndividuListResponse;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenghambatTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.FaktorPenunjangTargetRenjaSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanRequest;
import cc.kertaskerja.realisasi_individu_service.renja.web.subkegiatan.RenjaIndividuSubKegiatanResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RenjaIndividuService {
    private final RenjaProgramOpdIndividuRepository programRepo;
    private final RenjaKegiatanOpdIndividuRepository kegiatanRepo;
    private final RenjaSubKegiatanOpdIndividuRepository subKegiatanRepo;

    record CapaianResult(Double capaian, String keteranganCapaian) {}

    public RenjaIndividuService(
            RenjaProgramOpdIndividuRepository programRepo,
            RenjaKegiatanOpdIndividuRepository kegiatanRepo,
            RenjaSubKegiatanOpdIndividuRepository subKegiatanRepo
    ) {
        this.programRepo = programRepo;
        this.kegiatanRepo = kegiatanRepo;
        this.subKegiatanRepo = subKegiatanRepo;
    }

    @Transactional
    public Mono<RenjaIndividuProgramResponse> submitProgram(RenjaIndividuProgramRequest req) {
        return upsertProgram(req)
                .flatMap(this::enrichProgramResponse);
    }

    @Transactional
    public Mono<RenjaIndividuKegiatanResponse> submitKegiatan(RenjaIndividuKegiatanRequest req) {
        return upsertKegiatan(req)
                .flatMap(this::enrichKegiatanResponse);
    }

    @Transactional
    public Mono<RenjaIndividuSubKegiatanResponse> submitSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        return upsertSubKegiatan(req)
                .flatMap(this::enrichSubKegiatanResponse);
    }

    public Mono<RenjaProgramOpd> updateFaktorPenunjangProgram(FaktorPenunjangTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaProgramOpd> updateFaktorPenghambatProgram(FaktorPenghambatTargetRenjaProgramRequest req) {
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target program individu tidak ditemukan")))
                .flatMap(existing -> programRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenunjangKegiatan(FaktorPenunjangTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaKegiatanOpd> updateFaktorPenghambatKegiatan(FaktorPenghambatTargetRenjaKegiatanRequest req) {
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target kegiatan individu tidak ditemukan")))
                .flatMap(existing -> kegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenunjangSubKegiatan(FaktorPenunjangTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenunjang(req.faktorPenunjang())));
    }

    public Mono<RenjaSubKegiatanOpd> updateFaktorPenghambatSubKegiatan(FaktorPenghambatTargetRenjaSubKegiatanRequest req) {
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Target subkegiatan individu tidak ditemukan")))
                .flatMap(existing -> subKegiatanRepo.save(existing.withFaktorPenghambat(req.faktorPenghambat())));
    }

    public Mono<RenjaIndividuListResponse> getRealisasiByKodeOpdAndNipAndTahunAndBulan(
            String kodeOpd, String nip, String tahun, String bulan) {
        Mono<List<RenjaIndividuProgramResponse>> programs = programRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichProgramResponse)
                .collectList();
        Mono<List<RenjaIndividuKegiatanResponse>> kegiatans = kegiatanRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichKegiatanResponse)
                .collectList();
        Mono<List<RenjaIndividuSubKegiatanResponse>> subkegiatans = subKegiatanRepo
                .findAllByKodeOpdAndNipAndTahunAndBulan(kodeOpd, nip, tahun, bulan)
                .flatMap(this::enrichSubKegiatanResponse)
                .collectList();
        return Mono.zip(programs, kegiatans, subkegiatans)
                .map(tuple -> new RenjaIndividuListResponse(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private Mono<RenjaProgramOpd> upsertProgram(RenjaIndividuProgramRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return programRepo.findByKodeOpdAndKodeProgramAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> programRepo.save(new RenjaProgramOpd(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeProgram(), existing.kodeIndikator(), existing.kodeTarget(),
                        existing.kodePagu(), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> programRepo.save(new RenjaProgramOpd(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeProgram(), req.kodeIndikator(), req.kodeTarget(),
                        kodePagu, BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaKegiatanOpd> upsertKegiatan(RenjaIndividuKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return kegiatanRepo.findByKodeOpdAndKodeKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> kegiatanRepo.save(new RenjaKegiatanOpd(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeKegiatan(), existing.kodeIndikator(), existing.kodeTarget(),
                        existing.kodePagu(), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> kegiatanRepo.save(new RenjaKegiatanOpd(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeKegiatan(), req.kodeIndikator(), req.kodeTarget(),
                        kodePagu, BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaSubKegiatanOpd> upsertSubKegiatan(RenjaIndividuSubKegiatanRequest req) {
        String kodePagu = req.kodePagu() != null ? req.kodePagu() : "";
        String jenisRealisasi = req.jenisRealisasi() != null ? req.jenisRealisasi() : "NAIK";
        return subKegiatanRepo.findByKodeOpdAndKodeSubKegiatanAndKodeIndikatorAndKodeTargetAndTahunAndBulan(
                        req.kodeOpd(), req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(), req.tahun(), req.bulan())
                .flatMap(existing -> subKegiatanRepo.save(new RenjaSubKegiatanOpd(
                        existing.id(), existing.kodeOpd(), existing.nip(),
                        existing.tahun(), existing.bulan(),
                        existing.kodeSubKegiatan(), existing.kodeIndikator(), existing.kodeTarget(),
                        existing.kodePagu(), BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        existing.faktorPenunjang(), existing.faktorPenghambat(),
                        existing.createdDate(), null, existing.createdBy(), null
                )))
                .switchIfEmpty(Mono.defer(() -> subKegiatanRepo.save(new RenjaSubKegiatanOpd(
                        null, req.kodeOpd(), req.nip(),
                        req.tahun(), req.bulan(),
                        req.kodeSubKegiatan(), req.kodeIndikator(), req.kodeTarget(),
                        kodePagu, BigDecimal.valueOf(req.realisasi()), jenisRealisasi,
                        "", "",
                        null, null, null, null
                ))));
    }

    private Mono<RenjaIndividuProgramResponse> enrichProgramResponse(RenjaProgramOpd saved) {
        var capaianResult = hitungCapaian(
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null, null);
        return Mono.just(new RenjaIndividuProgramResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeProgram(), saved.kodeIndikator(), saved.kodeTarget(),
                saved.kodePagu(), saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                "NAIK",
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                saved.faktorPenunjang(), saved.faktorPenghambat()
        ));
    }

    private Mono<RenjaIndividuKegiatanResponse> enrichKegiatanResponse(RenjaKegiatanOpd saved) {
        var capaianResult = hitungCapaian(
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null, null);
        return Mono.just(new RenjaIndividuKegiatanResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeKegiatan(), saved.kodeIndikator(), saved.kodeTarget(),
                saved.kodePagu(), saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                "NAIK",
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                saved.faktorPenunjang(), saved.faktorPenghambat()
        ));
    }

    private Mono<RenjaIndividuSubKegiatanResponse> enrichSubKegiatanResponse(RenjaSubKegiatanOpd saved) {
        var capaianResult = hitungCapaian(
                saved.realisasi() != null ? saved.realisasi().doubleValue() : null, null);
        return Mono.just(new RenjaIndividuSubKegiatanResponse(
                saved.id(), saved.kodeOpd(), saved.tahun(), saved.bulan(), saved.nip(),
                saved.kodeSubKegiatan(), saved.kodeIndikator(), saved.kodeTarget(),
                saved.kodePagu(), saved.realisasi() != null ? saved.realisasi().doubleValue() : null,
                "NAIK",
                capaianResult.capaian(), capaianResult.keteranganCapaian(),
                saved.faktorPenunjang(), saved.faktorPenghambat()
        ));
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
}
