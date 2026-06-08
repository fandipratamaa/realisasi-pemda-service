ALTER TABLE target_renja_program_opd ADD COLUMN kode_opd VARCHAR(255);
ALTER TABLE target_renja_program_opd ADD COLUMN kode_program VARCHAR(255);

ALTER TABLE target_renja_kegiatan_opd ADD COLUMN kode_opd VARCHAR(255);
ALTER TABLE target_renja_kegiatan_opd ADD COLUMN kode_kegiatan VARCHAR(255);

ALTER TABLE target_renja_subkegiatan_opd ADD COLUMN kode_opd VARCHAR(255);
ALTER TABLE target_renja_subkegiatan_opd ADD COLUMN kode_subkegiatan VARCHAR(255);
