package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import cc.kertaskerja.realisasi.domain.JenisRealisasi;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenghambatRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.FaktorPenunjangRenaksiRequest;
import cc.kertaskerja.realisasi_individu_service.renaksi.web.RenaksiIndividuRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RenaksiService {
    private final SasaranIndividuRepository sasaranIndividuRepository;
    private final RenaksiIndividuRepository renaksiIndividuRepository;

    public RenaksiService(SasaranIndividuRepository sasaranIndividuRepository,
                          RenaksiIndividuRepository renaksiIndividuRepository) {
        this.sasaranIndividuRepository = sasaranIndividuRepository;
        this.renaksiIndividuRepository = renaksiIndividuRepository;
    }

    public Flux<SasaranIndividu> getSasaranByNipAndTahun(String nip, String tahun) {
        return sasaranIndividuRepository.findAllByNipAndTahun(nip, tahun);
    }

    public Flux<SasaranIndividu> getSasaranByKodeOpdAndTahunAndBulan(String kodeOpd, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    public Mono<RenaksiIndividu> createSasaran(RenaksiIndividuRequest req) {
        Mono<SasaranIndividu> sasaranMono = sasaranIndividuRepository
                .findFirstByNipAndTahunAndBulanAndKodeSasaran(req.nip(), req.tahun(), req.bulan(), req.kodeSasaran())
                .flatMap(existing -> sasaranIndividuRepository.save(buildUpdatedSasaran(existing, req)))
                .switchIfEmpty(Mono.defer(() -> {
                    SasaranIndividu baru = buildUncheckedSasaran(
                            req.kodeOpd(), req.nip(), req.kodeSasaran(),
                            req.tahun(), req.bulan());
                    return sasaranIndividuRepository.save(baru);
                }));

        return sasaranMono
                .flatMap(savedSasaran ->
                        upsertRealisasiTarget(req)
                );
    }

    private Mono<RenaksiIndividu> upsertRealisasiTarget(RenaksiIndividuRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTarget(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget())
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            req.kodeOpd(), req.nip(), req.kodeSasaran(),
                            req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget(),
                            req.paguAnggaran(), req.realisasi(), JenisRealisasi.NAIK,
                            existing.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    RenaksiIndividu baru = RenaksiIndividu.of(
                            req.kodeOpd(), req.nip(), req.kodeSasaran(),
                            req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget(),
                            req.paguAnggaran(), req.realisasi(), JenisRealisasi.NAIK, "", "");
                    return renaksiIndividuRepository.save(baru);
                }));
    }

    public Mono<RenaksiIndividu> updateFaktorPenunjang(FaktorPenunjangRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTarget(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.kodeSasaran(),
                            existing.kodeRenaksi(), existing.kodeIndikator(), existing.kodeTarget(),
                            existing.paguAnggaran(), existing.realisasi(), existing.jenisRealisasi(),
                            req.faktorPenunjang(), existing.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                });
    }

    public Mono<RenaksiIndividu> updateFaktorPenghambat(FaktorPenghambatRenaksiRequest req) {
        return renaksiIndividuRepository
                .findFirstByKodeOpdAndNipAndKodeSasaranAndKodeRenaksiAndKodeIndikatorAndKodeTarget(
                        req.kodeOpd(), req.nip(), req.kodeSasaran(),
                        req.kodeRenaksi(), req.kodeIndikator(), req.kodeTarget())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Data realisasi tidak ditemukan")))
                .flatMap(existing -> {
                    RenaksiIndividu updated = new RenaksiIndividu(
                            existing.id(),
                            existing.kodeOpd(), existing.nip(), existing.kodeSasaran(),
                            existing.kodeRenaksi(), existing.kodeIndikator(), existing.kodeTarget(),
                            existing.paguAnggaran(), existing.realisasi(), existing.jenisRealisasi(),
                            existing.faktorPenunjang(), req.faktorPenghambat(),
                            existing.createdBy(), existing.lastModifiedBy(),
                            existing.createdDate(), existing.lastModifiedDate()
                    );
                    return renaksiIndividuRepository.save(updated);
                });
    }

    public static SasaranIndividu buildUncheckedSasaran(
            String kodeOpd, String nip, String kodeSasaran, String tahun, String bulan) {
        return SasaranIndividu.of(kodeOpd, nip, kodeSasaran, "Realisasi Sasaran " + kodeSasaran,
                tahun, bulan, RenaksiStatus.UNCHECKED);
    }

    private static SasaranIndividu buildUpdatedSasaran(SasaranIndividu existing, RenaksiIndividuRequest req) {
        return new SasaranIndividu(
                existing.id(), req.kodeOpd(), req.nip(), req.kodeSasaran(),
                existing.sasaran(), req.tahun(), req.bulan(), RenaksiStatus.UNCHECKED,
                existing.createdBy(), existing.lastModifiedBy(),
                existing.createdDate(), existing.lastModifiedDate(), existing.version()
        );
    }

    public Flux<SasaranWithDetails> getSasaranWithDetailsByNipAndKodeOpdAndTahunAndBulan(String nip, String kodeOpd, String tahun, String bulan) {
        return sasaranIndividuRepository.findAllByNipAndKodeOpdAndTahunAndBulan(nip, kodeOpd, tahun, bulan)
                .flatMap(this::enrichWithDetails);
    }

    private Mono<SasaranWithDetails> enrichWithDetails(SasaranIndividu sasaran) {
        return renaksiIndividuRepository
                .findAllByKodeOpdAndNipAndKodeSasaran(sasaran.kodeOpd(), sasaran.nip(), sasaran.kodeSasaran())
                .collectList()
                .map(realisasiTargets -> new SasaranWithDetails(sasaran, realisasiTargets));
    }
}
