package com.newgen.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Value("${version}")
	private String version;
	
    @Bean
    public Docket usersApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .groupName("storage_credentials_service")
                    .host("localhost:8186")
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.any())
                    .paths(paths())
                    .build();
    }
    
    @SuppressWarnings("unchecked")
	private Predicate<String> paths() {
        return or(
            regex("/storagecredentials.*")
            );
      }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Storage Credential Service")
                .description("Library Service for managing storage credentials on ECM Server")
//                .termsOfServiceUrl("http://www.opencredo.com")
                .contact(new Contact("Anushree Jain", "", "anushree.jain@newgen.co.in"))
                //.license("Apache License Version 2.0")
                //.licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version(version)
                .build();
    }
}
