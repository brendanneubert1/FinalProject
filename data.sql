INSERT INTO `user` (username, password, firstname, lastname)
VALUES
('alice123', 'pass1', 'Alice', 'Johnson'),
('bob456', 'pass2', 'Bob', 'Smith'),
('carol789', 'pass3', 'John', 'Doe');

INSERT INTO review (userId, bookId, recommended, body, created_date)
VALUES
(1, '9780002005883', TRUE, 'Amazing story, very engaging!', NOW()),
(2, '9780002261982', TRUE, 'Thought-provoking and emotional.', NOW()),
(3, '9780006163831', FALSE, 'Too long for my taste, but imaginative.', NOW());


INSERT INTO follows (userId, follows)
VALUES
(1, 2), 
(2, 3), 
(3, 1); 


INSERT INTO wishlist (userId, bookId)
VALUES
(1, '9780002005883'), 
(2, '9780006486145'), 
(3, '9780007113804'); 


INSERT INTO likes (userId, bookId)
VALUES
(1, '9780002005883'), 
(2, '9780006512677'), 
(3, '9780006486145'); 

INSERT INTO `read` (userId, bookId, date_read)
VALUES
(1, '9780002005883', '2025-01-10 14:30:00'),
(2, '9780002261982', '2025-02-05 09:00:00'),
(3, '9780002261982', '2025-03-12 20:15:00');

INSERT INTO rating (userId, bookId, rating) 
VALUES
(1, 9780002005883, 4),
(2, 9780002005883, 5),
(3, 9780002005883, 3);




