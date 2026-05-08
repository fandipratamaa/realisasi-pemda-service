ALTER TABLE tujuans ADD COLUMN visi_misi TEXT;
ALTER TABLE tujuans ADD COLUMN rumus_perhitungan TEXT;

UPDATE tujuans
SET visi_misi = ''
WHERE visi_misi IS NULL;

UPDATE tujuans
SET rumus_perhitungan = ''
WHERE rumus_perhitungan IS NULL;

ALTER TABLE tujuans ALTER COLUMN visi_misi SET NOT NULL;
ALTER TABLE tujuans ALTER COLUMN rumus_perhitungan SET NOT NULL;
