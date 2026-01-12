CREATE TABLE IF NOT EXISTS news (
    Id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    Title nvarchar(100) NOT NULL,
    Content nvarchar(255) NOT NULL,
    Created_Date timestamp NOT NULL,
    Last_Updated_Date timestamp NULL,
    Author_Id bigint NULL,

    CONSTRAINT FK_NEWS_AUTHOR FOREIGN KEY (Author_Id) REFERENCES authors(Id) ON DELETE SET NULL
);