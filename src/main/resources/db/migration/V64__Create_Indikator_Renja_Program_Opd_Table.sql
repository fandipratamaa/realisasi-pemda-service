CREATE TABLE indikator_renja_program_opd (
    id BIGSERIAL PRIMARY KEY,
    renja_program_opd_id BIGINT NOT NULL UNIQUE,
    kode_indikator VARCHAR(255) NOT NULL UNIQUE,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_indikator_renja_program_opd
    FOREIGN KEY (renja_program_opd_id) REFERENCES renja_program_opd(id) ON DELETE CASCADE
);
