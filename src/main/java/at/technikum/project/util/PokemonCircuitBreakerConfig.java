package at.technikum.project.util;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PokemonCircuitBreakerConfig {

    private final int failureRateThreshold;

    private final int minimumNumberOfCalls;

    private final int waitDurationInOpenStateInMillis;

    public PokemonCircuitBreakerConfig(
            @Value("${pokemon.circuit-breaker.failureRateThreshold}") int failureRateThreshold,
            @Value("${pokemon.circuit-breaker.minimumNumberOfCalls}") int minimumNumberOfCalls,
            @Value("${pokemon.circuit-breaker.waitDurationInOpenStateInMillis}") int waitDurationInOpenStateInMillis
    ) {
        this.failureRateThreshold = failureRateThreshold;
        this.minimumNumberOfCalls = minimumNumberOfCalls;
        this.waitDurationInOpenStateInMillis = waitDurationInOpenStateInMillis;
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenStateInMillis))
                .build();
        return CircuitBreaker.of("pokemon-circuit-breaker", config);
    }
}
