package cc.kertaskerja.realisasi_individu_service.rekin.web;

import cc.kertaskerja.realisasi_individu_service.rekin.domain.Rekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.RekinService;
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
@RequestMapping("rekin")
@Tag(name = "Individu - Rekin", description = "Endpoint realisasi rekin tingkat individu")
public class RekinController {
    private final RekinService rekinService;

    public RekinController(RekinService rekinService) {
        this.rekinService = rekinService;
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}")
    @Operation(summary = "Cari realisasi rekin berdasarkan NIP dan tahun (belum digunakan di endpoint realisasi)", description = "Mengambil daftar data realisasi rekin berdasarkan `nip` dan `tahun`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi rekin", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rekin.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Rekin> getRealisasiRekinByNipAndTahun(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun) {
        if (nip == null || nip.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip dan tahun tidak boleh kosong");
        }
        return rekinService.getRealisasiRekinByNipAndTahun(nip, tahun);
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi rekin berdasarkan NIP, tahun, dan bulan", description = "Mengambil daftar data realisasi rekin berdasarkan `nip`, `tahun`, dan `bulan`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi rekin", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rekin.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Rekin> getRealisasiRekinByNipAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        if (nip == null || nip.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, tahun, dan bulan tidak boleh kosong");
        }
        return rekinService.getRealisasiRekinByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    @GetMapping("/by-tahun/{tahun}/by-nip/{nip}/by-id-sasaran/{idSasaran}/rekin/{rekinId}")
    @Operation(summary = "Cari realisasi rekin berdasarkan tahun, NIP, ID sasaran, dan rekin", description = "Mengambil satu data realisasi rekin berdasarkan `tahun`, `nip`, `idSasaran`, dan `rekinId`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi rekin ditemukan", content = @Content(schema = @Schema(implementation = Rekin.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Rekin> getRealisasiRekinByTahunNipIdSasaranRekinId(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "ID sasaran", example = "SAS-001") @PathVariable String idSasaran,
            @Parameter(description = "ID rekin", example = "REKIN-001") @PathVariable String rekinId) {
        if (tahun == null || tahun.isBlank()
                || nip == null || nip.isBlank()
                || idSasaran == null || idSasaran.isBlank()
                || rekinId == null || rekinId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter tahun, nip, idSasaran, dan rekinId tidak boleh kosong");
        }
        return rekinService.getRealisasiRekinByNipIdSasaranTahunRekinId(nip, idSasaran, tahun, rekinId);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi rekin (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi rekin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi rekin tersimpan", content = @Content(schema = @Schema(implementation = Rekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Rekin> submitRealisasiRekin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi rekin", required = true,
                    content = @Content(schema = @Schema(implementation = RekinRequest.class)))
            @RequestBody @Valid RekinRequest rekinRequest) {
        return rekinService.submitRealisasiRekin(
                rekinRequest.rekinId(),
                rekinRequest.rekin(),
                rekinRequest.indikatorId(),
                rekinRequest.indikator(),
                rekinRequest.nip(),
                rekinRequest.idSasaran(),
                rekinRequest.sasaran(),
                rekinRequest.targetId(),
                rekinRequest.target(),
                rekinRequest.realisasi(),
                rekinRequest.satuan(),
                rekinRequest.tahun(),
                rekinRequest.bulan(),
                rekinRequest.jenisRealisasi()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hapus realisasi rekin (belum digunakan di endpoint realisasi)", description = "Menghapus satu data realisasi rekin berdasarkan ID internal.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi rekin terhapus", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<Void> deleteRealisasiRekin(
            @Parameter(description = "ID internal realisasi rekin", example = "1") @PathVariable Long id) {
        return rekinService.deleteRealisasiRekin(id);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi rekin", description = "Menyimpan beberapa data realisasi rekin dalam satu request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rekin.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Rekin> batchSubmitRealisasiRekin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi rekin", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RekinRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": \"1\",\n" +
                                    "    \"rekinId\": \"REKIN-001\",\n" +
                                    "    \"rekin\": \"Rekin Peningkatan Infrastruktur\",\n" +
                                    "    \"indikatorId\": \"IND-REK-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian rekin\",\n" +
                                    "    \"nip\": \"198012312005011001\",\n" +
                                    "    \"idSasaran\": \"SAS-001\",\n" +
                                    "    \"sasaran\": \"Meningkatkan kualitas layanan\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"01\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RekinRequest> rekinRequests) {
        return rekinService.batchSubmitRealisasiRekin(rekinRequests);
    }
}
