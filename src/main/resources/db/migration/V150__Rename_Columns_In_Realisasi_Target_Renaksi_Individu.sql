ALTER TABLE realisasi_target_renaksi_individu
RENAME COLUMN kode_sasaran TO kode_rekin;

ALTER TABLE realisasi_target_renaksi_individu
RENAME COLUMN sasaran TO rekin;

ALTER TABLE realisasi_target_renaksi_individu
RENAME COLUMN kode_target TO kode_pelaksanaan;

ALTER TABLE realisasi_target_renaksi_individu
RENAME COLUMN target TO bobot;
