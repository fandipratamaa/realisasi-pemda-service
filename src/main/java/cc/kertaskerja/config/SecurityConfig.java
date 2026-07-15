package cc.kertaskerja.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
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
            "/renja_opd"
    );
    private static final List<String> INDIVIDU_ALLOWED_GET_PREFIXES = List.of(
            "/renaksi_individu",
            "/rekin",
            "/renja_individu"
    );
    private static final List<String> OPD_ALLOWED_GET_PREFIXES = List.of(
            "/tujuan_opd",
            "/sasaran_opd"
    );
    private static final List<String> INDIVIDU_CORE_FULL_ACCESS_PREFIXES = List.of(
            "/renaksi_individu",
            "/rekin",
            "/renja_individu"
    );
    private static final List<String> INDIVIDU_RENJA_ALLOWED_GET_PREFIXES = List.of(
            "/renja_individu"
    );
    private static final List<String> INDIVIDU_ALL_FULL_ACCESS_PREFIXES = List.of(
            "/renaksi_individu",
            "/rekin",
            "/renja_individu"
    );
    private static final List<String> INDIVIDU_RENJA_PROGRAM_FULL_ACCESS_PREFIXES = List.of(
            "/renja_individu/program"
    );
    private static final List<String> INDIVIDU_RENJA_KEGIATAN_SUBKEGIATAN_FULL_ACCESS_PREFIXES = List.of(
            "/renja_individu/kegiatan",
            "/renja_individu/subkegiatan"
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
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .requestCache(requestCacheSpec ->
                        requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    // convert / ekstrak role agar nilainya sesuai dan selalu (super_admin, admin_opd, level_1, level_2, level_3, level_4) 
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            JwtGrantedAuthoritiesConverter defaultAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> defaultAuthorities = defaultAuthoritiesConverter.convert(jwt);
            Set<GrantedAuthority> authorities = new HashSet<>(defaultAuthorities != null ? defaultAuthorities : Collections.emptySet());

            if (defaultAuthorities != null) {
                for (GrantedAuthority auth : defaultAuthorities) {
                    if (auth.getAuthority().startsWith("SCOPE_")) {
                        authorities.add(new SimpleGrantedAuthority(auth.getAuthority().substring(6)));
                    }
                }
            }

            Object realmAccessObj = jwt.getClaim("realm_access");
            if (realmAccessObj instanceof Map) {
                Map<?, ?> realmAccess = (Map<?, ?>) realmAccessObj;
                Object rolesObj = realmAccess.get("roles");
                if (rolesObj instanceof Collection) {
                    for (Object role : (Collection<?>) rolesObj) {
                        authorities.add(new SimpleGrantedAuthority(String.valueOf(role)));
                    }
                }
            }

            Object resourceAccessObj = jwt.getClaim("resource_access");
            if (resourceAccessObj instanceof Map) {
                Map<?, ?> resourceAccess = (Map<?, ?>) resourceAccessObj;
                for (Object clientObj : resourceAccess.values()) {
                    if (clientObj instanceof Map) {
                        Map<?, ?> clientAccess = (Map<?, ?>) clientObj;
                        Object rolesObj = clientAccess.get("roles");
                        if (rolesObj instanceof Collection) {
                            for (Object role : (Collection<?>) rolesObj) {
                                authorities.add(new SimpleGrantedAuthority(String.valueOf(role)));
                            }
                        }
                    }
                }
            }

            Object rootRolesObj = jwt.getClaim("roles");
            if (rootRolesObj instanceof Collection) {
                for (Object role : (Collection<?>) rootRolesObj) {
                    authorities.add(new SimpleGrantedAuthority(String.valueOf(role)));
                }
            }

            return authorities;
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
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
        if (matchesAnyPrefix(path, INDIVIDU_ALL_FULL_ACCESS_PREFIXES)
                || matchesAnyPrefix(path, INDIVIDU_RENJA_PROGRAM_FULL_ACCESS_PREFIXES)) {
            return true;
        }
        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, OPD_ALLOWED_GET_PREFIXES);
    }
    private boolean isLevel3Allowed(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (matchesAnyPrefix(path, INDIVIDU_ALL_FULL_ACCESS_PREFIXES)
                || matchesAnyPrefix(path, INDIVIDU_RENJA_KEGIATAN_SUBKEGIATAN_FULL_ACCESS_PREFIXES)) {
            return true;
        }
        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && matchesAnyPrefix(path, OPD_ALLOWED_GET_PREFIXES);
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
