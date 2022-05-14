package com.wei.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.wei.reggie.service","com.wei.reggie.mapper",
"com.wei.reggie.entity","com.wei.reggie.controller","com.wei.reggie.common","com.wei.reggie.service.impl",
        "com.wei.reggie.config"})
@ServletComponentScan
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");
    }
}
