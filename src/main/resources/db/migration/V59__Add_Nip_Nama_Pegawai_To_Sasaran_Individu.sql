ALTER TABLE sasaran_individu ADD COLUMN nip VARCHAR(255);
ALTER TABLE sasaran_individu ADD COLUMN nama_pegawai VARCHAR(255);

UPDATE sasaran_individu SET nip = '' WHERE nip IS NULL;
UPDATE sasaran_individu SET nama_pegawai = '' WHERE nama_pegawai IS NULL;

ALTER TABLE sasaran_individu ALTER COLUMN nip SET NOT NULL;
ALTER TABLE sasaran_individu ALTER COLUMN nama_pegawai SET NOT NULL;
