package com.toolkit.assetscan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan(basePackages = {"com.toolkit.assetscan.dao.mybatis"})
@EnableRedisHttpSession
public class AssetScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetScanApplication.class, args);
    }

}
