CREATE TABLE renja_pagu
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    renja_id           varchar(255)          NOT NULL,
    renja              varchar(255)          NOT NULL,
    jenis_renja        varchar(255)          NOT NULL,
    pagu               integer               NOT NULL,
    realisasi          integer               NOT NULL,
    satuan             varchar(255)          NOT NULL,
    tahun              varchar(10)           NOT NULL,
    jenis_realisasi    varchar(255)          NOT NULL,
    kode_opd           varchar(255)          NOT NULL,
    status             varchar(255)          NOT NULL,
    created_by         varchar(255)          NOT NULL,
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    version            integer               NOT NULL
)