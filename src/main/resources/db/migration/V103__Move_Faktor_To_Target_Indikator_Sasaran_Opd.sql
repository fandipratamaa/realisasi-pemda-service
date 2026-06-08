ALTER TABLE target_indikator_sasaran_opd
    ADD COLUMN faktor_penunjang TEXT NOT NULL DEFAULT '',
    ADD COLUMN faktor_penghambat TEXT NOT NULL DEFAULT '';

UPDATE target_indikator_sasaran_opd t
SET faktor_penunjang = s.faktor_penunjang,
    faktor_penghambat = s.faktor_penghambat
FROM indikator_sasaran_opd i
         JOIN sasaran_opd s ON s.id = i.sasaran_opd_id
WHERE i.id = t.indikator_sasaran_id;

ALTER TABLE sasaran_opd DROP COLUMN faktor_penunjang;
ALTER TABLE sasaran_opd DROP COLUMN faktor_penghambat;
