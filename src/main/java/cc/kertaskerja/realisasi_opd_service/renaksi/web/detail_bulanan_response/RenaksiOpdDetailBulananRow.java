package cc.kertaskerja.realisasi_opd_service.renaksi.web.detail_bulanan_response;

public record RenaksiOpdDetailBulananRow(
        String renaksiId,
        String targetId,
        String bulan,
        Integer realisasi
) {
}
