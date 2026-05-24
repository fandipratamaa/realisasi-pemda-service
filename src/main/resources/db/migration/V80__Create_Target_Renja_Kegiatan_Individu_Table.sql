CREATE TABLE target_renja_kegiatan_individu (
    id BIGSERIAL PRIMARY KEY,
    indikator_renja_kegiatan_individu_id BIGINT NOT NULL UNIQUE,
    kode_target VARCHAR(255) NOT NULL UNIQUE,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    realisasi NUMERIC(20, 2),
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_target_indikator_renja_kegiatan_individu 
    FOREIGN KEY (indikator_renja_kegiatan_individu_id) REFERENCES indikator_renja_kegiatan_individu(id) ON DELETE CASCADE
);
