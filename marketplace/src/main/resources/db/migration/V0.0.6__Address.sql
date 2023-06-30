-- public.cart_address definition

-- Drop table

-- DROP TABLE public.cart_address;

CREATE TABLE public.cart_address
(
    id          bigserial    NOT NULL,
    cart_id     int8         NULL,
    postal_code varchar(255) NULL,
    city        varchar(255) NULL,
    district    varchar(255) NULL,
    street      varchar(255) NULL,
    number      varchar(255) NULL,
    description varchar(255) NULL,
    latitude    float8       NULL,
    longitude   float8       NULL,
    created_at  timestamp(6) NULL,
    CONSTRAINT cart_address_pkey UNIQUE (id),
    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES public.cart (id) ON DELETE CASCADE
);