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

    @Bean
    ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        ReactiveOAuth2AuthorizedClientProvider provider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
        );

        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

    @Bean
    WebClient webClient(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager
    ) {
        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager
        );

        oauth2.setDefaultClientRegistrationId("gateway");

        return WebClient.builder()
                .filter(oauth2)
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = uploadDirectory.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }

}
