package com.nwnu.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nwnu.shortlink.project.dao.mapper")
public class shortLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(shortLinkApplication.class);
    }
}
