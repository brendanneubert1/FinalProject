CREATE DATABASE IF NOT EXISTS library_manager;

USE library_manager

CREATE TABLE `user` (
userId 		INT AUTO_INCREMENT NOT NULL,
username 	VARCHAR(50) NOT NULL UNIQUE,
password 	VARCHAR(255),
firstname  	VARCHAR(50),
lastname  	VARCHAR(50),
PRIMARY KEY (userId)
);

CREATE TABLE book (
isbn  		VARCHAR(20) NOT NULL,
title  		VARCHAR(255) NOT NULL,
imglink   	VARCHAR(255),
category   	VARCHAR(100),
description  	TEXT,
rating 	 	DECIMAL(2,1),
num_ratings 	INT,
num_pages 	INT,
release_date DATE,
PRIMARY KEY (isbn)
);

CREATE TABLE author (
authorId   	bigint AUTO_INCREMENT,
name 		VARCHAR(255) NOT NULL,
PRIMARY KEY (authorId)
);

CREATE TABLE review (
reviewId 		BIGINT AUTO_INCREMENT,
userId	 		INT NOT NULL,
bookId	 		VARCHAR(20) NOT NULL,
recommended 	BOOLEAN NOT NULL,
body	 		TEXT,
created_date 	DATETIME,
PRIMARY KEY (reviewId),
FOREIGN KEY (userId) REFERENCES `user`(userId)
	ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (bookId) REFERENCES book(isbn)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE written_by (
bookId 	 	VARCHAR(20) NOT NULL,
authorId  	BIGINT NOT NULL,
PRIMARY KEY (bookId, authorId),
FOREIGN KEY (bookId) REFERENCES book(isbn)
	ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (authorId) REFERENCES author(authorId)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE follows (
userId 	 	INT NOT NULL,
follows  	INT NOT NULL,
PRIMARY KEY (userId, follows),
FOREIGN KEY (userId) REFERENCES `user`(userId)
	ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (follows) REFERENCES `user`(userId)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE wishlist (
userId 	 	INT NOT NULL,
bookId 	 	VARCHAR(20),
PRIMARY KEY (userId, bookId),
FOREIGN KEY (userId) REFERENCES `user`(userId)
	ON DELETE CASCADE,
FOREIGN KEY (bookId) REFERENCES book(isbn)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE likes (
userId		INT NOT NULL, 
bookId	 	VARCHAR(20) NOT NULL, 
PRIMARY KEY (userId, bookId), 
FOREIGN KEY (userId) REFERENCES `user`(userId), 
FOREIGN KEY (bookId) REFERENCES book(isbn) 
);

CREATE TABLE `read` (
userId INT NOT NULL,
bookId VARCHAR(20) NOT NULL,
date_read DATETIME NOT NULL DEFAULT NOW(),
PRIMARY KEY(userId, bookId),
FOREIGN KEY (userId) REFERENCES `user`(userId) 
	ON DELETE CASCADE,
FOREIGN KEY (bookId) REFERENCES book(isbn) 
	ON DELETE CASCADE
);

CREATE TABLE rating (
userId INT NOT NULL,
bookId VARCHAR(20) NOT NULL,
rating DECIMAL(2,1) NOT NULL,
PRIMARY KEY (userId, bookId),
FOREIGN KEY (userId) REFERENCES `user`(userId)
 ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (bookId) REFERENCES book(isbn)
 ON DELETE CASCADE ON UPDATE CASCADE);

CREATE INDEX idx_book_title ON book (title);
CREATE INDEX idx_book_category ON book (category);
