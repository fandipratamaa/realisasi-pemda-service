package cc.kertaskerja.realisasi_individu_service.renja_target_individu.web;

import cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain.RenjaTargetIndividu;
import cc.kertaskerja.realisasi_individu_service.renja_target_individu.domain.RenjaTargetIndividuService;
import cc.kertaskerja.renja.domain.JenisRenja;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("renja_target_individu")
@Tag(name = "Individu - Renja Target", description = "Endpoint realisasi renja target tingkat individu")
public class RenjaTargetIndividuController {
    private final RenjaTargetIndividuService renjaTargetIndividuService;

    public RenjaTargetIndividuController(RenjaTargetIndividuService renjaTargetIndividuService) {
        this.renjaTargetIndividuService = renjaTargetIndividuService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renja target individu", description = "Mengambil seluruh data realisasi renja target individu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getAllRealisasiRenjaTargetIndividu() {
        return renjaTargetIndividuService.getAllRealisasiRenjaTargetIndividu();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Ambil realisasi renja target individu berdasarkan ID", description = "Mengambil satu data realisasi renja target individu berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target individu ditemukan", content = @Content(schema = @Schema(implementation = RenjaTargetIndividu.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenjaTargetIndividu> getRealisasiRenjaTargetIndividu(
            @Parameter(description = "ID internal realisasi renja target individu", example = "1") @PathVariable Long id) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuById(id);
    }

    @GetMapping("/by-renja/{renjaId}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan ID renja", description = "Mengambil daftar realisasi renja target individu berdasarkan `renjaId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByRenjaId(
            @Parameter(description = "ID renja", example = "RENJA-001") @PathVariable String renjaId) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByRenjaId(renjaId);
    }

    @GetMapping("/by-nip/{nip}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan NIP", description = "Mengambil seluruh realisasi renja target untuk satu NIP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByNip(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByNip(nip);
    }

    @GetMapping("/{nip}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi renja target individu per tahun", description = "Mengambil realisasi renja target individu berdasarkan NIP dan tahun, dapat difilter dengan `renjaId` atau `jenisRenja` + `kodeRenja`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndNip(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Filter opsional ID renja", example = "RENJA-001") @RequestParam(required = false) String renjaId,
            @Parameter(description = "Filter opsional jenis renja", example = "PROGRAM") @RequestParam(required = false) JenisRenja jenisRenja,
            @Parameter(description = "Filter opsional kode renja", example = "1.02.01") @RequestParam(required = false) String kodeRenja) {
        if (renjaId != null && !renjaId.isBlank()) {
            return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunAndRenjaIdAndNip(tahun, renjaId, nip);
        }
        if (jenisRenja != null && kodeRenja != null && !kodeRenja.isBlank()) {
            return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunAndJenisRenjaAndKodeRenjaAndNip(tahun, jenisRenja, kodeRenja, nip);
        }
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunAndNip(tahun, nip);
    }

    @GetMapping("/by-nip/{nip}/by-kode")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan kode renja", description = "Mengambil realisasi renja target individu berdasarkan NIP, jenis renja, dan kode renja.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByJenisRenjaAndKodeRenjaAndNip(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Jenis level renja", example = "PROGRAM") @RequestParam JenisRenja jenisRenja,
            @Parameter(description = "Kode renja sesuai jenis", example = "1.02.01") @RequestParam String kodeRenja) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByJenisRenjaAndKodeRenjaAndNip(jenisRenja, kodeRenja, nip);
    }

    @GetMapping("/{nip}/by-periode/{tahunAwal}/{tahunAkhir}/rpjmd")
    @Operation(summary = "Cari realisasi renja target individu periode RPJMD", description = "Mengambil realisasi renja target individu pada rentang tahun RPJMD.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu periode RPJMD", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter periode tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByPeriodeRpjmd(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun awal periode", example = "2025") @PathVariable String tahunAwal,
            @Parameter(description = "Tahun akhir periode", example = "2030") @PathVariable String tahunAkhir) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByPeriodeRpjmd(tahunAwal, tahunAkhir, nip);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja target individu", description = "Menyimpan satu data realisasi renja target individu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target individu tersimpan", content = @Content(schema = @Schema(implementation = RenjaTargetIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaTargetIndividu> submitRealisasiRenjaTargetIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja target individu", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaTargetIndividuRequest.class)))
            @RequestBody @Valid RenjaTargetIndividuRequest renjaTargetIndividuRequest) {
        return renjaTargetIndividuService.submitRealisasiRenjaTargetIndividu(
                renjaTargetIndividuRequest.renjaId(),
                renjaTargetIndividuRequest.renja(),
                renjaTargetIndividuRequest.kodeRenja(),
                renjaTargetIndividuRequest.jenisRenja(),
                renjaTargetIndividuRequest.nip(),
                renjaTargetIndividuRequest.idIndikator(),
                renjaTargetIndividuRequest.indikator(),
                renjaTargetIndividuRequest.targetId(),
                renjaTargetIndividuRequest.target(),
                renjaTargetIndividuRequest.realisasi(),
                renjaTargetIndividuRequest.satuan(),
                renjaTargetIndividuRequest.tahun(),
                renjaTargetIndividuRequest.jenisRealisasi()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja target individu", description = "Menyimpan beberapa data realisasi renja target individu dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> batchSubmitRealisasiRenjaTargetIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renja target individu", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividuRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": 10,\n" +
                                    "    \"renjaId\": \"RENJA-001\",\n" +
                                    "    \"renja\": \"Program Pembangunan Jalan\",\n" +
                                    "    \"kodeRenja\": \"1.02.01\",\n" +
                                    "    \"jenisRenja\": \"PROGRAM\",\n" +
                                    "    \"nip\": \"198012312005011001\",\n" +
                                    "    \"idIndikator\": \"IND-REN-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian renja\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaTargetIndividuRequest> renjaTargetIndividuRequests) {
        return renjaTargetIndividuService.batchSubmitRealisasiRenjaTargetIndividu(renjaTargetIndividuRequests);
    }
}
