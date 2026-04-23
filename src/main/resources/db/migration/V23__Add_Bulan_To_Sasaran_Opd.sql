ALTER TABLE sasaran_opd ADD COLUMN bulan VARCHAR(255);
UPDATE sasaran_opd SET bulan = '' WHERE bulan IS NULL;
ALTER TABLE sasaran_opd ALTER COLUMN bulan SET NOT NULL;