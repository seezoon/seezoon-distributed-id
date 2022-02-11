package com.seezoon;

import com.seezoon.application.SegmentProperties;
import com.seezoon.mybatis.repository.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan(basePackages = "com.seezoon.domain.repository.mapper", markerInterface = BaseMapper.class)
@SpringBootApplication
@EnableConfigurationProperties({SegmentProperties.class})
public class MainApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
