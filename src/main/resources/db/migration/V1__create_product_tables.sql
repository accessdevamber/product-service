
-- PostgreSQL 17
-- Database: ecom_product_db
-- Schema: public

CREATE TABLE public.categories (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description TEXT,

    parent_id UUID,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_categories_slug UNIQUE (slug),

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id)
        REFERENCES public.categories(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_categories_not_self_parent
        CHECK (parent_id IS NULL OR parent_id <> id),

    CONSTRAINT chk_categories_name_not_blank
        CHECK (LENGTH(TRIM(name)) > 0)
);


CREATE TABLE public.products (
    id UUID PRIMARY KEY,

    category_id UUID NOT NULL,

    sku VARCHAR(80) NOT NULL,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL,

    description TEXT,
    brand VARCHAR(100),

    price NUMERIC(12,2) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'INR',

    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    specifications JSONB NOT NULL DEFAULT '{}'::jsonb,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT uk_products_slug UNIQUE (slug),

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
        REFERENCES public.categories(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_products_price
        CHECK (price >= 0),

    CONSTRAINT chk_products_name_not_blank
        CHECK (LENGTH(TRIM(name)) > 0),

    CONSTRAINT chk_products_sku_not_blank
        CHECK (LENGTH(TRIM(sku)) > 0),

    CONSTRAINT chk_products_status
        CHECK (status IN (
            'DRAFT',
            'ACTIVE',
            'INACTIVE',
            'DISCONTINUED'
        )),

    CONSTRAINT chk_products_specs_object
        CHECK (jsonb_typeof(specifications) = 'object')
);


-- Indexes for common catalog queries

CREATE INDEX idx_products_category_id
    ON public.products(category_id);

CREATE INDEX idx_products_status
    ON public.products(status);

CREATE INDEX idx_products_brand
    ON public.products(brand);

CREATE INDEX idx_categories_parent_id
    ON public.categories(parent_id);
