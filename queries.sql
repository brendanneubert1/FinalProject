/** URL: "/{bookId}/like/{isAdd}"
// Purpose: Add or remove a "like" for a book by a user.
*//
    if (isAdd) {
        sql = "INSERT INTO likes (userId, bookId) VALUES (?, ?)";
    } else {
        sql = "DELETE FROM likes WHERE userId = ? AND bookId = ?";
    }

/** URL: "/{bookId}"
// Purpose: Retrieve detailed book information by ISBN, including author, likes, wishlist, and read status.
*/
    final String sql = """
        SELECT
            B.isbn,
            B.title,
            B.description,
            B.imglink,
            B.category,
            B.rating,
            B.num_ratings,
            B.num_pages,
            B.release_date,
            A.authorId,
            A.name AS author_name,
            COUNT(DISTINCT L.userId) AS likes_count,                   
            MAX(CASE WHEN L.userId = ? THEN 1 ELSE 0 END) AS liked,    
            MAX(CASE WHEN W.userId = ? THEN 1 ELSE 0 END) AS wishlisted,
            MAX(CASE WHEN R.userId = ? THEN 1 ELSE 0 END) AS `read`
        FROM book B
        LEFT JOIN written_by WB ON WB.bookId = B.isbn
        LEFT JOIN author A ON A.authorId = WB.authorId
        LEFT JOIN likes L ON B.isbn = L.bookId
        LEFT JOIN wishlist W ON B.isbn = W.bookId
        LEFT JOIN `read` R ON B.isbn = R.bookId
        WHERE B.isbn = ?
        GROUP BY B.isbn, A.authorId;
    """;

/** URL: "/createreview/search"
// Purpose: Search for books by title, returning basic info and author details.
*/
    final String sql = """
        SELECT 
        isbn, title, A.name, A.authorId, imglink, category 
        FROM book 
        LEFT JOIN written_by WB ON WB.bookId = book.isbn
        LEFT JOIN author A ON A.authorId = WB.authorId
        WHERE title LIKE CONCAT('%', ?, '%');
        """;

/** URL: "/{userId}"
// Purpose: Get all books wishlisted by a user, including metadata and status flags.
*/
    final String sql = 
        """
    SELECT
        B.isbn,
        B.title,
        B.description,
        B.imglink,
        B.category,
        B.rating,
        B.num_ratings,
        B.num_pages,
        B.release_date,
        A.authorId,
        A.name AS author_name,
        COUNT(DISTINCT L.userId) AS likes_count,
        MAX(CASE WHEN 1 = L.userId  THEN 1 ELSE 0 END) AS liked,
        MAX(CASE WHEN 1 = W.userId  THEN 1 ELSE 0 END) AS wishlisted,
        MAX(CASE WHEN 1 = R.userId  THEN 1 ELSE 0 END) AS `read`
    FROM book B
    LEFT JOIN written_by WB ON WB.bookId = B.isbn
    LEFT JOIN author A ON A.authorId = WB.authorId
    LEFT JOIN likes as L ON b.isbn = L.bookId
    LEFT JOIN wishlist as W ON B.isbn = W.bookId
    LEFT JOIN `read` as R ON B.isbn = R.bookId
    WHERE W.userId = ?
    GROUP BY B.isbn, A.authorId;       
            """;


/** URL: "/{userId}"
// Purpose: Get all books marked as "read" by a user, including metadata and status flags.
*/
    final String sql = 
        """
    SELECT
        B.isbn,
        B.title,
        B.description,
        B.imglink,
        B.category,
        B.rating,
        B.num_ratings,
        B.num_pages,
        B.release_date,
        A.authorId,
        A.name AS author_name,
        COUNT(DISTINCT L.userId) AS likes_count,
        MAX(CASE WHEN 1 = L.userId  THEN 1 ELSE 0 END) AS liked,
        MAX(CASE WHEN 1 = W.userId  THEN 1 ELSE 0 END) AS wishlisted,
        MAX(CASE WHEN 1 = R.userId  THEN 1 ELSE 0 END) AS `read`
    FROM book B
    LEFT JOIN written_by WB ON WB.bookId = B.isbn
    LEFT JOIN author A ON A.authorId = WB.authorId
    LEFT JOIN likes as L ON b.isbn = L.bookId
    LEFT JOIN wishlist as W ON B.isbn = W.bookId
    LEFT JOIN `read` as R ON B.isbn = R.bookId
    WHERE R.userId = ?
    GROUP BY B.isbn, A.authorId;       
            """;

/** URL: "/{bookId}/rate" or "/books/{isbn}/rate"
// Purpose: Insert or update a user’s rating for a book.
*/
    String sql = """
        INSERT INTO rating (userId, bookId, rating)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE rating = VALUES(rating)
    """;

/** URL: "/{bookId}"
// Purpose: Get a specific user’s rating for a given book.
*/
    String sql = """
        SELECT * FROM rating
        WHERE userId = ? AND bookId = ?
    """;

/** URL: "/{bookId}"
// Purpose: Calculate the average rating for a book across all users.
*/
    String sql = """
        SELECT AVG(rating) AS avg_rating FROM rating
        WHERE bookId = ?
    """;

/** URL: "/createreview"
// Purpose: Create a new review for a book by a user, storing text, recommendation, and timestamp.
*/
    String findBookSql = "SELECT isbn FROM book WHERE title = ?";
    String insertSql = "INSERT INTO review (userId, bookId, body, created_date, recommended) VALUES (?, ?, ?, NOW(), ?)";


/** URL: "/home"
// Purpose: Retrieve the latest 20 reviews with book details for the home feed.
*//
    String sql = """
        SELECT r.reviewId, r.userId, r.body, r.recommended, r.created_date,
            b.isbn, b.title, b.imglink, b.category, b.rating, b.num_ratings, b.releaseDate AS publishDate
        FROM review r
        JOIN book b ON r.bookId = b.isbn
        ORDER BY r.created_date DESC
        LIMIT 20
    """;
/** URL: "/register"
// Purpose: Register a new user with username, password, and personal details.
*/
final String registerSql = "INSERT INTO user (username, password, firstName, lastName) VALUES (?, ?, ?, ?)";

/** URL: "/login"
// Purpose: Authenticate a user by checking if the username exists 
*/
final String sql = "SELECT * FROM user WHERE username = ?";