package at.technikum.project.util;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

@AllArgsConstructor
@Component
public class ResilienceDecorator {

    private ThreadPoolBulkhead threadPoolBulkhead;

    private ScheduledExecutorService scheduledExecutorService;

    private CircuitBreaker circuitBreaker;

    private TimeLimiter timeLimiter;

    public <T> T decorateAndGet(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        val decorated = Decorators.ofSupplier(supplier)
                .withThreadPoolBulkhead(threadPoolBulkhead)
                .withTimeLimiter(timeLimiter, scheduledExecutorService)
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        return decorated.get().toCompletableFuture().get();
    }
}
