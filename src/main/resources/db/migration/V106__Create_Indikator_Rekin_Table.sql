CREATE TABLE indikator_rekin (
    id BIGSERIAL PRIMARY KEY,
    rekin_id BIGINT NOT NULL,
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
    CONSTRAINT fk_indikator_rekin_rekin
        FOREIGN KEY (rekin_id) REFERENCES rekin(id)
        ON DELETE CASCADE
);
