package at.technikum.project.service;

import at.technikum.project.persistence.model.PokemonEntity;

import java.util.List;
import java.util.Optional;

public interface PokemonService {

    void importPokemon();

    List<String> getAllPokemonNames();

    Optional<PokemonEntity> getPokemonByName(String name);

    Optional<Integer> likePokemon(String name);

}
