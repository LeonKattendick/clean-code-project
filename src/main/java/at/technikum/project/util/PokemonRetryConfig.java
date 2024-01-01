package at.technikum.project.util;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.internal.RetryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PokemonRetryConfig {

    private final int maxAttempts;

    private final int waitDurationInMillis;

    public PokemonRetryConfig(
            @Value("${pokemon.retry.maxAttempts}") int maxAttempts,
            @Value("${pokemon.retry.waitDurationInMillis}") int waitDurationInMillis
    ) {
        this.maxAttempts = maxAttempts;
        this.waitDurationInMillis = waitDurationInMillis;
    }

    @Bean
    public Retry retry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDurationInMillis))
                .build();
        return new RetryImpl<>("pokemon-retry", config);
    }
}
