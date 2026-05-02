package cc.kertaskerja.realisasi_opd_service.renaksi.web.detail_bulanan_response;

import java.util.List;

public record RenaksiOpdDetailBulananResponse(
        String nip,
        String kodeOpd,
        String tahun,
        List<RenaksiOpdDetailBulananRow> data
) {
}
