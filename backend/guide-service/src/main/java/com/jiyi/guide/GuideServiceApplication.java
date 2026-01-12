package com.jiyi.guide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jiyi.guide.mapper")
public class GuideServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuideServiceApplication.class, args);
    }
}
