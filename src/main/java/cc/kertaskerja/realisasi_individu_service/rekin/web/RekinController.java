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
@Tag(name = "Individu - Rekin", description = "Endpoint realisasi rekin tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class RekinController {
    private final RekinService rekinService;

    public RekinController(RekinService rekinService) {
        this.rekinService = rekinService;
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi rekin berdasarkan NIP, tahun, dan bulan", description = "Mengambil daftar data realisasi rekin berdasarkan `nip`, `tahun`, dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
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

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi rekin berdasarkan kode OPD, tahun, dan bulan", description = "Mengambil daftar data realisasi rekin berdasarkan `kode_opd`, `tahun`, dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi rekin", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rekin.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Rekin> getRealisasiRekinByKodeOpdAndTahunAndBulan(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "01") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return rekinService.getRealisasiRekinByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi rekin (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi rekin. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi rekin tersimpan", content = @Content(schema = @Schema(implementation = Rekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Rekin> submitRealisasiRekin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi rekin", required = true,
                    content = @Content(schema = @Schema(implementation = RekinRequest.class)))
            @RequestBody @Valid RekinRequest rekinRequest) {
        return rekinService.submitRealisasiRekin(rekinRequest);
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi rekin", description = "Menyimpan beberapa data realisasi rekin dalam satu request. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Rekin.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
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
                                     "    \"namaPegawai\": \"Budi Santoso\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"01\",\n" +
                                    "    \"kodeOpd\": \"1.01.0.00.0.00.01.0000\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RekinRequest> rekinRequests) {
        return rekinService.batchSubmitRealisasiRekin(rekinRequests);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang rekin", description = "Memperbarui hanya field faktor_penunjang pada record Rekin yang cocok dengan composite key (nip, tahun, bulan, rekinId, targetId).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Rekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rekin tidak ditemukan", content = @Content)
    })
    public Mono<Rekin> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRekinRequest.class)))
            @RequestBody @Valid FaktorPenunjangRekinRequest req) {
        return rekinService.updateFaktorPenunjang(
                req.nip(),
                req.tahun(),
                req.bulan(),
                req.rekinId(),
                req.targetId(),
                req.faktorPenunjang()
        );
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat rekin", description = "Memperbarui hanya field faktor_penghambat pada record Rekin yang cocok dengan composite key (nip, tahun, bulan, rekinId, targetId).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Rekin.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rekin tidak ditemukan", content = @Content)
    })
    public Mono<Rekin> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRekinRequest.class)))
            @RequestBody @Valid FaktorPenghambatRekinRequest req) {
        return rekinService.updateFaktorPenghambat(
                req.nip(),
                req.tahun(),
                req.bulan(),
                req.rekinId(),
                req.targetId(),
                req.faktorPenghambat()
        );
    }
}
