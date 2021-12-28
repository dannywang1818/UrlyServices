-- ----------------------------
-- Table structure for LONG_SEQUENCE_ID
-- ----------------------------
DROP TABLE IF EXISTS LONG_TO_SEQUENCE_ID;

CREATE TABLE LONG_TO_SEQUENCE_ID(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sequence_id BIGINT NOT NULL,
    long_url VARCHAR(256) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;