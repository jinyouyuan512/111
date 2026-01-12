package com.jiyi.academy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.jiyi.academy.mapper")
@ComponentScan(basePackages = {"com.jiyi.academy", "com.jiyi.common"})
public class AcademyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcademyServiceApplication.class, args);
    }
}
