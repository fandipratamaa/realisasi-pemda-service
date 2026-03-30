CREATE TABLE rekin
(
    id                 BIGSERIAL PRIMARY KEY NOT NULL,
    rekin_id           varchar(255)          NOT NULL,
    rekin              varchar(255)          NOT NULL,
    nip                varchar(255)          NOT NULL,
    id_sasaran         varchar(255)          NOT NULL,
    sasaran            varchar(255)          NOT NULL,
    target_id          varchar(255)          NOT NULL UNIQUE,
    target             text                  NOT NULL,
    realisasi          integer               NOT NULL,
    satuan             varchar(255)          NOT NULL,
    tahun              varchar(10)           NOT NULL,
    jenis_realisasi    varchar(255)          NOT NULL,
    status             varchar(255)          NOT NULL,
    keterangan_capaian varchar(255)          NULL,
    created_by         varchar(255)          NOT NULL,
    last_modified_by   varchar(255)          NOT NULL,
    created_date       timestamp             NOT NULL,
    last_modified_date timestamp             NOT NULL,
    version            integer               NOT NULL
)