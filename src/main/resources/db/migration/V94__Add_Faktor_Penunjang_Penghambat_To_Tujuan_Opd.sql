ALTER TABLE tujuan_opd ADD COLUMN faktor_penunjang TEXT;
ALTER TABLE tujuan_opd ADD COLUMN faktor_penghambat TEXT;

UPDATE tujuan_opd SET faktor_penunjang = '' WHERE faktor_penunjang IS NULL;
UPDATE tujuan_opd SET faktor_penghambat = '' WHERE faktor_penghambat IS NULL;

ALTER TABLE tujuan_opd ALTER COLUMN faktor_penunjang SET NOT NULL;
ALTER TABLE tujuan_opd ALTER COLUMN faktor_penghambat SET NOT NULL;
