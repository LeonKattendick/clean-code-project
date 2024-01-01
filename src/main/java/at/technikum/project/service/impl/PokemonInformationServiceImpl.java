package at.technikum.project.service.impl;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.model.PokemonInformationEntity;
import at.technikum.project.persistence.model.PokemonTypeEntity;
import at.technikum.project.persistence.repository.PokemonInformationRepository;
import at.technikum.project.persistence.repository.PokemonTypeRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.service.PokemonInformationService;
import at.technikum.project.util.ResilienceDecorator;
import at.technikum.project.util.pokeApi.PokeApiPokemonResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PokemonInformationServiceImpl implements PokemonInformationService {

    private final PokemonInformationRepository pokemonInformationRepository;

    private final PokemonTypeRepository pokemonTypeRepository;

    private final HttpService httpService;

    private final Retry retry;

    private final ResilienceDecorator resilienceDecorator;

    @Value("${pokemon.poke.api.url}")
    private String pokeApiUrl;

    @Override
    public PokemonInformationEntity loadInformationForPokemon(PokemonEntity pokemon) {
        return retry.executeSupplier(() -> {
            try {
                return loadInformationForPokemonWithoutRetry(pokemon);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private PokeApiPokemonResponse loadPokemonFromApi(String name) {
        val response = httpService.call(pokeApiUrl + "/" + name, PokeApiPokemonResponse.class);
        log.info("Received {} as Pokemon", response);

        return response;
    }

    @Transactional
    public PokemonInformationEntity loadInformationForPokemonWithoutRetry(PokemonEntity pokemon) throws ExecutionException, InterruptedException {
        val result = resilienceDecorator.decorateAndGet(() -> loadPokemonFromApi(pokemon.getName()));

        val informationEntity = pokemonInformationRepository.save(
                PokemonInformationEntity.builder()
                        .height(result.height())
                        .imageUrl(result.sprites().url())
                        .build()
        );

        val types = pokemonTypeRepository.saveAll(
                result.types().stream().map(
                        v -> PokemonTypeEntity.builder()
                                .pokemonInformation(informationEntity)
                                .type(v.type().name())
                                .build()
                ).toList()
        );

        return informationEntity.withTypes(types);
    }
}
