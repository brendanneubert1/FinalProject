------USER------
INSERT INTO `user` (username, password, firstname, lastname)
VALUES
('alice123', 'pass1', 'Alice', 'Johnson'),
('bob456', 'pass2', 'Bob', 'Smith'),
('carol789', 'pass3', 'John', 'Doe');


-----BOOKS-------
INSERT INTO book (isbn, title, imglink, category, description, rating, num_ratings, num_pages, release_date)
VALUES
('9780002005883', 'Gilead', 'http://books.google.com/books/content?id=KQZCPgAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api', 'Fiction', "A NOVEL THAT READERS and critics have been eagerly anticipating for over a decade, Gilead is an astonishingly imagined story of remarkable lives. John Ames is a preacher, the son of a preacher and the grandson (both maternal and paternal) of preachers. It’s 1956 in Gilead, Iowa, towards the end of the Reverend Ames’s life, and he is absorbed in recording his family’s story, a legacy for the young son he will never see grow up. Haunted by his grandfather’s presence, John tells of the rift between his grandfather and his father: the elder, an angry visionary who fought for the abolitionist cause, and his son, an ardent pacifist. He is troubled, too, by his prodigal namesake, Jack (John Ames) Boughton, his best friend’s lost son who returns to Gilead searching for forgiveness and redemption. Told in John Ames’s joyous, rambling voice that finds beauty, humour and truth in the smallest of life’s details, Gilead is a song of celebration and acceptance of the best and the worst the world has to offer. At its heart is a tale of the sacred bonds between fathers and sons, pitch-perfect in style and story, set to dazzle critics and readers alike.", 3.85, 361, 247, '2004-28-10'),

---AUTHORS-----
INSERT INTO author (name)
VALUES
('Marilynne Robinson'),
('Elizabeth Lowell');

-----WRITTEN_BY-----
INSERT INTO written_by (bookId, authorId)
VALUES
('9780002005883', 1), 
('9780060511135', 2), 

-----REVIEW------
INSERT INTO review (userId, bookId, recommended, body, created_date)
VALUES
(1, '9780002005883', TRUE, 'Amazing story, very engaging!', NOW()),
(2, '9780002261982', TRUE, 'Thought-provoking and emotional.', NOW()),
(3, '9780006163831', FALSE, 'Too long for my taste, but imaginative.', NOW());


-----------FOLLOWS--------
INSERT INTO follows (userId, follows)
VALUES
(1, 2), 
(2, 3), 
(3, 1); 


-------WISHLIST-----
INSERT INTO wishlist (userId, bookId)
VALUES
(1, '9780002005883'), 
(2, '9780006486145'), 
(3, '9780007113804'); 


-----------LIKES------
INSERT INTO likes (userId, bookId)
VALUES
(1, '9780002005883'), 
(2, '9780006512677'), 
(3, '9780006486145'); 

-------READ-------
INSERT INTO `read` (userId, bookId, date_read)
VALUES
(1, '9780002005883', '2025-01-10 14:30:00'),
(2, '9780002261982', '2025-02-05 09:00:00'),
(3, '9780002261982', '2025-03-12 20:15:00');

--------RATING----------
INSERT INTO rating (userId, bookId, rating) 
VALUES
(1, 9780002005883, 4),
(2, 9780002005883, 5),
(3,9780002005883, 3);




