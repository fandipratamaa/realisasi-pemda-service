CREATE TABLE sasaran_individu (
    id BIGSERIAL PRIMARY KEY,
    kode_opd VARCHAR(255) NOT NULL,
    kode_sasaran_opd VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100)
);
