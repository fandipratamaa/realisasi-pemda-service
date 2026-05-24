package cc.kertaskerja.realisasi_opd_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_program_opd")
public record RenjaProgramOpdHeader(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        @Column("kode_program")
        String kodeProgram,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {
    public static RenjaProgramOpdHeader of(String kodeOpd, String kodeProgram, String tahun, String bulan) {
        return new RenjaProgramOpdHeader(null, kodeOpd, kodeProgram, tahun, bulan,
                null, null, null, null);
    }
}
