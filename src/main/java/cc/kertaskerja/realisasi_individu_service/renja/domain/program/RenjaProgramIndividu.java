package cc.kertaskerja.realisasi_individu_service.renja.domain.program;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("renja_program_individu")
public record RenjaProgramIndividu(
        @Id Long id,

        @Column("kode_opd")
        String kodeOpd,

        @Column("kode_program")
        String kodeProgram,

        String nip,

        @Column("nama_pegawai")
        String namaPegawai,

        String tahun,
        String bulan,

        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @CreatedBy String createdBy,
        @LastModifiedBy String lastModifiedBy
) {}
