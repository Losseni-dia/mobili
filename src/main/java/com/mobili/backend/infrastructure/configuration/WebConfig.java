
package com.mobili.backend.infrastructure.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le chemin physique doit être absolu
        String uploadPath = System.getProperty("user.dir") + "/.uploads/";

        registry.addResourceHandler("/uploads/**") // L'URL web
                .addResourceLocations("file:" + uploadPath); // Le dossier réel
    }
}
