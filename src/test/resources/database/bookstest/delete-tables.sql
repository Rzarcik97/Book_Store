DELETE from books_categories;
DELETE from categories;
DELETE from books;
ALTER TABLE books AUTO_INCREMENT = 1;
ALTER TABLE categories AUTO_INCREMENT = 1;
ALTER TABLE books_categories AUTO_INCREMENT = 1;
