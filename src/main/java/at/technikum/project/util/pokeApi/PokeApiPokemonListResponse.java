package at.technikum.project.util.pokeApi;

import java.util.List;


public record PokeApiPokemonListResponse(int count, List<PokeApiPokemonNameResponse> results) {

    public record PokeApiPokemonNameResponse(String name) {
    }
}
