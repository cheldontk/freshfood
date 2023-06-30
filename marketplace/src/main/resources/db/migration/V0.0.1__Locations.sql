-- public."location" definition

-- Drop table

-- DROP TABLE public."location";

CREATE TABLE public."location" (
	latitude float8 NULL,
	longitude float8 NULL,
	id bigserial NOT NULL,
	CONSTRAINT location_pkey UNIQUE(id)
);