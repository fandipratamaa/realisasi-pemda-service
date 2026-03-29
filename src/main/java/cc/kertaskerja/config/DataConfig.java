package cc.kertaskerja.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
public class DataConfig {
    @Bean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> Mono.just("guest"); //default guest
    }
}
