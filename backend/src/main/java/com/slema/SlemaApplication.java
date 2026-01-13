package com.slema;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.slema.mapper")
@EnableScheduling
public class SlemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlemaApplication.class, args);
    }
}
