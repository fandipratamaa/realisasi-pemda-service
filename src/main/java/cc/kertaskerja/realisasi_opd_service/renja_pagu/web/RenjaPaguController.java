package cc.kertaskerja.realisasi_opd_service.renja_pagu.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPagu;
import cc.kertaskerja.realisasi_opd_service.renja_pagu.domain.RenjaPaguService;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_pagu")
public class RenjaPaguController {
    private final RenjaPaguService renjaPaguService;

    public RenjaPaguController(RenjaPaguService renjaPaguService) {
        this.renjaPaguService = renjaPaguService;
    }

    @GetMapping
    public Flux<RenjaPagu> getAllRealisasiRenjaPagu() {
        return renjaPaguService.getAllRealisasiRenjaPagu();
    }

    @GetMapping("/find/{id}")
    public Mono<RenjaPagu> getRealisasiRenjaPagu(@PathVariable Long id) {
        return renjaPaguService.getRealisasiRenjaPaguById(id);
    }

    @GetMapping("/by-renjaPagu/{renjaPaguId}")
    public Flux<RenjaPagu> getRealisasiRenjaPaguByRenjaPaguId(@PathVariable String renjaPaguId) {
        return renjaPaguService.getRealisasiRenjaPaguByRenjaPaguId(renjaPaguId);
    }

    @GetMapping("/{kodeOpd}")
    public Flux<RenjaPagu> getRealisasiRencanaPaguByKodeOpd(@PathVariable String kodeOpd) {
        return renjaPaguService.getRealisasiRenjaPaguByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    public Flux<RenjaPagu> getRealisasiRenjaPaguByTahunAndKodeOpd(
            @PathVariable String kodeOpd,
            @PathVariable String tahun,
            @RequestParam(required = false) String renjaPaguId) {
        if (renjaPaguId != null && !renjaPaguId.isBlank()) {
            return renjaPaguService.getRealisasiRenjaPaguByTahunAndRenjaPaguIdAndKodeOpd(tahun, renjaPaguId, kodeOpd);
        }
        return renjaPaguService.getRealisasiRenjaPaguByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    public Flux<RenjaPagu> getRealisasiRenjaPaguByPeriodeRpjmd(@PathVariable String kodeOpd, @PathVariable String tahunAwal, @PathVariable String tahunAkhir) {
        return renjaPaguService.getRealisasiRenjaPaguByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @PostMapping
    public Mono<RenjaPagu> submitRealisasiRenjaPagu(@RequestBody @Valid RenjaPaguRequest renjaPaguRequest) {
        return renjaPaguService.submitRealisasiRenjaPagu(
                renjaPaguRequest.renjaPaguId(),
                renjaPaguRequest.renjaPagu(),
                renjaPaguRequest.jenisRenjaPagu(),
                renjaPaguRequest.pagu(),
                renjaPaguRequest.realisasi(),
                renjaPaguRequest.satuan(),
                renjaPaguRequest.tahun(),
                renjaPaguRequest.jenisRealisasi(),
                renjaPaguRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    public Flux<RenjaPagu> batchSubmitRealisasiRenjaPagu(@RequestBody @Valid List<RenjaPaguRequest> renjaPaguRequests) {
        return renjaPaguService.batchSubmitRealisasiRenjaPagu(renjaPaguRequests);
    }
}
