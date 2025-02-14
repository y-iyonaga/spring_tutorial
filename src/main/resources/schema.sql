CREATE TABLE issues (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    summary VARCHAR(256) NOT NULL UNIQUE, -- UNIQUE制約を追加
    description VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);



CREATE TABLE issues_creator (
    ID BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    issues_ID BIGINT NOT NULL,
    creatorName VARCHAR(256) NOT NULL,
    tourokuDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (issues_ID) REFERENCES issues(id)
);
