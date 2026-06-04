package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.Renaksi;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import cc.kertaskerja.realisasi_opd_service.renaksi.domain.RenaksiOpdService;
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
@RequestMapping("renaksi")
@Tag(name = "Individu - Renaksi", description = "Endpoint realisasi renaksi tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class RenaksiController {
    private final RenaksiService renaksiService;

    public RenaksiController(RenaksiService renaksiService, RenaksiOpdService renaksiOpdService) {
        this.renaksiService = renaksiService;
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renaksi berdasarkan NIP, tahun dan bulan", description = "Mengambil daftar data realisasi renaksi berdasarkan `nip`, `tahun` dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> getRealisasiRenaksiByNipTahunBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realizations", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realizations", example = "Januari") @PathVariable String bulan) {
        if (nip == null || nip.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getRealisasiRenaksiByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renaksi berdasarkan kode OPD, tahun dan bulan", description = "Mengambil daftar data realisasi renaksi berdasarkan `kodeOpd`, `tahun` dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> getRealisasiRenaksiByKodeOpdTahunBulan(
            @Parameter(description = "Kode OPD", example = "4.01.01.") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "Januari") @PathVariable String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.getRealisasiRenaksiByKodeOpdAndTahunAndBulan(kodeOpd, tahun, bulan);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renaksi (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi renaksi. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renaksi tersimpan", content = @Content(schema = @Schema(implementation = Renaksi.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Renaksi> submitRealisasiRenaksi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = RenaksiRequest.class)))
            @RequestBody @Valid RenaksiRequest renaksiRequest) {
        return renaksiService.submitRealisasiRenaksi(renaksiRequest);
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang renaksi", description = "Memperbarui hanya field faktor_penunjang pada record Renaksi yang cocok dengan composite key (nip, tahun, bulan, rekinId, renaksiId, targetId).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Renaksi.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Renaksi tidak ditemukan", content = @Content)
    })
    public Mono<Renaksi> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penunjang", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenunjangRenaksiRequest req) {
        return renaksiService.updateFaktorPenunjang(
                req.nip(),
                req.tahun(),
                req.bulan(),
                req.rekinId(),
                req.renaksiId(),
                req.targetId(),
                req.faktorPenunjang()
        );
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat renaksi", description = "Memperbarui hanya field faktor_penghambat pada record Renaksi yang cocok dengan composite key (nip, tahun, bulan, rekinId, renaksiId, targetId).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = Renaksi.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Renaksi tidak ditemukan", content = @Content)
    })
    public Mono<Renaksi> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload parsial faktor penghambat", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenghambatRenaksiRequest req) {
        return renaksiService.updateFaktorPenghambat(
                req.nip(),
                req.tahun(),
                req.bulan(),
                req.rekinId(),
                req.renaksiId(),
                req.targetId(),
                req.faktorPenghambat()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renaksi", description = "Menyimpan beberapa data realisasi renaksi dalam satu request. Payload wajib menyertakan field `kodeOpd`. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Renaksi.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<Renaksi> batchSubmitRealisasiRenaksi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renaksi", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenaksiRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": \"1\",\n" +
                                    "    \"renaksiId\": \"RENAKSI-001\",\n" +
                                    "    \"renaksi\": \"Renaksi Peningkatan Infrastruktur\",\n" +
                                     "    \"nip\": \"198012312005011001\",\n" +
                                     "    \"namaPegawai\": \"Budi Santoso\",\n" +
                                     "    \"rekinId\": \"REKIN-001\",\n" +
                                    "    \"rekin\": \"Rekin Peningkatan Infrastruktur\",\n" +
                                    "    \"targetId\": \"TAR-1\",\n" +
                                    "    \"target\": \"100\",\n" +
                                    "    \"realisasi\": 85,\n" +
                                    "    \"satuan\": \"%\",\n" +
                                    "    \"bulan\": \"Januari\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"kodeOpd\": \"1.01.0.00.0.00.01.0000\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenaksiRequest> renaksiRequests) {
        return renaksiService.batchSubmitRealisasiRenaksi(renaksiRequests);
    }
}
