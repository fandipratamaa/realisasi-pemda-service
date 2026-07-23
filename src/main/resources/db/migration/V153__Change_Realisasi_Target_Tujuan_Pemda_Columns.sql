ALTER TABLE realisasi_target_tujuan_pemda RENAME COLUMN tujuan_id TO kode_tujuan_pemda;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN tujuan;
ALTER TABLE realisasi_target_tujuan_pemda RENAME COLUMN indikator_id TO kode_indikator;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN indikator;
ALTER TABLE realisasi_target_tujuan_pemda RENAME COLUMN target_id TO kode_target;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN target;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN visi_misi;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN rumus_perhitungan;
ALTER TABLE realisasi_target_tujuan_pemda DROP COLUMN sumber_data;
