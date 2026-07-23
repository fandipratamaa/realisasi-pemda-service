ALTER TABLE realisasi_target_sasaran_pemda RENAME COLUMN sasaran_id TO kode_sasaran_pemda;
ALTER TABLE realisasi_target_sasaran_pemda DROP COLUMN sasaran;
ALTER TABLE realisasi_target_sasaran_pemda RENAME COLUMN indikator_id TO kode_indikator;
ALTER TABLE realisasi_target_sasaran_pemda DROP COLUMN indikator;
ALTER TABLE realisasi_target_sasaran_pemda RENAME COLUMN target_id TO kode_target;
ALTER TABLE realisasi_target_sasaran_pemda DROP COLUMN target;
ALTER TABLE realisasi_target_sasaran_pemda DROP COLUMN rumus_perhitungan;
ALTER TABLE realisasi_target_sasaran_pemda DROP COLUMN sumber_data;
