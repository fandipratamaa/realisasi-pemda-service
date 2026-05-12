CREATE TABLE sasaran_individu
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    renja_id           varchar(255)          NOT NULL,
    renja              varchar(255)          NOT NULL,
    indikator_id       varchar(255)          NOT NULL,
    indikator          varchar(255)          NOT NULL,
    target_id          varchar(255)          NOT NULL UNIQUE,
    target             text                  NOT NULL,
    realisasi          double precision      NOT NULL,
    satuan             varchar(255)          NOT NULL,
    tahun              varchar(10)           NOT NULL,
    bulan              varchar(255)          NOT NULL,
    jenis_realisasi    varchar(255)          NOT NULL,
    nip                varchar(255)          NOT NULL,
    rumus_perhitungan  varchar(255)          NOT NULL,
    sumber_data        varchar(255)          NOT NULL,
    status             varchar(255)          NOT NULL,
    created_by         varchar(255),
    last_modified_by   varchar(255),
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    version            integer               NOT NULL
)
