package cc.kertaskerja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing(auditorAwareRef = "auditorAware")
public class DataConfig {
    @Bean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    Object principal = authentication.getPrincipal();
                    if (principal instanceof Jwt jwt) {
                        String username = jwt.getClaimAsString("preferred_username");
                        if (username != null && !username.isBlank()) {
                            return username;
                        }
                    }
                    return authentication.getName();
                })
                .switchIfEmpty(Mono.just("guest"));
    }
}
