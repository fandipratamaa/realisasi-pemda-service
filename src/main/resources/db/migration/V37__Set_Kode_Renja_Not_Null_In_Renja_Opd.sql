UPDATE renja_target
SET kode_renja = ''
WHERE kode_renja IS NULL;

UPDATE renja_pagu
SET kode_renja = ''
WHERE kode_renja IS NULL;

ALTER TABLE renja_target
ALTER COLUMN kode_renja SET NOT NULL;

ALTER TABLE renja_pagu
ALTER COLUMN kode_renja SET NOT NULL;
