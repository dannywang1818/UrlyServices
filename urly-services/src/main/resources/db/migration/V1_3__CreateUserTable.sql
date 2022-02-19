-- ----------------------------
-- Table structure for USER
-- ----------------------------
DROP TABLE IF EXISTS USER;

CREATE TABLE USER(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(511),
    roles VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    provider VARCHAR(255)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- INSERT INTO`USER`(`email`,`username`,`password`, `role`) VALUES('test1@gmail.com', 'test1', '$2a$12$whb8WjUPOA4dwnZvcAyuSuvHSmhRNYYHiUFdYCi.WeZe5a/HMFJTu', 'USER');
