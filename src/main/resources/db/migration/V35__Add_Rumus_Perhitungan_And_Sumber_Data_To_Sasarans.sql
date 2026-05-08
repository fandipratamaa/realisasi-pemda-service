ALTER TABLE sasarans ADD COLUMN rumus_perhitungan TEXT;
ALTER TABLE sasarans ADD COLUMN sumber_data TEXT;

UPDATE sasarans
SET rumus_perhitungan = ''
WHERE rumus_perhitungan IS NULL;

UPDATE sasarans
SET sumber_data = ''
WHERE sumber_data IS NULL;

ALTER TABLE sasarans ALTER COLUMN rumus_perhitungan SET NOT NULL;
ALTER TABLE sasarans ALTER COLUMN sumber_data SET NOT NULL;
