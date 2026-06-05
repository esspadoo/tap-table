-- All the users have @UniPadova26 as password
INSERT INTO "public"."users" ("id", "username", "email", "name", "surname", "phone_number", "role", "password_hash") VALUES
(1, 'klamerja',   'merja@test.com',      'Klaudio',   'Merja',      '1234567890', 'ADMIN',    '$2a$10$9i5IInoeVbDddTclE6G6EOPrTK4v4NllI.T1nYY5EzzD6U0uXv7W6'),
(2, 'agarberino', 'garberino@test.com',  'Alvise',    'Garberino',  '123456789',  'STAFF',    '$2a$10$9i5IInoeVbDddTclE6G6EOPrTK4v4NllI.T1nYY5EzzD6U0uXv7W6'),
(3, 'tsanavia',   'sanavia@test.com',    'Thomas',    'Sanavia',    '12345678',   'STAFF',    '$2a$10$9i5IInoeVbDddTclE6G6EOPrTK4v4NllI.T1nYY5EzzD6U0uXv7W6'),
(4, 'gpadoan',    'padoan@test.com',     'Giancarlo', 'Padoan',     '1234567',    'CUSTOMER', '$2a$10$9i5IInoeVbDddTclE6G6EOPrTK4v4NllI.T1nYY5EzzD6U0uXv7W6'),
(5, 'fbaldan',    'fabio@test.com',      'Fabio',     'Baldan',     '123456',     'CUSTOMER', '$2a$10$9i5IInoeVbDddTclE6G6EOPrTK4v4NllI.T1nYY5EzzD6U0uXv7W6');

INSERT INTO "public"."categories" ("name") VALUES
('Antipasti'),
('Primi'),
('Secondi'),
('Contorni'),
('Dolci'),
('Bevande');

INSERT INTO "public"."ingredients" ("id", "name", "allergen", "is_frozen") VALUES
(1, 'Pomodoro San Marzano', NULL,                                     false),
(2, 'Mozzarella di Bufala', ARRAY['milk']::ALLERGEN[],                false),
(3, 'Basilico fresco',      NULL,                                     false),
(4, 'Spaghetti di Gragnano',ARRAY['cereals', 'eggs']::ALLERGEN[],     false),
(5, 'Mascarpone',           ARRAY['milk']::ALLERGEN[],                false);

INSERT INTO "public"."dishes" ("id", "name", "description", "category_name", "price") VALUES
(1, 'Bruschetta al pomodoro',  'Pane tostato con pomodoro fresco e basilico',     'Antipasti', 5.50),
(2, 'Spaghetti al pomodoro',   'Spaghetti con salsa di pomodoro fresco e basilico','Primi',    9.00),
(3, 'Pollo ai ferri',          'Petto di pollo marinato alla griglia con limone', 'Secondi',  13.00),
(4, 'Tiramisù della casa',     'Classico tiramisù al mascarpone e caffè',         'Dolci',     6.00),
(5, 'Acqua naturale 0.5L',     NULL,                                               'Bevande',  1.50);

INSERT INTO "public"."dish_ingredients" ("id", "dish_id", "ingredient_id") VALUES
(1, 1, 1),  -- bruschetta   → pomodoro
(2, 1, 3),  -- bruschetta   → basilico
(3, 2, 4),  -- spaghetti    → spaghetti di gragnano
(4, 2, 1),  -- spaghetti    → pomodoro
(5, 4, 5);  -- tiramisù     → mascarpone

INSERT INTO "public"."promotions" ("id", "code", "discount", "description", "valid_from", "valid_to") VALUES
(1, 'BENVENUTO10',  10, 'Sconto 10% per i nuovi clienti',              '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
(2, 'ESTATE20',     20, 'Sconto 20% per il periodo estivo',            '2026-06-01 00:00:00', '2026-08-31 23:59:59'),
(3, 'FEDELTA5',     5, 'Sconto 5% per i clienti fedeli',              '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
(4, 'NATALE15',     15, 'Sconto 15% per le festività natalizie',       '2026-12-01 00:00:00', '2026-12-31 23:59:59'),
(5, 'COMPLEANNO25', 25, 'Sconto 25% nel giorno del compleanno',        '2026-01-01 00:00:00', '2026-12-31 23:59:59');

-- customers: gpadoan (4), fbaldan (5)
INSERT INTO "public"."orders" ("id", "user_id", "promotion_id", "total_amount", "status") VALUES
(1, 4, 1,    11.00, 'COMPLETED'),
(2, 4, NULL,  9.00, 'PENDING'),
(3, 5, 2,    19.00, 'COMPLETED'),
(4, 5, NULL,  6.00, 'CANCELLED'),
(5, 4, NULL, 13.00, 'PENDING');

INSERT INTO "public"."order_dishes" ("id", "order_id", "dish_id", "quantity", "is_liked") VALUES
(1, 1, 1, 2, true),   -- ordine 1: bruschetta x2 = 11.00
(2, 2, 2, 1, NULL),   -- ordine 2: spaghetti x1 = 9.00
(3, 3, 3, 1, true),   -- ordine 3: pollo       = 13.00
(4, 3, 4, 1, true),   -- ordine 3: tiramisù    = 6.00
(5, 4, 4, 1, false),  -- ordine 4: tiramisù    = 6.00
(6, 5, 3, 1, NULL);   -- ordine 5: pollo       = 13.00

-- reset sequences
SELECT setval('users_id_seq',          5);
SELECT setval('ingredients_id_seq',    5);
SELECT setval('dishes_id_seq',         5);
SELECT setval('dish_ingredients_id_seq', 5);
SELECT setval('promotions_id_seq',     5);
SELECT setval('orders_id_seq',         5);
SELECT setval('order_dishes_id_seq',   6);
