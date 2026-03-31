package cc.kertaskerja.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI realisasiPemdaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Realisasi Pemda Service API")
                        .description("Dokumentasi API untuk layanan realisasi pemda dan opd.")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    GroupedOpenApi pemdaApiGroup() {
        return GroupedOpenApi.builder()
                .group("pemda")
                .pathsToMatch("/tujuans/**", "/sasarans/**", "/ikus/**")
                .build();
    }

    @Bean
    GroupedOpenApi opdApiGroup() {
        return GroupedOpenApi.builder()
                .group("opd")
                .pathsToMatch(
                        "/tujuan_opd/**",
                        "/sasaran_opd/**",
                        "/iku_opd/**",
                        "/renja_target/**",
                        "/renja_pagu/**")
                .build();
    }

    @Bean
    GroupedOpenApi individuApiGroup() {
        return GroupedOpenApi.builder()
                .group("individu")
                .pathsToMatch("/rekin/**", "/renja_pagu_individu/**", "/renja_target_individu/**")
                .build();
    }
}
