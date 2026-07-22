-- Remove redundant columns from realisasi_target_renaksi_individu
ALTER TABLE realisasi_target_renaksi_individu DROP COLUMN IF EXISTS rekin_id;
ALTER TABLE realisasi_target_renaksi_individu DROP COLUMN IF EXISTS rekin;
ALTER TABLE realisasi_target_renaksi_individu DROP COLUMN IF EXISTS renaksi;
ALTER TABLE realisasi_target_renaksi_individu DROP COLUMN IF EXISTS pagu_anggaran;
