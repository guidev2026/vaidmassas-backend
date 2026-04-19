CREATE TABLE category (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ingredient (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    quantity   NUMERIC(10,3) NOT NULL DEFAULT 0,
    unit       VARCHAR(20) NOT NULL,
    min_stock  NUMERIC(10,3) NOT NULL DEFAULT 0,
    active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE product (
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    price       NUMERIC(10,2) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE product_ingredient (
    id            BIGSERIAL PRIMARY KEY,
    product_id    BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity      NUMERIC(10,3) NOT NULL,
    unit          VARCHAR(20) NOT NULL,
    CONSTRAINT fk_pi_product    FOREIGN KEY (product_id)    REFERENCES product(id),
    CONSTRAINT fk_pi_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

CREATE TABLE sale (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL,
    quantity    INTEGER NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL,
    total_price NUMERIC(10,2) NOT NULL,
    sale_date   DATE NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES product(id)
);