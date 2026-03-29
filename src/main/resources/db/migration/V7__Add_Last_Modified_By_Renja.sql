ALTER TABLE renja_pagu
    ADD COLUMN last_modified_by varchar(255);

ALTER TABLE renja_target
    ADD COLUMN last_modified_by varchar(255);
