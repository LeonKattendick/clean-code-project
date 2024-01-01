package at.technikum.project.util.pokeApi;

import java.util.List;


public record PokeApiPokemonListResponse(int count, List<PokeApiPokemonResponse> results) {

    public record PokeApiPokemonResponse(String name) {

    }
}
