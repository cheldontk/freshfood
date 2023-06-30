-- public.product definition

-- Drop table

-- DROP TABLE public.product;

CREATE TABLE public.product (
	costprice numeric(38, 2) NULL,
	ispublished bool NULL,
	price numeric(38, 2) NULL,
	stock int4 NULL,
    img varchar(255) NULL,
	category_id int8 NULL,
	created_at timestamp(6) NULL,
	farm_id int8 NULL,
	id bigserial NOT NULL,
	updated_at timestamp(6) NULL,
	description varchar(255) NULL,
	"name" varchar(255) NULL,
	CONSTRAINT product_pkey UNIQUE(id),
	CONSTRAINT fk1mtsbur82frn64de7balymq9s FOREIGN KEY (category_id) REFERENCES public.category(id),
	CONSTRAINT fk61og7cmkjnqviqnp7m0wqprhh FOREIGN KEY (farm_id) REFERENCES public.farm(id)
);