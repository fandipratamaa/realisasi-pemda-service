package cc.kertaskerja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebClientConfig implements WebFluxConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadDirectory.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }

}
