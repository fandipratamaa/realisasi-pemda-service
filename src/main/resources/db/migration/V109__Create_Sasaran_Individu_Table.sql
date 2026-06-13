CREATE TABLE sasaran_individu
(
    id                 BIGSERIAL    PRIMARY KEY NOT NULL,
    kode_opd           varchar(255) NOT NULL,
    nip                varchar(255) NOT NULL,
    kode_sasaran       varchar(255) NOT NULL,
    sasaran            varchar(255) NOT NULL,
    tahun              varchar(255) NOT NULL,
    bulan              varchar(255) NOT NULL,
    status             varchar(50)  NOT NULL DEFAULT 'UNCHECKED',
    created_date       timestamp    NOT NULL,
    last_modified_date timestamp    NOT NULL,
    created_by         varchar(100) NULL,
    last_modified_by   varchar(100) NULL,
    version            integer      NOT NULL
);
