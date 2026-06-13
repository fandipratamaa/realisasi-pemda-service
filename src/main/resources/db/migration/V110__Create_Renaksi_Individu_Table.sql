CREATE TABLE renaksi_individu
(
    id                 BIGSERIAL    PRIMARY KEY NOT NULL,
    sasaran_id         BIGINT       NOT NULL,
    kode_opd           varchar(255) NOT NULL,
    nip                varchar(255) NOT NULL,
    kode_renaksi       varchar(255) NOT NULL,
    renaksi            varchar(255) NOT NULL,
    tahun              varchar(255) NOT NULL,
    bulan              varchar(255) NOT NULL,
    status             varchar(50)  NOT NULL DEFAULT 'UNCHECKED',
    created_date       timestamp    NOT NULL,
    last_modified_date timestamp    NOT NULL,
    created_by         varchar(100) NULL,
    last_modified_by   varchar(100) NULL,
    version            integer      NOT NULL,
    CONSTRAINT fk_renaksi_individu_sasaran_individu
        FOREIGN KEY (sasaran_id) REFERENCES sasaran_individu(id)
        ON DELETE CASCADE
);
