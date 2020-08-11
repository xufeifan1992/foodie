package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter(){

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedOrigin("http://192.168.133.129:8080");
        corsConfiguration.addAllowedOrigin("http://192.168.133.129");

        //设置是否发送cookie信息
        corsConfiguration.setAllowCredentials(true);

        //设置允许交互的方式
        corsConfiguration.addAllowedMethod("*");

        //设置允许请求的方式
        corsConfiguration.addAllowedHeader("*");

        //为url添加路径映射信息
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(corsConfigurationSource);

    }
}
