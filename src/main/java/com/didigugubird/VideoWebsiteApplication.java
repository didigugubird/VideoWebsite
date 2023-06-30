package com.didigugubird;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan("com.didigugubird.mapper")
@EnableCaching
@SpringBootConfiguration
@EnableOpenApi
public class VideoWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoWebsiteApplication.class, args);
    }

}
