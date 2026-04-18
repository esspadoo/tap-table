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
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PROMOTIONS
CREATE TABLE promotions (
                            id SERIAL PRIMARY KEY,
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
    status ORDER_STATUS NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

-- ITEMS
-- TODO: change the name (ugly)
CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ORDER_ITEMS
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    is_liked BOOLEAN, -- this field can be null as the user might not have liked or disliked the item yet (TODO: create a trigger to avoid filling this field with the order still not completed)
    UNIQUE (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

-- INGREDIENTS
CREATE TYPE ALLERGEN AS ENUM ('cereals', 'crustaceans', 'eggs', 'fish', 'peanuts', 'soybeans', 'milk', 'nuts', 'celery', 'mustard', 'sesame_seeds', 'sulphur_dioxide_and_sulphites', 'lupin', 'molluscs'); -- https://www.fellernet.it/barra2/Allergeni%204%20lingue.pdf
CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    allergen ALLERGEN[], -- this field can be null as the item might not contain any allergen,
    is_frozen BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ITEM_INGREDIENTS
CREATE TABLE item_ingredients (
    id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL,
    ingredient_id INTEGER NOT NULL,
    UNIQUE (item_id, ingredient_id),
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

-- CATEGORIES
CREATE TABLE categories(
    name VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- ITEM_CATEGORIES
CREATE TABLE item_categories (
    id SERIAL PRIMARY KEY,
    item_id INTEGER NOT NULL,
    category_id VARCHAR(255) NOT NULL,
    UNIQUE (item_id, category_id),
    FOREIGN KEY (item_id) REFERENCES items(id),
    FOREIGN KEY (category_id) REFERENCES categories(name)
);

-- REFRESH TOKENS
CREATE TABLE refresh_tokens (
    token      VARCHAR(512) PRIMARY KEY,
    user_id    INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL
);