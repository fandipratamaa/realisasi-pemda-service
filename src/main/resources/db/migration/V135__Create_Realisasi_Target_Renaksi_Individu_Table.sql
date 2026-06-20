CREATE TABLE realisasi_target_renaksi_individu (
    id BIGSERIAL PRIMARY KEY,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    kode_sasaran VARCHAR(255) NOT NULL,
    kode_renaksi VARCHAR(255) NOT NULL,
    kode_indikator VARCHAR(255) NOT NULL,
    kode_target VARCHAR(255) NOT NULL,
    pagu_anggaran NUMERIC(20, 2) NOT NULL,
    realisasi NUMERIC(20, 2) NOT NULL,
    jenis_realisasi VARCHAR(255) NOT NULL,
    faktor_penunjang VARCHAR(255) NOT NULL,
    faktor_penghambat VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    last_modified_by VARCHAR(100) NOT NULL
);
