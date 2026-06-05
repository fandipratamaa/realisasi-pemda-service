ALTER TABLE sasaran_opd ADD COLUMN faktor_penunjang TEXT;
ALTER TABLE sasaran_opd ADD COLUMN faktor_penghambat TEXT;

UPDATE sasaran_opd SET faktor_penunjang = '' WHERE faktor_penunjang IS NULL;
UPDATE sasaran_opd SET faktor_penghambat = '' WHERE faktor_penghambat IS NULL;

ALTER TABLE sasaran_opd ALTER COLUMN faktor_penunjang SET NOT NULL;
ALTER TABLE sasaran_opd ALTER COLUMN faktor_penghambat SET NOT NULL;
