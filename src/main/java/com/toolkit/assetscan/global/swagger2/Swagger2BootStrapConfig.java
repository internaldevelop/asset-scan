package com.toolkit.assetscan.global.swagger2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2BootStrapConfig extends WebMvcConfigurationSupport {

    //    @Bean
//    public Docket createH5RestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .groupName("h5")
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.xiaoleitech.authapi"))
//                .paths(PathSelectors.any())
//                .build();
//    }
    @Value("${swagger.show:false}")
    private boolean swaggerShow;

    @Bean
    public Docket createPcRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerShow)
                .apiInfo(apiInfo())
                .groupName("SystemAutoConfigAPI")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.toolkit.assetscan"))
                .paths(PathSelectors.any())
                .build();
    }

    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Asset Scanning System API")
                //创建人
                .contact(new Contact("WeiYitao", "", ""))
                //版本号
                .version("1.0")
                //描述
                .description("主站终端自动化配置系统接口API 描述")
                .build();
    }

    //添加ResourceHandler
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("doc.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//        super.addResourceHandlers(registry);
//    }

}
