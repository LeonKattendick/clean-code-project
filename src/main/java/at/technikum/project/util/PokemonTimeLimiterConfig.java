package at.technikum.project.util;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PokemonTimeLimiterConfig {

    private final int timeoutDurationInMillis;

    public PokemonTimeLimiterConfig(@Value("${pokemon.time-limiter.timeoutDurationInMillis}") int timeoutDurationInMillis) {
        this.timeoutDurationInMillis = timeoutDurationInMillis;
    }

    @Bean
    public TimeLimiter timeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(timeoutDurationInMillis))
                .build();
        return TimeLimiter.of("pokemon-time-limiter", config);
    }
}
