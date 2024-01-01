package at.technikum.project.service;

import at.technikum.project.persistence.repository.PokemonRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.service.impl.PokemonServiceImpl;
import at.technikum.project.util.*;
import at.technikum.project.util.pokeApi.PokeApiPokemonListResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private HttpService httpService;

    private PokemonServiceImpl pokemonService;

    @BeforeEach
    void setUp() {
        pokemonService = new PokemonServiceImpl(
                pokemonRepository,
                httpService,
                new PokemonRetryConfig(3, 100).retry(),
                new PokemonBulkheadConfig().bulkhead(),
                new PokemonExecutorService().scheduledExecutorService(),
                new PokemonCircuitBreakerConfig(100, 2, 100).circuitBreaker(),
                new PokemonTimeLimiterConfig(100).timeLimiter()
        );
    }

    @Test
    void importPokemonWithoutRetry_notThrowsAnyException_whenCallSucceeds() {
        when(httpService.call(anyString(), any())).thenReturn(pokeApiPokemonListResponse());

        assertDoesNotThrow(() -> pokemonService.importPokemonWithoutRetry());
    }

    @Test
    void timeLimiter_throwsTimeoutException_whenCallTakesToLong() {
        when(httpService.call(anyString(), any())).thenAnswer(invocation -> {
            Thread.sleep(200);
            return pokeApiPokemonListResponse();
        });

        ExecutionException executionException = assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());

        assertInstanceOf(TimeoutException.class, executionException.getCause());
        assertEquals("TimeLimiter 'pokemon-time-limiter' recorded a timeout exception.", executionException.getCause().getMessage());
    }

    @Test
    void circuitBreaker_throwsCallNotPermittedExceptionException_whenToManyFailed() {
        when(httpService.call(anyString(), any())).thenThrow(new RuntimeException());

        ExecutionException executionException1 = assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());
        ExecutionException executionException2 = assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());
        ExecutionException executionException3 = assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());

        assertInstanceOf(RuntimeException.class, executionException1.getCause());
        assertInstanceOf(RuntimeException.class, executionException2.getCause());
        assertInstanceOf(CallNotPermittedException.class, executionException3.getCause());
        assertEquals("CircuitBreaker 'pokemon-circuit-breaker' is OPEN and does not permit further calls", executionException3.getCause().getMessage());
    }

    @Test
    void circuitBreaker_opensAgain_afterTimeout() throws InterruptedException {
        when(httpService.call(anyString(), any()))
                .thenThrow(new RuntimeException())
                .thenThrow(new RuntimeException())
                .thenReturn(pokeApiPokemonListResponse());

        assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());
        assertThrows(ExecutionException.class, () -> pokemonService.importPokemonWithoutRetry());
        Thread.sleep(200);
        assertDoesNotThrow(() -> pokemonService.importPokemonWithoutRetry());
    }

    @Test
    void retry_whenSuccess_runsOnce() {
        when(httpService.call(anyString(), any())).thenReturn(pokeApiPokemonListResponse());

        pokemonService.importPokemon();

        verify(httpService, times(1)).call(anyString(), any());
    }

    @Test
    void retry_whenOneFails_runsASecondTime() {
        when(httpService.call(anyString(), any()))
                .thenThrow(new RuntimeException())
                .thenReturn(pokeApiPokemonListResponse());

        pokemonService.importPokemon();

        verify(httpService, times(2)).call(anyString(), any());
    }


    @Test
    void retry_whenAllFail_failsAfterThirdTime() {
        when(httpService.call(anyString(), any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> pokemonService.importPokemon());

        verify(httpService, times(3)).call(anyString(), any());
    }

    private PokeApiPokemonListResponse pokeApiPokemonListResponse() {
        return new PokeApiPokemonListResponse(0, Collections.emptyList());
    }
}
