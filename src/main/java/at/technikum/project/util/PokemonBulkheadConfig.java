package at.technikum.project.util;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PokemonBulkheadConfig {

    @Bean
    public ThreadPoolBulkhead bulkhead() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .build();
        return ThreadPoolBulkhead.of("pokemon-bulkhead", config);
    }
}
