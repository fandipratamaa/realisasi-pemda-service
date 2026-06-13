package cc.kertaskerja.realisasi_individu_service.rekin.domain;

import java.util.Collections;
import java.util.List;

import cc.kertaskerja.realisasi_individu_service.rekin.domain.indikator.IndikatorRekin;
import cc.kertaskerja.realisasi_individu_service.rekin.domain.target.TargetIndikatorRekin;

public record RekinWithDetails(
        Rekin rekin,
        List<IndikatorRekin> indikators,
        List<TargetIndikatorRekin> targets
) {
    public RekinWithDetails(Rekin rekin, List<IndikatorRekin> indikators) {
        this(rekin, indikators, Collections.emptyList());
    }
}
