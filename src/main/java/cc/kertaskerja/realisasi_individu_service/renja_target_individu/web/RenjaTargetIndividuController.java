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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
    @Operation(summary = "Ambil semua realisasi renja target individu (belum digunakan di endpoint realisasi)", description = "Mengambil seluruh data realisasi renja target individu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getAllRealisasiRenjaTargetIndividu() {
        return renjaTargetIndividuService.getAllRealisasiRenjaTargetIndividu();
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan NIP, tahun, dan bulan", description = "Mengambil realisasi renja target individu berdasarkan NIP, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndNipAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunNipAndBulan(tahun, nip, bulan);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan tahun, bulan, dan kode OPD", description = "Mengambil realisasi renja target individu berdasarkan tahun, bulan, dan `kode_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan kode OPD, NIP, tahun, dan bulan", description = "Mengambil realisasi renja target individu berdasarkan `kode_opd`, `nip`, `tahun`, dan `bulan`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja target individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaTargetIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunNipAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunAndNipAndBulanAndKodeOpd(tahun, nip, bulan, kodeOpd);
    }

    @GetMapping("/by-tahun/{tahun}/by-nip/{nip}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja}")
    @Operation(summary = "Cari realisasi renja target individu berdasarkan tahun, NIP, jenis renja, dan kode renja", description = "Mengambil satu data realisasi renja target individu berdasarkan `tahun`, `nip`, `jenisRenja`, dan `kodeRenja`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target individu ditemukan", content = @Content(schema = @Schema(implementation = RenjaTargetIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaTargetIndividu> getRealisasiRenjaTargetIndividuByTahunNipJenisRenjaKodeRenjaRenjaId(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Jenis renja", example = "PROGRAM") @PathVariable JenisRenja jenisRenja,
            @Parameter(description = "Kode renja sesuai jenis renja", example = "1.1") @PathVariable String kodeRenja) {
        if (tahun == null || tahun.isBlank() || nip == null || nip.isBlank()
                || kodeRenja == null || kodeRenja.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter tahun, nip, jenisRenja, dan kodeRenja tidak boleh kosong");
        }
        return renjaTargetIndividuService.getRealisasiRenjaTargetIndividuByTahunNipJenisRenjaKodeRenja(tahun, nip, jenisRenja, kodeRenja);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja target individu (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi renja target individu.")
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
                renjaTargetIndividuRequest.kodeRenja(),
                renjaTargetIndividuRequest.jenisRenja(),
                renjaTargetIndividuRequest.nip(),
                renjaTargetIndividuRequest.kodeOpd(),
                renjaTargetIndividuRequest.idIndikator(),
                renjaTargetIndividuRequest.indikator(),
                renjaTargetIndividuRequest.targetId(),
                renjaTargetIndividuRequest.target(),
                renjaTargetIndividuRequest.realisasi(),
                renjaTargetIndividuRequest.satuan(),
                renjaTargetIndividuRequest.tahun(),
                renjaTargetIndividuRequest.bulan(),
                renjaTargetIndividuRequest.jenisRealisasi()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja target individu", description = "Menyimpan beberapa data realisasi renja target individu dalam satu request. Payload mendukung field `kodeOpd` (opsional selama masa transisi).")
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
                                    "    \"kodeRenja\": \"1.02.01\",\n" +
                                    "    \"jenisRenja\": \"PROGRAM\",\n" +
                                    "    \"nip\": \"198012312005011001\",\n" +
                                    "    \"kodeOpd\": \"1.01.0.00.0.00.01.0000\",\n" +
                                    "    \"idIndikator\": \"IND-REN-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian renja\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"1\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaTargetIndividuRequest> renjaTargetIndividuRequests) {
        return renjaTargetIndividuService.batchSubmitRealisasiRenjaTargetIndividu(renjaTargetIndividuRequests);
    }

    @DeleteMapping("/by-tahun/{tahun}/by-nip/{nip}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja}")
    @Operation(summary = "Hapus realisasi renja target individu (belum digunakan di endpoint realisasi)", description = "Menghapus data realisasi renja target individu berdasarkan `tahun`, `nip`, `jenisRenja`, dan `kodeRenja`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja target individu terhapus", content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Void> deleteRealisasiRenjaTargetIndividu(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Jenis renja", example = "PROGRAM") @PathVariable JenisRenja jenisRenja,
            @Parameter(description = "Kode renja sesuai jenis renja", example = "1.1") @PathVariable String kodeRenja) {
        if (tahun == null || tahun.isBlank() || nip == null || nip.isBlank() || kodeRenja == null || kodeRenja.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter tahun, nip, jenisRenja, dan kodeRenja tidak boleh kosong");
        }
        return renjaTargetIndividuService.deleteRealisasiRenjaTargetIndividuByFilters(tahun, nip, jenisRenja, kodeRenja);
    }
}
