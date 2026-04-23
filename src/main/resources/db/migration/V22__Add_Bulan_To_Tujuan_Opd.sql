ALTER TABLE tujuan_opd ADD COLUMN bulan VARCHAR(255);
UPDATE tujuan_opd SET bulan = '' WHERE bulan IS NULL;
ALTER TABLE tujuan_opd ALTER COLUMN bulan SET NOT NULL;