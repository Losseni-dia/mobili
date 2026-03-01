
package com.mobili.backend.infrastructure.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // On définit le chemin ABSOLU sans aucune ambiguïté
        // Note le "/" à la fin, c'est crucial !
        String location = "file:///C:/Users/User/Desktop/prj/mobili/backend/.uploads/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(0);

        System.out.println("--- TENTATIVE FINALE ---");
        System.out.println("Spring surveille ce dossier : " + location);
    }
}
