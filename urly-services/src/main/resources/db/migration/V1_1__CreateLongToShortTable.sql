-- ----------------------------
-- Table structure for LONG_TO_SHORT
-- ----------------------------
DROP TABLE IF EXISTS LONG_TO_SHORT;

CREATE TABLE LONG_TO_SHORT(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL DEFAULT NOW(6),
    long_url VARCHAR(256) NOT NULL,
    short_url VARCHAR(20) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;