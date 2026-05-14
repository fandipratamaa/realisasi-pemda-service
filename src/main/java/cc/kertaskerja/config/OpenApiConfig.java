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
                        .description("Dokumentasi API untuk layanan realisasi pemda, opd, dan individu. Role `super_admin` hanya dapat mengakses seluruh endpoint grup pemda dan opd, serta endpoint `GET` pada grup individu untuk resource renaksi, rekin, sasaran_individu, renja_target_individu, dan renja_pagu_individu. Role `admin_opd` hanya dapat mengakses seluruh endpoint grup opd, serta endpoint `GET` pada grup individu untuk resource yang sama. Role `level_1` hanya dapat mengakses endpoint `GET` pada `tujuan_opd` dan `sasaran_opd`, endpoint `GET` pada `renja_target_individu` dan `renja_pagu_individu`, serta seluruh endpoint pada `renaksi`, `rekin`, dan `sasaran_individu`. Role `level_2` dan `level_3` hanya dapat mengakses endpoint `GET` pada `tujuan_opd` dan `sasaran_opd`, serta seluruh endpoint pada seluruh service individu. Role `level_4` hanya dapat mengakses endpoint `GET` pada `tujuan_opd` dan `sasaran_opd`, serta seluruh endpoint pada `renaksi`, `rekin`, dan `sasaran_individu`, namun tidak dapat mengakses semua endpoint `renja_target_individu` dan `renja_pagu_individu`.")
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
                .pathsToMatch(
                        "/tujuans/**", 
                        "/sasarans/**", 
                        "/ikus/**")
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
                        "/renaksi_opd/**",
                        "/renja_target/**",
                        "/renja_pagu/**")
                .build();
    }

    @Bean
    GroupedOpenApi individuApiGroup() {
        return GroupedOpenApi.builder()
                .group("individu")
                .pathsToMatch(
                        "/rekin/**",
                        "/sasaran_individu/**",
                        "/renja_pagu_individu/**", 
                        "/renja_target_individu/**", 
                        "/renaksi/**")
                .build();
    }
}
