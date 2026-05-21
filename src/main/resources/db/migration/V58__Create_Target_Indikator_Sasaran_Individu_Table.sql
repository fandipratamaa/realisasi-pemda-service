CREATE TABLE target_indikator_sasaran_individu (
    id BIGSERIAL PRIMARY KEY,
    indikator_sasaran_id BIGINT,
    kode_target VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    realisasi NUMERIC(20, 2),
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_target_indikator_sasaran_indikator 
    FOREIGN KEY (indikator_sasaran_id) REFERENCES indikator_sasaran_individu(id) ON DELETE CASCADE
);
