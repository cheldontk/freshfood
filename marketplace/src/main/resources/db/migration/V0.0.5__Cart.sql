-- public.cart definition

-- Drop table

-- DROP TABLE public.cart;

CREATE TABLE public.cart
(
    id             bigserial    NOT NULL UNIQUE,
    customer_id    varchar(255) NOT NULL,
    customer_name  varchar(255) NOT NULL,
    customer_email varchar(255) NOT NULL,
    payment_method varchar(255) NULL,
    payment_status varchar(255) NULL,
    payment_token  varchar(255) NULL,
    created_at     timestamp(6) NULL,
    CONSTRAINT cart_pkey UNIQUE (id)
);

-- public.cart_product definition

-- Drop table

-- DROP TABLE public.cart_product;

CREATE TABLE public.cart_product
(
    id         bigserial      NOT NULL,
    cart_id    int8           NULL,
    product_id int8           NULL,
    farm_id    int8           NULL,
    quantity   int4           NULL,
    price      numeric(38, 2) NULL,
    created_at timestamp(6)   NULL,
    CONSTRAINT cart_product_pkey UNIQUE (id),
    CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES public.product (id),
    CONSTRAINT fk_farm_id FOREIGN KEY (farm_id) REFERENCES public.farm (id),
    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES public.cart (id) ON DELETE CASCADE
);