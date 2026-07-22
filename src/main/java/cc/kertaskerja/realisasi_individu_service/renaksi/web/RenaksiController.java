package cc.kertaskerja.realisasi_individu_service.renaksi.web;

import cc.kertaskerja.realisasi.domain.JenisLaporan;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.RenaksiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("renaksi_individu")
@Tag(name = "Individu - Renaksi", description = "Endpoint realisasi renaksi tingkat individu. Role `super_admin` dan `admin_opd` hanya diizinkan mengakses endpoint `GET` pada resource ini, sedangkan role `level_1`, `level_2`, `level_3`, dan `level_4` dapat mengakses seluruh endpoint pada resource ini.")
public class RenaksiController {
    private final RenaksiService renaksiService;

    public RenaksiController(RenaksiService renaksiService) {
        this.renaksiService = renaksiService;
    }

    @GetMapping("/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/penetapan")
    @Operation(summary = "Integrasi penetapan dengan realisasi renaksi individu", description = "Menggabungkan data penetapan (dari external service) dengan data realisasi renaksi berdasarkan NIP, kode OPD, tahun, dan bulan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse> getPenetapanByNipAndTahunAndBulan(
            @Parameter(description = "NIP pelaksana") @PathVariable String nip,
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renaksiService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan);
    }

    @PostMapping("/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/sync/penetapan")
    @Operation(summary = "Sinkronisasi renaksi individu", description = "Memicu sinkronisasi data renaksi individu dari service penetapan dan langsung mengembalikan data terbarunya.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data penetapan ter-sinkronisasi dan terintegrasi dengan realisasi", content = @Content(schema = @Schema(implementation = cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<cc.kertaskerja.realisasi_individu_service.rekin.web.PenetapanRekinIndividuResponse> syncRenaksiIndividu(
            @Parameter(description = "NIP pelaksana", example = "198012312005011001") @PathVariable String nip,
            @Parameter(description = "Kode OPD", example = "1.01.0.00.0.00.01.0000") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun", example = "2026") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi (opsional)", example = "1") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renaksiService.syncPenetapanRenaksiIndividu(nip, kodeOpd, Integer.parseInt(tahun))
                .then(renaksiService.getPenetapanByNip(nip, kodeOpd, Integer.parseInt(tahun), bulan));
    }

    @GetMapping("/kodeOpd/{kodeOpd}/tahun/{tahun}/bulan/{bulan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Mencari realisasi renaksi individu", description = "Endpoint untuk fitur pencarian realisasi renaksi individu di frontend. Memvalidasi NIP ke service pegawai lalu mengambil data realisasi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar realisasi renaksi individu", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RenaksiIndividu.class)))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pegawai tidak ditemukan", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<RenaksiIndividu> searchRealisasi(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun realisasi") @PathVariable String tahun,
            @Parameter(description = "Bulan realisasi") @PathVariable String bulan,
            @Parameter(description = "Level Role (LEVEL_1, dll)") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank() || bulan == null || bulan.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd, tahun, dan bulan tidak boleh kosong");
        }
        return renaksiService.searchRealisasi(kodeOpd, tahun, bulan, levelRole, nip);
    }

    @GetMapping("/laporan/nip/{nip}/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}")
    @Operation(summary = "Laporan realisasi renaksi individu per periode", description = "Mengambil total realisasi renaksi individu yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renaksi individu", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenaksiIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasi(
            @Parameter(description = "NIP pelaksana") @PathVariable String nip,
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        if (nip == null || nip.isBlank() || kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter nip, kodeOpd, dan tahun tidak boleh kosong");
        }
        return renaksiService.getLaporanRealisasi(nip, kodeOpd, tahun, jenisLaporan, bulan);
    }

    @GetMapping("/laporan/kodeOpd/{kodeOpd}/tahun/{tahun}/jenisLaporan/{jenisLaporan}/levelRole/{levelRole}/nip/{nip}")
    @Operation(summary = "Laporan realisasi renaksi individu per periode (OPD)", description = "Mengambil total realisasi renaksi individu yang dikelompokkan berdasarkan periode (BULANAN, TRIWULAN, TAHUNAN) untuk NIP tertentu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data laporan realisasi renaksi individu", content = @Content(schema = @Schema(implementation = LaporanRealisasiRenaksiIndividuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parameter tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PreAuthorize("hasAnyAuthority('super_admin', 'ROLE_SUPER_ADMIN', 'admin_opd', 'ROLE_ADMIN_OPD')")
    public Flux<LaporanRealisasiRenaksiIndividuResponse> getLaporanRealisasiByOpd(
            @Parameter(description = "Kode OPD") @PathVariable String kodeOpd,
            @Parameter(description = "Tahun laporan") @PathVariable String tahun,
            @Parameter(description = "Jenis periode laporan") @PathVariable JenisLaporan jenisLaporan,
            @Parameter(description = "Level Role") @PathVariable String levelRole,
            @Parameter(description = "NIP Pegawai") @PathVariable String nip,
            @Parameter(description = "Nomor bulan (1-12), wajib jika BULANAN") @RequestParam(required = false) String bulan) {
        if (kodeOpd == null || kodeOpd.isBlank() || tahun == null || tahun.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter kodeOpd dan tahun tidak boleh kosong");
        }
        return renaksiService.getLaporanRealisasiByOpd(kodeOpd, tahun, jenisLaporan, bulan, levelRole, nip);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Simpan realisasi target renaksi individu (upsert)", description = "Menyimpan realisasi target renaksi individu. Jika data dengan composite key yang sama sudah ada, akan diperbarui.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Realisasi tersimpan", content = @Content(schema = @Schema(implementation = RenaksiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public Mono<RenaksiResponse> submitRealisasi(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload realisasi renaksi individu", required = true,
                    content = @Content(schema = @Schema(implementation = RenaksiIndividuRequest.class)))
            @RequestBody @Valid RenaksiIndividuRequest request) {
        return renaksiService.submitRealisasiTarget(request);
    }

    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file bukti pendukung", description = "Mengunggah file dan mengembalikan string URL.")
    public Mono<java.util.Map<String, String>> uploadFile(
            @Parameter(description = "File yang akan diupload", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") FilePart file) {
        return renaksiService.uploadFile(file)
                .map(url -> java.util.Map.of("url", url));
    }

    @PostMapping("/faktor-penunjang")
    @Operation(summary = "Perbarui faktor penunjang renaksi", description = "Memperbarui hanya field faktor_penunjang pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiIndividu> updateFaktorPenunjang(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penunjang renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenunjangRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenunjangRenaksiRequest request) {
        return renaksiService.updateFaktorPenunjang(request);
    }

    @PostMapping("/faktor-penghambat")
    @Operation(summary = "Perbarui faktor penghambat renaksi", description = "Memperbarui hanya field faktor_penghambat pada target indikator renaksi yang cocok dengan composite key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Berhasil diperbarui", content = @Content(schema = @Schema(implementation = RenaksiIndividu.class))),
            @ApiResponse(responseCode = "400", description = "Payload tidak valid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data tidak ditemukan", content = @Content)
    })
    public Mono<RenaksiIndividu> updateFaktorPenghambat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload faktor penghambat renaksi", required = true,
                    content = @Content(schema = @Schema(implementation = FaktorPenghambatRenaksiRequest.class)))
            @RequestBody @Valid FaktorPenghambatRenaksiRequest request) {
        return renaksiService.updateFaktorPenghambat(request);
    }


}
