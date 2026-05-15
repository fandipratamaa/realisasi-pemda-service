package cc.kertaskerja.realisasi_individu_service.sasaran.web;

import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividu;
import cc.kertaskerja.realisasi_individu_service.sasaran.domain.SasaranIndividuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("sasaran_individu")
@Tag(name = "Individu - Sasaran", description = "Endpoint realisasi sasaran tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class SasaranIndividuController {
    private final SasaranIndividuService sasaranIndividuService;

    public SasaranIndividuController(SasaranIndividuService sasaranIndividuService) {
        this.sasaranIndividuService = sasaranIndividuService;
    }

    @GetMapping
    @Operation(summary = "Ambil semua realisasi sasaran individu", description = "Mengambil seluruh data realisasi sasaran individu. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> getAllRealisasiSasaranIndividu() {
        return sasaranIndividuService.getAllRealisasiSasaranIndividu();
    }

    @GetMapping("/{nip}/by-tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran individu per tahun dan bulan", description = "Mengambil realisasi sasaran individu berdasarkan NIP, tahun, dan bulan. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndNip(
            @Parameter(description = "NIP", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndNip(tahun, bulan, nip);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-nip/{nip}/by-tahun/{tahun}/bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran individu per kode OPD, NIP, tahun, dan bulan", description = "Mengambil realisasi sasaran individu berdasarkan `kode_opd`, `nip`, `tahun`, dan `bulan`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpdAndNip(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "NIP", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpdAndNip(tahun, bulan, kodeOpd, nip);
    }

    @GetMapping("/by-tahun/{tahun}/by-bulan/{bulan}/by-nip/{nip}/by-id-renja/{renjaId}")
    @Operation(summary = "Cari realisasi sasaran individu per tahun, bulan, NIP, dan renja", description = "Mengambil realisasi sasaran individu berdasarkan tahun, bulan, NIP, dan `renjaId`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndNipAndRenjaId(
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan,
            @Parameter(description = "NIP", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "ID renja", example = "REN-001") @PathVariable String renjaId) {
        return sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndNipAndRenjaId(tahun, bulan, nip, renjaId);
    }

    @GetMapping("/by-kode-opd/{kodeOpd}/by-tahun/{tahun}/by-bulan/{bulan}")
    @Operation(summary = "Cari realisasi sasaran individu per tahun, bulan, dan kode OPD", description = "Mengambil realisasi sasaran individu berdasarkan tahun, bulan, dan `kode_opd`. Endpoint `GET` ini dapat diakses oleh role `super_admin` dan `admin_opd`.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi sasaran individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpd(
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi", example = "2025") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi", example = "1") @PathVariable String bulan) {
        return sasaranIndividuService.getRealisasiSasaranIndividuByTahunAndBulanAndKodeOpd(tahun, bulan, kodeOpd);
    }

    @PostMapping
    @Operation(summary = "Simpan realisasi sasaran individu", description = "Menyimpan satu data realisasi sasaran individu. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran individu tersimpan", content = @Content(schema = @Schema(implementation = SasaranIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<SasaranIndividu> submitRealisasiSasaranIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi sasaran individu", required = true,
                    content = @Content(schema = @Schema(implementation = SasaranIndividuRequest.class)))
            @RequestBody @Valid SasaranIndividuRequest sasaranIndividuRequest) {
        return sasaranIndividuService.submitRealisasiSasaranIndividu(
                sasaranIndividuRequest.renjaId(),
                sasaranIndividuRequest.indikatorId(),
                sasaranIndividuRequest.targetId(),
                sasaranIndividuRequest.target(),
                sasaranIndividuRequest.realisasi(),
                sasaranIndividuRequest.satuan(),
                sasaranIndividuRequest.tahun(),
                sasaranIndividuRequest.bulan(),
                sasaranIndividuRequest.jenisRealisasi(),
                sasaranIndividuRequest.nip(),
                sasaranIndividuRequest.namaPegawai(),
                sasaranIndividuRequest.kodeOpd(),
                sasaranIndividuRequest.rumusPerhitungan(),
                sasaranIndividuRequest.sumberData()
        );
    }

    @PostMapping("/batch")
    @Operation(summary = "Simpan batch realisasi sasaran individu", description = "Menyimpan beberapa data realisasi sasaran individu dalam satu request. Payload mendukung field `kodeOpd` (opsional selama masa transisi). Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch berhasil disimpan", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Payload batch tidak valid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<SasaranIndividu> batchSubmitRealisasiSasaranIndividu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Daftar payload realisasi sasaran individu", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SasaranIndividuRequest.class))))
            @RequestBody @Valid List<SasaranIndividuRequest> sasaranIndividuRequests) {
        return sasaranIndividuService.batchSubmitRealisasiSasaranIndividu(sasaranIndividuRequests);
    }

    @DeleteMapping("/by-sasaran-id/{sasaranId}")
    @Operation(summary = "Hapus realisasi sasaran individu berdasarkan sasaran ID", description = "Menghapus data realisasi sasaran individu berdasarkan `sasaranId`. Role `super_admin` dan `admin_opd` tidak diizinkan mengakses endpoint ini.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data realisasi sasaran individu terhapus", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden untuk role super_admin dan admin_opd", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<Void> deleteRealisasiSasaranIndividuBySasaranId(
            @Parameter(description = "Sasaran ID", example = "REN-001") @PathVariable String sasaranId) {
        return sasaranIndividuService.deleteRealisasiSasaranIndividuBySasaranId(sasaranId);
    }
}
