package com.emiratiyo.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.emiratiyo.api.util.EmiraInternalInterceptor;
import com.emiratiyo.api.util.RateLimitingInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;
    private final EmiraInternalInterceptor emiraInternalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(emiraInternalInterceptor)
                .addPathPatterns("/api/v1/internal/emira/**");
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/**");
    }
}