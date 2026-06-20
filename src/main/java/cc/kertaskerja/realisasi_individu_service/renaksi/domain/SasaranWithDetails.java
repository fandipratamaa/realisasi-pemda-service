package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import java.util.List;

public record SasaranWithDetails(
        SasaranIndividu sasaran,
        List<RenaksiIndividu> realisasiTargets
) {
}
