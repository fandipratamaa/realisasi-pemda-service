CREATE TABLE target_indikator_renaksi_individu (
    id BIGSERIAL PRIMARY KEY,
    indikator_renaksi_id BIGINT NOT NULL,
    kode_target VARCHAR(255) NOT NULL,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    pagu_anggaran NUMERIC(20,2) NOT NULL,
    target NUMERIC(20,2) NOT NULL,
    realisasi NUMERIC(20,2),
    jenis_realisasi VARCHAR(255) NOT NULL,
    faktor_penunjang TEXT,
    faktor_penghambat TEXT,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_target_indikator_renaksi_individu_indikator
        FOREIGN KEY (indikator_renaksi_id) REFERENCES indikator_renaksi_individu(id)
        ON DELETE CASCADE
);
