package com.ai.avance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Mapeos de rutas básicas para manejar redirecciones SPA
        registry.addViewController("/").setViewName("forward:/index");
        registry.addViewController("/home").setViewName("forward:/index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        
        // Rutas de la aplicación principal
        registry.addViewController("/chat").setViewName("chat");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/settings").setViewName("settings");
        registry.addViewController("/chatbot").setViewName("chatbot");
        
        // Rutas para cuando ocurre error 404, redireccionar al inicio
        registry.addViewController("/error").setViewName("forward:/");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar los manejadores de recursos estáticos
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
    }
}
