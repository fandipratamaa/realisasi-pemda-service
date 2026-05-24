CREATE TABLE pagu_renja_kegiatan_individu (
    id BIGSERIAL PRIMARY KEY,
    kegiatan_id BIGINT NOT NULL UNIQUE,
    kode_opd VARCHAR(255) NOT NULL,
    kode_pagu VARCHAR(255) NOT NULL UNIQUE,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    realisasi BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_pagu_renja_kegiatan_individu_kegiatan FOREIGN KEY (kegiatan_id) REFERENCES renja_kegiatan_individu(id) ON DELETE CASCADE
);
