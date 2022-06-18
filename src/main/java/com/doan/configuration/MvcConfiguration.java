package com.doan.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resources/bootstrap/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap/4.6.1/");
        registry.addResourceHandler("/resources/jquery/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/jquery/3.5.1/");
        registry.addResourceHandler("/resources/popper/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/popper.js/1.16.0/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath: /static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath: /static/js/");
        registry.addResourceHandler("/richtext/**")
                .addResourceLocations("classpath: /static/richtexteditor/");
    }



}
