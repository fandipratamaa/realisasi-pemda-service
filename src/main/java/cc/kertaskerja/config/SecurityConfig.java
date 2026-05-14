package cc.kertaskerja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    private static final List<String> PEMDA_ENDPOINT_PREFIXES = List.of(
            "/tujuans",
            "/sasarans",
            "/ikus"
    );

    private static final List<String> OPD_ENDPOINT_PREFIXES = List.of(
            "/tujuan_opd",
            "/sasaran_opd",
            "/iku_opd",
            "/renaksi_opd",
            "/renja_target",
            "/renja_pagu"
    );

    private static final List<String> INDIVIDU_ALLOWED_GET_PREFIXES = List.of(
            "/renaksi",
            "/rekin",
            "/sasaran_individu",
            "/renja_target_individu",
            "/renja_pagu_individu"
    );

    private static final List<String> OPD_ALLOWED_GET_PREFIXES = List.of(
            "/tujuan_opd",
            "/sasaran_opd"
    );

    private static final List<String> INDIVIDU_CORE_FULL_ACCESS_PREFIXES = List.of(
            "/renaksi",
            "/rekin",
            "/sasaran_individu"
    );

    private static final List<String> INDIVIDU_RENJA_ALLOWED_GET_PREFIXES = List.of(
            "/renja_target_individu",
            "/renja_pagu_individu"
    );

    private static final List<String> INDIVIDU_ALL_FULL_ACCESS_PREFIXES = List.of(
            "/renaksi",
            "/rekin",
            "/sasaran_individu",
            "/renja_target_individu",
            "/renja_pagu_individu"
    );

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .anyExchange().access((authentication, context) -> authentication
                                .map(auth -> new AuthorizationDecision(isAuthorized(auth, context.getExchange())))
                                .defaultIfEmpty(new AuthorizationDecision(false))))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults()))
                .requestCache(requestCacheSpec ->
                        requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    private boolean isAuthorized(Authentication authentication, ServerWebExchange exchange) {
        if (!authentication.isAuthenticated()) {
            return false;
        }

        if (hasSuperAdminAuthority(authentication)) {
            return isSuperAdminAllowed(exchange);
        }

        if (hasAdminOpdAuthority(authentication)) {
            return isAdminOpdAllowed(exchange);
        }

        if (hasLevel1Authority(authentication)) {
            return isLevel1Allowed(exchange);
        }

        if (hasLevel2Authority(authentication)) {
            return isLevel2Allowed(exchange);
        }

        if (hasLevel3Authority(authentication)) {
            return isLevel3Allowed(exchange);
        }

        if (hasLevel4Authority(authentication)) {
            return isLevel4Allowed(exchange);
        }

        return true;
    }

    private boolean hasSuperAdminAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "super_admin".equalsIgnoreCase(authority)
                        || "ROLE_SUPER_ADMIN".equalsIgnoreCase(authority));
    }

    private boolean hasAdminOpdAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "admin_opd".equalsIgnoreCase(authority)
                        || "ROLE_ADMIN_OPD".equalsIgnoreCase(authority));
    }

    private boolean hasLevel1Authority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "level_1".equalsIgnoreCase(authority)
                        || "ROLE_LEVEL_1".equalsIgnoreCase(authority));
    }

    private boolean hasLevel2Authority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "level_2".equalsIgnoreCase(authority)
                        || "ROLE_LEVEL_2".equalsIgnoreCase(authority));
    }

    private boolean hasLevel3Authority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "level_3".equalsIgnoreCase(authority)
                        || "ROLE_LEVEL_3".equalsIgnoreCase(authority));
    }

    private boolean hasLevel4Authority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(authority -> "level_4".equalsIgnoreCase(authority)
                        || "ROLE_LEVEL_4".equalsIgnoreCase(authority));
    }

    private boolean isSuperAdminAllowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (matchesAnyPrefix(path, PEMDA_ENDPOINT_PREFIXES) || matchesAnyPrefix(path, OPD_ENDPOINT_PREFIXES)) {
            return true;
        }

        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, INDIVIDU_ALLOWED_GET_PREFIXES);
    }

    private boolean isAdminOpdAllowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (matchesAnyPrefix(path, OPD_ENDPOINT_PREFIXES)) {
            return true;
        }

        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, INDIVIDU_ALLOWED_GET_PREFIXES);
    }

    private boolean isLevel1Allowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (matchesAnyPrefix(path, INDIVIDU_CORE_FULL_ACCESS_PREFIXES)) {
            return true;
        }

        if (HttpMethod.GET.equals(exchange.getRequest().getMethod())) {
            return matchesAnyPrefix(path, OPD_ALLOWED_GET_PREFIXES)
                    || matchesAnyPrefix(path, INDIVIDU_RENJA_ALLOWED_GET_PREFIXES);
        }

        return false;
    }

    private boolean isLevel2Allowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (matchesAnyPrefix(path, INDIVIDU_ALL_FULL_ACCESS_PREFIXES)) {
            return true;
        }

        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, OPD_ALLOWED_GET_PREFIXES);
    }

    private boolean isLevel3Allowed(ServerWebExchange exchange) {
        return isLevel2Allowed(exchange);
    }

    private boolean isLevel4Allowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (matchesAnyPrefix(path, INDIVIDU_CORE_FULL_ACCESS_PREFIXES)) {
            return true;
        }

        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, OPD_ALLOWED_GET_PREFIXES);
    }

    private boolean matchesAnyPrefix(String path, List<String> prefixes) {
        return prefixes.stream().anyMatch(prefix -> matchesPrefix(path, prefix));
    }

    private boolean matchesPrefix(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
