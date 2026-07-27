-- Drop unused columns from realisasi_target_renja_program_individu
ALTER TABLE realisasi_target_renja_program_individu DROP COLUMN IF EXISTS target;
ALTER TABLE realisasi_target_renja_program_individu DROP COLUMN IF EXISTS program;
ALTER TABLE realisasi_target_renja_program_individu DROP COLUMN IF EXISTS indikator;
ALTER TABLE realisasi_target_renja_program_individu DROP COLUMN IF EXISTS pagu;

-- Drop unused columns from realisasi_target_renja_kegiatan_individu
ALTER TABLE realisasi_target_renja_kegiatan_individu DROP COLUMN IF EXISTS target;
ALTER TABLE realisasi_target_renja_kegiatan_individu DROP COLUMN IF EXISTS kegiatan;
ALTER TABLE realisasi_target_renja_kegiatan_individu DROP COLUMN IF EXISTS indikator;
ALTER TABLE realisasi_target_renja_kegiatan_individu DROP COLUMN IF EXISTS pagu;

-- Drop unused columns from realisasi_target_renja_subkegiatan_individu
ALTER TABLE realisasi_target_renja_subkegiatan_individu DROP COLUMN IF EXISTS target_realisasi;
ALTER TABLE realisasi_target_renja_subkegiatan_individu DROP COLUMN IF EXISTS subkegiatan;
ALTER TABLE realisasi_target_renja_subkegiatan_individu DROP COLUMN IF EXISTS indikator;
ALTER TABLE realisasi_target_renja_subkegiatan_individu DROP COLUMN IF EXISTS pagu;
