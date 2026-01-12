CREATE TABLE IF NOT EXISTS authors (
    Id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    Name nvarchar(100) NOT NULL,
    Created_Date timestamp NOT NULL,
    Last_Updated_Date timestamp NULL
);