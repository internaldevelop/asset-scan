package com.toolkit.assetscan;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@MapperScan(basePackages = {"com.toolkit.assetscan.dao.mybatis"})
@EnableRedisHttpSession
public class AssetScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetScanApplication.class, args);
    }

}
