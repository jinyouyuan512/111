package com.jiyi.creative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.jiyi.creative.mapper")
public class CreativeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreativeServiceApplication.class, args);
    }
}
