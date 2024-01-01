package at.technikum.project.service.impl;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.repository.PokemonRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.service.PokemonInformationService;
import at.technikum.project.service.PokemonService;
import at.technikum.project.util.ResilienceDecorator;
import at.technikum.project.util.pokeApi.PokeApiPokemonListResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PokemonServiceImpl implements PokemonService {

    private final PokemonRepository pokemonRepository;

    private final HttpService httpService;

    private final PokemonInformationService pokemonInformationService;

    private final Retry retry;

    private final ResilienceDecorator resilienceDecorator;

    @Value("${pokemon.poke.api.url}")
    private String pokeApiUrl;

    @Override
    public void importPokemon() {
        retry.executeRunnable(() -> {
            try {
                importPokemonWithoutRetry();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private PokeApiPokemonListResponse loadPokemonFromApi() {
        val response = httpService.call(pokeApiUrl + "?limit=100000&offset=0", PokeApiPokemonListResponse.class);
        log.info("Received {} Pokemon for import", response.count());

        return response;
    }

    @Transactional
    public void importPokemonWithoutRetry() throws ExecutionException, InterruptedException {
        val result = resilienceDecorator.decorateAndGet(this::loadPokemonFromApi);

        pokemonRepository.deleteAll();
        log.info("Deleted all current Pokemon entries");

        val mappedEntities = result.results()
                .stream()
                .map(
                        (v) -> PokemonEntity.builder()
                                .name(v.name())
                                .likes(0)
                                .pokemonInformation(null)
                                .build()
                )
                .toList();

        pokemonRepository.saveAll(mappedEntities);
        log.info("Imported all {} new Pokemon entries", mappedEntities.size());
    }

    @Override
    public List<String> getAllPokemonNames() {
        return pokemonRepository.findAll().stream().map(PokemonEntity::getName).toList();
    }

    @Transactional
    @Override
    public Optional<PokemonEntity> getPokemonByName(String name) {
        val pokemonOptional = pokemonRepository.findByName(name);
        if (pokemonOptional.isEmpty()) return Optional.empty();

        var pokemon = pokemonOptional.get();
        if (pokemon.getPokemonInformation() == null) {
            val information = pokemonInformationService.loadInformationForPokemon(pokemon);
            val pokemonWithInformation = pokemon.withPokemonInformation(information);

            pokemon = pokemonRepository.save(pokemonWithInformation);
        }

        return Optional.of(pokemon);
    }

    @Override
    public PokemonEntity likePokemon(long id) {
        return null;
    }
}
