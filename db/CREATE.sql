-- WEBAPP USER
CREATE ROLE webapp
WITH LOGIN
PASSWORD 'password'
NOSUPERUSER
NOCREATEDB
NOCREATEROLE
NOINHERIT
-- CONNECTION LIMIT 5 TODO: understand how many concurrent connections we need and set the limit accordingly to HikariCP configuration (maximumPoolSize = 10 default)
;

-- USERS
CREATE TYPE USER_ROLE AS ENUM ('STAFF', 'CUSTOMER', 'ADMIN');
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    phone_number VARCHAR(25) NOT NULL UNIQUE,
    role USER_ROLE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- PROMOTIONS
CREATE TABLE promotions (
    id SERIAL PRIMARY KEY, -- TODO: remove id from promotions (maybe best idea)
    code VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL, -- TODO: capire se fare enum
    description VARCHAR(20) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL,
    CHECK (valid_to > valid_from) -- TODO: check if timestamp comparison works
);

-- ORDERS
CREATE TYPE ORDER_STATUS AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED');
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    promotion_id INTEGER,
    total_amount DECIMAL(10, 2) NOT NULL,
    status ORDER_STATUS NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE SET NULL,
    UNIQUE (user_id, promotion_id)
);

-- CATEGORIES
CREATE TABLE categories(
    name VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY
);

-- DISHES
CREATE TABLE dishes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (category_name) REFERENCES categories(name)
);

-- ORDER_DISHES
CREATE TABLE order_dishes (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    dish_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    is_liked BOOLEAN,
    UNIQUE (order_id, dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

-- INGREDIENTS
CREATE TYPE ALLERGEN AS ENUM ('cereals', 'crustaceans', 'eggs', 'fish', 'peanuts', 'soybeans', 'milk', 'nuts', 'celery', 'mustard', 'sesame_seeds', 'sulphur_dioxide_and_sulphites', 'lupin', 'molluscs'); -- https://www.fellernet.it/barra2/Allergeni%204%20lingue.pdf
CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    allergen ALLERGEN[], -- this field can be null as the dish might not contain any allergen,
    is_frozen BOOLEAN
);

-- DISH_INGREDIENTS
CREATE TABLE dish_ingredients (
    id SERIAL PRIMARY KEY,
    dish_id INTEGER NOT NULL,
    ingredient_id INTEGER NOT NULL,
    UNIQUE (dish_id, ingredient_id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);

-- TRIGGERS
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER orders_set_updated_at
BEFORE UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION set_updated_at();