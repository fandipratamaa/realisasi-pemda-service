CREATE TABLE realisasi_target_renja_subkegiatan_individu (
    id BIGSERIAL PRIMARY KEY,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    kode_subkegiatan VARCHAR(255) NOT NULL,
    kode_indikator VARCHAR(255) NOT NULL,
    kode_target VARCHAR(255) NOT NULL,
    kode_pagu VARCHAR(255) NOT NULL,
    realisasi NUMERIC(20, 5) NOT NULL,
    jenis_realisasi VARCHAR(255) NOT NULL,
    faktor_penunjang VARCHAR(255) NOT NULL,
    faktor_penghambat VARCHAR(255) NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    last_modified_by VARCHAR(100) NOT NULL,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP
);
