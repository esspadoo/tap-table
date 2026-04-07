-- SOME SAMPLE DATA TO TEST
-- USERS
INSERT INTO users (username, email, phone_number, role, password_hash) VALUES
('alice', 'alice@example.com', '+1234567890', 'CUSTOMER', 'hash1'),
('bob', 'bob@example.com', '+1234567891', 'CUSTOMER', 'hash2'),
('carol', 'carol@example.com', '+1234567892', 'STAFF', 'hash3');

-- CATEGORIES
INSERT INTO categories (name) VALUES
('Beverages'),
('Snacks'),
('Desserts'),
('Sandwiches'),
('Salads');

-- ITEMS
INSERT INTO items (name, description, price) VALUES
('Chocolate Cake', 'Rich chocolate cake', 5.50),
('Coca Cola', 'Soft drink 330ml', 1.50),
('Potato Chips', 'Salted potato chips', 2.00),
('Green Tea', 'Organic green tea', 2.50),
('Ham Sandwich', 'Classic ham sandwich with lettuce and tomato', 4.50),
('Veggie Sandwich', 'Healthy sandwich with mixed vegetables', 4.00),
('Caesar Salad', 'Fresh salad with parmesan and croutons', 5.00),
('Iced Coffee', 'Cold coffee with milk and ice', 3.00);

-- ITEM_CATEGORIES
INSERT INTO item_categories (item_id, category_id) VALUES
(1, 3),  -- Chocolate Cake -> Desserts
(2, 1),  -- Coca Cola -> Beverages
(3, 2),  -- Potato Chips -> Snacks
(4, 1), -- Green Tea -> Beverages
(5, 4),  -- Ham Sandwich -> Sandwiches
(6, 4),  -- Veggie Sandwich -> Sandwiches
(7, 5),  -- Caesar Salad -> Salads
(8, 1);  -- Iced Coffee -> Beverages

-- INGREDIENTS
INSERT INTO ingredients (name, allergen) VALUES
('Flour', '{"cereals"}'),
('Sugar', NULL),
('Cocoa', NULL),
('Milk', '{"milk"}'),
('Potatoes', NULL),
('Tea Leaves', NULL),
('Carbonated Water', NULL),
('Salt', NULL),
('Ham', NULL),
('Lettuce', NULL),
('Tomato', NULL),
('Bread', '{"cereals"}'),
('Cheese', '{"milk"}'),
('Parmesan', '{"milk"}'),
('Croutons', '{"cereals"}'),
('Coffee', NULL),
('Ice', NULL),
('Mixed Veggies', NULL);

-- ITEM_INGREDIENTS
-- Chocolate Cake
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(1, 1), -- Flour
(1, 2), -- Sugar
(1, 3), -- Cocoa
(1, 4); -- Milk

-- Coca Cola
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(2, 7), -- Carbonated Water
(2, 2); -- Sugar

-- Potato Chips
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(3, 5), -- Potatoes
(3, 8); -- Salt

-- Green Tea
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(4, 6); -- Tea Leaves

-- Ham Sandwich
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(5, 1), -- Ham
(5, 2), -- Lettuce
(5, 3), -- Tomato
(5, 4), -- Bread
(5, 5); -- Cheese

-- Veggie Sandwich
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(6, 2), -- Lettuce
(6, 3), -- Tomato
(6, 4), -- Bread
(6, 10), -- Mixed Veggies
(6, 5); -- Cheese

-- Caesar Salad
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(7, 2), -- Lettuce
(7, 5), -- Cheese
(7, 6), -- Parmesan
(7, 7); -- Croutons

-- Iced Coffee
INSERT INTO item_ingredients (item_id, ingredient_id) VALUES
(8, 8), -- Coffee
(8, 5), -- Milk (optional)
(8, 9); -- Ice


-- ORDERS
INSERT INTO orders (user_id, total_amount, status, created_at) VALUES
(1, 8.00, 'COMPLETED', '2026-03-28 12:30:00'), -- Alice
(2, 3.50, 'PENDING', '2026-03-29 15:45:00'), -- Bob
(3, 9.50, 'COMPLETED', '2026-03-28 18:00:00'), -- Carol
(2, 8.50, 'PENDING', '2026-03-29 14:00:00'),   -- Dave
(1, 7.50, 'COMPLETED', '2026-03-30 10:00:00'); -- Alice


-- ORDER_ITEMS
    -- Alice's Order
INSERT INTO order_items (order_id, item_id, quantity, is_liked) VALUES
(1, 1, 1, TRUE),  -- Alice ordered 1 Chocolate Cake
(1, 2, 1, TRUE);  -- Alice ordered 1 Coca Cola

    -- Bob's Order
INSERT INTO order_items (order_id, item_id, quantity, is_liked) VALUES
(2, 3, 2, NULL);  -- Bob ordered 2 Potato Chips (not yet liked)

    -- Carol's order
INSERT INTO order_items (order_id, item_id, quantity, is_liked) VALUES
(3, 5, 1, TRUE), -- Ham Sandwich
(3, 8, 1, TRUE); -- Iced Coffee

    -- Dave's order
INSERT INTO order_items (order_id, item_id, quantity, is_liked) VALUES
(4, 6, 1, NULL), -- Veggie Sandwich
(4, 7, 1, NULL); -- Caesar Salad

    -- Alice's second order
INSERT INTO order_items (order_id, item_id, quantity, is_liked) VALUES
(5, 2, 2, TRUE), -- Coca Cola
(5, 3, 1, TRUE); -- Potato Chips



-- PROMOTIONS
INSERT INTO promotions (code, valid_from, valid_to) VALUES
('SPRING10', '2026-03-01 00:00:00', '2026-04-30 23:59:59'),
('WELCOME5', '2026-03-01 00:00:00', '2026-06-30 23:59:59');