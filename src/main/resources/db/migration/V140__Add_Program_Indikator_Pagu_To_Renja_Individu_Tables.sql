ALTER TABLE realisasi_target_renja_program_individu
    ADD COLUMN program VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN indikator VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN pagu NUMERIC(20, 5) NOT NULL DEFAULT 0;

ALTER TABLE realisasi_target_renja_kegiatan_individu
    ADD COLUMN kegiatan VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN indikator VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN pagu NUMERIC(20, 5) NOT NULL DEFAULT 0;

ALTER TABLE realisasi_target_renja_subkegiatan_individu
    ADD COLUMN subkegiatan VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN indikator VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN pagu NUMERIC(20, 5) NOT NULL DEFAULT 0;
