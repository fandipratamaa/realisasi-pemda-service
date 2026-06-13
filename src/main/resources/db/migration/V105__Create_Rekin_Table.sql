CREATE TABLE rekin (
    id BIGSERIAL PRIMARY KEY,
    kode_opd VARCHAR(255) NOT NULL,
    nip VARCHAR(255) NOT NULL,
    kode_rekin VARCHAR(255) NOT NULL,
    rekin VARCHAR(255) NOT NULL,
    tahun VARCHAR(255) NOT NULL,
    bulan VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UNCHECKED',
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    last_modified_by VARCHAR(100)
);
