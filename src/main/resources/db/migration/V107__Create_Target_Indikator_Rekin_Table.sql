CREATE TABLE target_indikator_rekin (
    id BIGSERIAL PRIMARY KEY,
    indikator_rekin_id BIGINT NOT NULL,
    kode_target VARCHAR(255) NOT NULL,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    target NUMERIC(20,2) NOT NULL,
    realisasi NUMERIC(20,2),
    jenis_realisasi VARCHAR(255) NOT NULL,
    faktor_penunjang TEXT,
    faktor_penghambat TEXT,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_target_indikator_rekin_indikator
        FOREIGN KEY (indikator_rekin_id) REFERENCES indikator_rekin(id)
        ON DELETE CASCADE
);
