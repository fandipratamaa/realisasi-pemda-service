ALTER TABLE tujuan_opd ADD COLUMN definisi_operational TEXT;
ALTER TABLE sasaran_opd ADD COLUMN definisi_operational TEXT;

UPDATE tujuan_opd
SET definisi_operational = ''
WHERE definisi_operational IS NULL;

UPDATE sasaran_opd
SET definisi_operational = ''
WHERE definisi_operational IS NULL;

ALTER TABLE tujuan_opd ALTER COLUMN definisi_operational SET NOT NULL;
ALTER TABLE sasaran_opd ALTER COLUMN definisi_operational SET NOT NULL;
