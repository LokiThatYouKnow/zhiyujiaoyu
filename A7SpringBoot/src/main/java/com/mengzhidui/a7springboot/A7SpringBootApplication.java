package com.mengzhidui.a7springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.mengzhidui.a7springboot.mapper")
@EnableScheduling // 启用定时任务(没写完数据大屏，暂时不启用，写完再启用)
public class A7SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(A7SpringBootApplication.class, args);
    }

}
