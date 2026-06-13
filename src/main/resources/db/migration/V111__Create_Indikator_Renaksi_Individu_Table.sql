CREATE TABLE indikator_renaksi_individu (
    id BIGSERIAL PRIMARY KEY,
    renaksi_id BIGINT NOT NULL,
    kode_indikator VARCHAR(255) NOT NULL,
    indikator VARCHAR(255) NOT NULL,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    version INTEGER NOT NULL,
    CONSTRAINT fk_indikator_renaksi_individu_renaksi_individu
        FOREIGN KEY (renaksi_id) REFERENCES renaksi_individu(id)
        ON DELETE CASCADE
);
