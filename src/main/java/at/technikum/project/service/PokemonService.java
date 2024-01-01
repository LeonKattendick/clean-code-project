package at.technikum.project.service;

import at.technikum.project.persistence.model.PokemonEntity;

public interface PokemonService {

    void importPokemon();

    PokemonEntity getPokemonById(long id);

    PokemonEntity likePokemon(long id);

    PokemonEntity dislikePokemon(long id);

}
