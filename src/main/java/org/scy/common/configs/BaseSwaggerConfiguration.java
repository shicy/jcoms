package org.scy.common.configs;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SuppressWarnings("unused")
public abstract class BaseSwaggerConfiguration {

    @Bean
    public Docket createRestApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(getTitle())
                .description(getDescription())
                .termsOfServiceUrl(getServiceUrl())
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage(getApiPackage()))
                .paths(PathSelectors.any())
                .build();
    }

    protected abstract String getTitle();

    protected abstract String getDescription();

    protected abstract String getServiceUrl();

    protected abstract String getApiPackage();
}
