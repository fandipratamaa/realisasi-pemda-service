package cc.kertaskerja.realisasi_opd_service.renja_target.web;

import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTarget;
import cc.kertaskerja.realisasi_opd_service.renja_target.domain.RenjaTargetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renja_target")
public class RenjaTargetController {
    private final RenjaTargetService renjaTargetService;

    public RenjaTargetController(RenjaTargetService renjaTargetService) {
        this.renjaTargetService = renjaTargetService;
    }

    @GetMapping
    public Flux<RenjaTarget> getAllRealisasiRenjaTarget() {
        return renjaTargetService.getAllRealisasiRenjaTarget();
    }

    @GetMapping("/find/{id}")
    public Mono<RenjaTarget> getRealisasiRenjaTarget(@PathVariable("id") Long id) {
        return renjaTargetService.getRealisasiRenjaTargetById(id);
    }

    @GetMapping("/by-renja/{renjaId}")
    public Flux<RenjaTarget> getRealisasiRenjaTargetByRenjaId(@PathVariable String renjaId) {
        return renjaTargetService.getRealisasiRenjaTargetByRenjaId(renjaId);
    }

    @GetMapping("/{kodeOpd}")
    public Flux<RenjaTarget> getRealisasiRenjaTargetByKodeOpd(@PathVariable String kodeOpd) {
        return renjaTargetService.getRealisasiRenjaTargetByKodeOpd(kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-tahun/{tahun}")
    public Flux<RenjaTarget> getRealisasiRenjaTargetByTahunAndKodeOpd(
            @PathVariable String kodeOpd,
            @PathVariable String tahun,
            @RequestParam(required = false) String renjaTargetId) {
        if (renjaTargetId != null && !renjaTargetId.isBlank()) {
            return renjaTargetService.getRealisasiRenjaTargetByTahunAndRenjaTargetIdAndKodeOpd(tahun, renjaTargetId, kodeOpd);
        }
        return renjaTargetService.getRealisasiRenjaTargetByTahunAndKodeOpd(tahun, kodeOpd);
    }

    @GetMapping("/{kodeOpd}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    public Flux<RenjaTarget> getRealisasiRenjaTargetByPeriodeRpjmd(@PathVariable String kodeOpd, @PathVariable String tahunAwal, @PathVariable String tahunAkhir) {
        return renjaTargetService.getRealisasiRenjaTargetByPeriodeRpjmd(tahunAwal, tahunAkhir, kodeOpd);
    }

    @GetMapping("/by-indikator/{indikatorId}")
    public Flux<RenjaTarget> getRealisasiRenjaTargetByIndikatorId(@PathVariable String indikatorId) {
        return renjaTargetService.getRealisasiRenjaTargetByIndikatorId(indikatorId);
    }

    @PostMapping
    public Mono<RenjaTarget> submitRealisasiRenjaTarget(@RequestBody @Valid RenjaTargetRequest renjaTargetRequest) {
        return renjaTargetService.submitRealisasiRenjaTarget(
                renjaTargetRequest.renjaTargetId(),
                renjaTargetRequest.renjaTarget(),
                renjaTargetRequest.jenisRenjaTarget(),
                renjaTargetRequest.indikatorId(),
                renjaTargetRequest.indikator(),
                renjaTargetRequest.targetId(),
                renjaTargetRequest.target(),
                renjaTargetRequest.realisasi(),
                renjaTargetRequest.satuan(),
                renjaTargetRequest.tahun(),
                renjaTargetRequest.jenisRealisasi(),
                renjaTargetRequest.kodeOpd()
        );
    }

    @PostMapping("/batch")
    public Flux<RenjaTarget> batchSubmitRealisasiRenjaTarget(@RequestBody @Valid List<RenjaTargetRequest> renjaTargetRequests) {
        return renjaTargetService.batchSubmitRealisasiRenjaTarget(renjaTargetRequests);
    }
}
