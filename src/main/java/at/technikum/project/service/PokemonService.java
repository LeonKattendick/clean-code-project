package at.technikum.project.service;

import at.technikum.project.persistence.model.PokemonEntity;

import java.util.List;

public interface PokemonService {

    void importPokemon();

    List<String> getAllPokemonNames();

    PokemonEntity getPokemonById(long id);

    PokemonEntity likePokemon(long id);

    PokemonEntity dislikePokemon(long id);

}
