package SmartAgricultural.Management.Config;






import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/profiles}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to uploads directory
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();

        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:" + absolutePath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}