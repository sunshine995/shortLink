package com.nwnu.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nwnu.shortlink.admin.dao.mapper")
public class shortLinkApplicationAdmin {
    public static void main(String[] args) {
        SpringApplication.run(shortLinkApplicationAdmin.class);
    }
}
