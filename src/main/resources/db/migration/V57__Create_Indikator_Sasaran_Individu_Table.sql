CREATE TABLE indikator_sasaran_individu (
    id BIGSERIAL PRIMARY KEY,
    sasaran_individu_id BIGINT,
    kode_indikator VARCHAR(255) NOT NULL,
    kode_opd VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_indikator_sasaran_individu_tujuan
    FOREIGN KEY (sasaran_individu_id) REFERENCES sasaran_individu(id) ON DELETE CASCADE
);
