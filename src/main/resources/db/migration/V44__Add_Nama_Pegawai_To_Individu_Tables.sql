ALTER TABLE rekin ADD COLUMN nama_pegawai VARCHAR(255);
UPDATE rekin SET nama_pegawai = '' WHERE nama_pegawai IS NULL;
ALTER TABLE rekin ALTER COLUMN nama_pegawai SET NOT NULL;

ALTER TABLE sasaran_individu ADD COLUMN nama_pegawai VARCHAR(255);
UPDATE sasaran_individu SET nama_pegawai = '' WHERE nama_pegawai IS NULL;
ALTER TABLE sasaran_individu ALTER COLUMN nama_pegawai SET NOT NULL;

ALTER TABLE renja_target_individu ADD COLUMN nama_pegawai VARCHAR(255);
UPDATE renja_target_individu SET nama_pegawai = '' WHERE nama_pegawai IS NULL;
ALTER TABLE renja_target_individu ALTER COLUMN nama_pegawai SET NOT NULL;

ALTER TABLE renja_pagu_individu ADD COLUMN nama_pegawai VARCHAR(255);
UPDATE renja_pagu_individu SET nama_pegawai = '' WHERE nama_pegawai IS NULL;
ALTER TABLE renja_pagu_individu ALTER COLUMN nama_pegawai SET NOT NULL;

ALTER TABLE renaksi ADD COLUMN nama_pegawai VARCHAR(255);
UPDATE renaksi SET nama_pegawai = '' WHERE nama_pegawai IS NULL;
ALTER TABLE renaksi ALTER COLUMN nama_pegawai SET NOT NULL;
