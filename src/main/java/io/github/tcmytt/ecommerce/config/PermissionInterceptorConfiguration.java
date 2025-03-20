package io.github.tcmytt.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.lang.NonNull;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/api/v1/products/**", "/api/v1/categories/**"
        };
        // "/api/v1/products/**", "/api/v1/coupons/**" , "/api/v1/users/**",
        // "/api/v1/roles/**", "/api/v1/reviews/**","/api/v1/orders/**",
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
