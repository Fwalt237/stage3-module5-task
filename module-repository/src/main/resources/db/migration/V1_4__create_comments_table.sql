CREATE TABLE IF NOT EXISTS comments (

    Id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    Content nvarchar(255) NOT NULL,
    Created_Date timestamp NOT NULL,
    Last_Updated_Date timestamp NULL,
    News_Id bigint NULL,

    CONSTRAINT FK_COMMENTS_NEWS FOREIGN KEY (News_Id) REFERENCES news(Id) ON DELETE SET NULL
    );