ALTER TABLE realisasi_target_tujuan_pemda ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_sasaran_pemda ADD COLUMN bukti_pendukung VARCHAR(255);

ALTER TABLE realisasi_target_tujuan_opd ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_sasaran_opd ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_program_opd ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_kegiatan_opd ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_subkegiatan_opd ADD COLUMN bukti_pendukung VARCHAR(255);

ALTER TABLE realisasi_target_rekin_individu ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_program_individu ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_kegiatan_individu ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renja_subkegiatan_individu ADD COLUMN bukti_pendukung VARCHAR(255);
ALTER TABLE realisasi_target_renaksi_individu ADD COLUMN bukti_pendukung VARCHAR(255);