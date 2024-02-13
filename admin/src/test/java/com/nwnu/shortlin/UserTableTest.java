package com.nwnu.shortlin;

public class UserTableTest {

    public static final String SQl = "DROP TABLE IF EXISTS `t_link_%d`;\n" +
            "CREATE TABLE `t_link_%d`  (\n" +
            "  `id` bigint NOT NULL COMMENT 'ID',\n" +
            "  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '域名',\n" +
            "  `short_url` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短链接',\n" +
            "  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '完整短链接',\n" +
            "  `origin` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始链接',\n" +
            "  `click_num` int NULL DEFAULT NULL COMMENT '点击量',\n" +
            "  `enable_status` tinyint(1) NULL DEFAULT NULL COMMENT '启用标识0：启用，1未启用',\n" +
            "  `create_type` tinyint(1) NULL DEFAULT NULL COMMENT '创建类型0：控制台，1接口创建',\n" +
            "  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分组id',\n" +
            "  `valid_date_type` tinyint(1) NULL DEFAULT NULL COMMENT '有效类型0：永久有效，1自定义',\n" +
            "  `valid_date` datetime NULL DEFAULT NULL COMMENT '有效期',\n" +
            "  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',\n" +
            "  `creat_time` datetime NULL DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',\n" +
            "  `del_flag` tinyint NULL DEFAULT NULL COMMENT '删除标识0：未删除，1删除',\n" +
            "  `favicon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片连接',\n" +
            "  PRIMARY KEY (`id`) USING BTREE,\n" +
            "  UNIQUE INDEX `id_unique_full_shortlink`(`full_short_url` ASC) USING BTREE COMMENT '全部短链接唯一索引'\n" +
            ") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;\n";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.println(String.format(SQl, i,i));
        }
    }
}
