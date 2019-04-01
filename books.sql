SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE IF NOT EXISTS books (
	id int(10) NOT NULL,
	author VARCHAR(20) NOT NULL,
	title VARCHAR(20) NOT NULL,
	year int(10) NOT NULL,
	status VARCHAR(10) NOT NULL,
	PRIMARY KEY(id)
	);
	
INSERT INTO 'books' ('id', 'author', 'title', 'year', 'status') VALUES
(1, 'Miguel de Cervantes', 'Don Quixote', 1605, 'free'),
(2, 'Charles Dickens', 'A Tale of Two Cities', 1859, 'free'),
(3, 'J. R. R. Tolkien', 'The Lord of the Rings', 1954, 'free'),
(4, 'Antonie de Saint-Exupery', 'The Little Prince', 1997, 'free'),
(5, 'J. K. Rowling', 'Harry Potter and the Philosopher\'s stone', 1997, 'free'),
(6, 'J. R. R. Tolkien', 'The Hobbit', 1937, 'free'),
(7, 'Agatha Christie', 'And Then There Were None', 1939, 'free'),
(8, 'Cao Xueqin', 'Dream of the Red Chamber', 1791, 'free'),
(9, 'C. S. Lewis', 'The Lion, the Witch and the Wardrobe', 1950, 'free')
(10, 'H. Rider Haggard', 'She: A History of Adventure', 1887, 'free');