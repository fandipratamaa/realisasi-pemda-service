ALTER TABLE target_indikator_tujuan_opd
    ADD COLUMN faktor_penunjang TEXT NOT NULL DEFAULT '',
    ADD COLUMN faktor_penghambat TEXT NOT NULL DEFAULT '';

UPDATE target_indikator_tujuan_opd t
SET faktor_penunjang = tu.faktor_penunjang,
    faktor_penghambat = tu.faktor_penghambat
FROM indikator_tujuan_opd i
         JOIN tujuan_opd tu ON tu.id = i.tujuan_opd_id
WHERE i.id = t.indikator_tujuan_id;

ALTER TABLE tujuan_opd DROP COLUMN faktor_penunjang;
ALTER TABLE tujuan_opd DROP COLUMN faktor_penghambat;
