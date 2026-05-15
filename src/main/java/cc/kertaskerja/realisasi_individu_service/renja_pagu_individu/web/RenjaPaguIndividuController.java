package cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.web;

import java.util.List;

import cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain.RenjaPaguIndividu;
import cc.kertaskerja.realisasi_individu_service.renja_pagu_individu.domain.RenjaPaguIndividuService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renja_pagu_individu")
@Tag(name = "Individu - Renja Pagu", description = "Endpoint realisasi renja pagu tingkat individu. Role `super_admin`, `admin_opd`, dan `level_1` hanya diizinkan mengakses endpoint `GET` pada resource ini, role `level_2` dan `level_3` dapat mengakses seluruh endpoint pada resource ini, sedangkan role `level_4` tidak diizinkan mengakses resource ini.")
public class RenjaPaguIndividuController {
    private final RenjaPaguIndividuService renjaPaguIndividuService;

    public RenjaPaguIndividuController(RenjaPaguIndividuService renjaPaguIndividuService) {
        this.renjaPaguIndividuService = renjaPaguIndividuService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi renja pagu individu", description = "Mengambil seluruh data realisasi renja pagu individu. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> getAllRealisasiRenjaPaguIndividu() {
        return renjaPaguIndividuService.getAllRealisasiRenjaPaguIndividu();
    }

    @GetMapping("/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja pagu individu berdasarkan NIP, tahun, dan bulan", description = "Mengambil daftar realisasi renja pagu individu berdasarkan NIP, tahun, dan bulan. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulan(nip, tahun, bulan);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja pagu individu berdasarkan tahun, bulan, dan kode OPD", description = "Mengambil realisasi renja pagu individu berdasarkan tahun, bulan, dan `kode_opd`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByTahunAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi renja pagu individu berdasarkan kode OPD, NIP, tahun, dan bulan", description = "Mengambil realisasi renja pagu individu berdasarkan `kode_opd`, `nip`, `tahun`, dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByNipAndTahunAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByNipAndTahunAndBulanAndKodeOpd(nip, tahun, bulan, kodeOpd);
    }

    @GetMapping("/by-tahun/{tahun}/by-nip/{nip}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja}")
    @Operation(summary = "Cari realisasi renja pagu individu berdasarkan filter lengkap", description = "Mengambil daftar realisasi renja pagu individu berdasarkan tahun, NIP, jenis renja, dan kode renja. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renja pagu individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> getRealisasiRenjaPaguIndividuByFilters(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Jenis renja", example = "PROGRAM") @PathVariable JenisRenja jenisRenja,
            @Parameter(description = "Kode renja", example = "1.02.01") @PathVariable String kodeRenja) {
        return renjaPaguIndividuService.getRealisasiRenjaPaguIndividuByFilters(tahun, nip, jenisRenja, kodeRenja);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi renja pagu individu (belum digunakan di endpoint realisasi)", description = "Menyimpan satu data realisasi renja pagu individu. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi renja pagu individu tersimpan", content = @Content(schema = @Schema(implementation = RenjaPaguIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenjaPaguIndividu> submitRealisasiRenjaPaguIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renja pagu individu", required = true,
                    content = @Content(schema = @Schema(implementation = RenjaPaguIndividuRequest.class)))
            @RequestBody @Valid RenjaPaguIndividuRequest renjaPaguIndividuRequest) {
        return renjaPaguIndividuService.submitRealisasiRenjaPaguIndividu(
                renjaPaguIndividuRequest.kodeRenja(),
                renjaPaguIndividuRequest.jenisRenja(),
                renjaPaguIndividuRequest.nip(),
                renjaPaguIndividuRequest.namaPegawai(),
                renjaPaguIndividuRequest.kodeOpd(),
                renjaPaguIndividuRequest.idIndikator(),
                renjaPaguIndividuRequest.indikator(),
                renjaPaguIndividuRequest.pagu(),
                renjaPaguIndividuRequest.realisasi(),
                renjaPaguIndividuRequest.satuan(),
                renjaPaguIndividuRequest.tahun(),
                renjaPaguIndividuRequest.bulan(),
                renjaPaguIndividuRequest.jenisRealisasi()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi renja pagu individu", description = "Menyimpan beberapa data realisasi renja pagu individu dalam satu request. Payload mendukung field `kodeOpd` (opsional selama masa transisi). Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<RenjaPaguIndividu> batchSubmitRealisasiRenjaPaguIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi renja pagu individu", required = true,
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RenjaPaguIndividuRequest.class)),
                            examples = @ExampleObject(name = "ArrayRequest", value = "[\n" +
                                    "  {\n" +
                                    "    \"targetRealisasiId\": 10,\n" +
                                    "    \"kodeRenja\": \"1.02.01\",\n" +
                                    "    \"jenisRenja\": \"PROGRAM\",\n" +
                                     "    \"nip\": \"198012312005011001\",\n" +
                                     "    \"namaPegawai\": \"Budi Santoso\",\n" +
                                     "    \"kodeOpd\": \"1.01.0.00.0.00.01.0000\",\n" +
                                    "    \"idIndikator\": \"IND-REN-123\",\n" +
                                    "    \"indikator\": \"Persentase capaian renja\",\n" +
                                    "    \"pagu\": 100000000,\n" +
                                    "    \"realisasi\": 70000000,\n" +
                                    "    \"satuan\": \"Rp\",\n" +
                                    "    \"tahun\": \"2026\",\n" +
                                    "    \"bulan\": \"1\",\n" +
                                    "    \"jenisRealisasi\": \"NAIK\"\n" +
                                    "  }\n" +
                                    "]")))
            @RequestBody @Valid List<RenjaPaguIndividuRequest> renjaPaguIndividuRequests) {
        return renjaPaguIndividuService.batchSubmitRealisasiRenjaPaguIndividu(renjaPaguIndividuRequests);
    }

    @DeleteMapping("/by-tahun/{tahun}/by-nip/{nip}/by-jenis-renja/{jenisRenja}/by-kode-renja/{kodeRenja} (belum digunakan di endpoint realisasi)")
    @Operation(summary = "Hapus realisasi renja pagu individu berdasarkan filter", description = "Menghapus semua data realisasi renja pagu individu berdasarkan tahun, NIP, jenis renja, dan kode renja. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data berhasil dihapus", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Void> deleteRealisasiRenjaPaguIndividuByFilters(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Jenis renja", example = "PROGRAM") @PathVariable JenisRenja jenisRenja,
            @Parameter(description = "Kode renja", example = "1.02.01") @PathVariable String kodeRenja) {
        return renjaPaguIndividuService.deleteRealisasiRenjaPaguIndividuByFilters(tahun, nip, jenisRenja, kodeRenja);
    }
}
