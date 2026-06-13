package cc.kertaskerja.realisasi_individu_service.renaksi.domain;

import java.util.Collections;
import java.util.List;

import cc.kertaskerja.realisasi_individu_service.renaksi.domain.indikator.IndikatorRenaksiIndividu;
import cc.kertaskerja.realisasi_individu_service.renaksi.domain.target.TargetIndikatorRenaksiIndividu;

public record SasaranWithDetails(
        SasaranIndividu sasaran,
        List<RenaksiIndividu> renaksis,
        List<IndikatorRenaksiIndividu> indikators,
        List<TargetIndikatorRenaksiIndividu> targets
) {
    public SasaranWithDetails(SasaranIndividu sasaran, List<RenaksiIndividu> renaksis, List<IndikatorRenaksiIndividu> indikators) {
        this(sasaran, renaksis, indikators, Collections.emptyList());
    }
}
