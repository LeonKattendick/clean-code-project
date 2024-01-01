package at.technikum.project.service.impl;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.repository.PokemonRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.service.PokemonService;
import at.technikum.project.util.pokeApi.PokeApiPokemonListResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class PokemonServiceImpl implements PokemonService {

    private PokemonRepository pokemonRepository;

    private HttpService httpService;

    private Retry retry;

    private CircuitBreaker circuitBreaker;

    @Override
    public void importPokemon() {
        retry.executeRunnable(this::importPokemonWithoutRetry);
    }

    private PokeApiPokemonListResponse loadPokemonFromApi() {
        val response = httpService.call("https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0", PokeApiPokemonListResponse.class);
        log.info("Received {} Pokemon for import", response.count());

        return response;
    }

    @Transactional
    public void importPokemonWithoutRetry() {
        val response = circuitBreaker.executeSupplier(this::loadPokemonFromApi);

        pokemonRepository.deleteAll();
        log.info("Deleted all current Pokemon entries");

        val mappedEntities = response.results()
                .stream()
                .map(
                        (v) -> PokemonEntity.builder()
                                .name(v.name())
                                .likes(0)
                                .dislikes(0)
                                .pokemonInformation(null)
                                .build()
                )
                .toList();

        pokemonRepository.saveAll(mappedEntities);
        log.info("Imported all {} new Pokemon entries", mappedEntities.size());
    }

    @Override
    public PokemonEntity getPokemonById(long id) {
        return null;
    }

    @Override
    public PokemonEntity likePokemon(long id) {
        return null;
    }

    @Override
    public PokemonEntity dislikePokemon(long id) {
        return null;
    }
}
