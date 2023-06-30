-- public.category definition

-- Drop table

-- DROP TABLE public.category;

CREATE TABLE public.category (
	created_at timestamp(6) NULL,
	farm_id int8 NULL,
	id bigserial NOT NULL,
	updated_at timestamp(6) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT category_pkey UNIQUE(id),
	CONSTRAINT fkfoteom0q78ghnc9g951woli8p FOREIGN KEY (farm_id) REFERENCES public.farm(id)
);