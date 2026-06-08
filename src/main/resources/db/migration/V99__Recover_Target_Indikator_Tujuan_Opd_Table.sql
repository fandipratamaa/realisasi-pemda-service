CREATE TABLE IF NOT EXISTS target_indikator_tujuan_opd (
    id BIGSERIAL PRIMARY KEY,
    indikator_tujuan_id BIGINT,
    kode_target VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    realisasi NUMERIC(20, 2),
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT fk_target_indikator_tujuan_indikator 
    FOREIGN KEY (indikator_tujuan_id) REFERENCES indikator_tujuan_opd(id) ON DELETE CASCADE
);
