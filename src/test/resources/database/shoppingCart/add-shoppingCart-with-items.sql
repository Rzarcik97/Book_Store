INSERT INTO books (title, author, isbn, price, description, cover_image, is_deleted) VALUES
        ('The Pragmatic Programmer', 'Andrew Hunt', '9780201616224', 42.99, 'Classic book on software craftsmanship.', 'https://example.com/images/pragmatic.jpg', 0),
        ('Clean Code', 'Robert C. Martin', '9780132350884', 37.55, 'A handbook of agile software craftsmanship.', 'https://example.com/images/cleancode.jpg', 0),
        ('Effective Java', 'Joshua Bloch', '9780134685991', 45.33, 'Best practices for the Java platform.', 'https://example.com/images/effectivejava.jpg', 0);
INSERT INTO users (email, password, first_Name, last_Name, shipping_Address, is_deleted) VALUES
        ('User@email.com','Password','User','Pass','City',0);
INSERT INTO shopping_carts (user_id) VALUES (1);
INSERT INTO cart_items (shopping_Cart_id, book_id, quantity) VALUES
        (1,1,3),
        (1,2,5);
