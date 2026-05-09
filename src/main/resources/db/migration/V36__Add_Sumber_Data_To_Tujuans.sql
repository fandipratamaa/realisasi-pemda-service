ALTER TABLE tujuans ADD COLUMN sumber_data VARCHAR(255);

UPDATE tujuans
SET sumber_data = ''
WHERE sumber_data IS NULL;

ALTER TABLE tujuans ALTER COLUMN sumber_data SET NOT NULL;
