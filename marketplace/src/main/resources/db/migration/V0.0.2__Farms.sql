-- public.farm definition

-- Drop table

-- DROP TABLE public.farm;

CREATE TABLE public.farm (
	created_at timestamp(6) NULL,
	id bigserial NOT NULL,
	location_id int8 NULL,
	updated_at timestamp(6) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT farm_location_id_key UNIQUE(location_id),
	CONSTRAINT farm_pkey UNIQUE(id),
	CONSTRAINT fkgwrxkhgx8o5l2r6cy3ipvd9mj FOREIGN KEY (location_id) REFERENCES public."location"(id)
);