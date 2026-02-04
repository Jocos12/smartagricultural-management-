package SmartAgricultural.Management.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Créer les dossiers s'ils n'existent pas
        File cropsDir = new File("uploads/crops");
        File profilesDir = new File("uploads/profiles");

        if (!cropsDir.exists()) {
            cropsDir.mkdirs();
        }

        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
        }

        // Configuration pour crops
        registry.addResourceHandler("/uploads/crops/**")
                .addResourceLocations("file:uploads/crops/")
                .setCachePeriod(0); // Pas de cache pour le développement

        // Configuration pour profiles
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:uploads/profiles/")
                .setCachePeriod(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}