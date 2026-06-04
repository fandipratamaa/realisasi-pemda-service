ALTER TABLE tujuans ADD COLUMN faktor_penunjang TEXT;
ALTER TABLE tujuans ADD COLUMN faktor_penghambat TEXT;

UPDATE tujuans SET faktor_penunjang = '' WHERE faktor_penunjang IS NULL;
UPDATE tujuans SET faktor_penghambat = '' WHERE faktor_penghambat IS NULL;

ALTER TABLE tujuans ALTER COLUMN faktor_penunjang SET NOT NULL;
ALTER TABLE tujuans ALTER COLUMN faktor_penghambat SET NOT NULL;
