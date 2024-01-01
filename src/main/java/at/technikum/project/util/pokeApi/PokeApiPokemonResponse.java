package at.technikum.project.util.pokeApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokeApiPokemonResponse(int height, PokeApiPokemonSpritesResponse sprites, List<PokeApiPokemonTypeSlotResponse> types) {

    public record PokeApiPokemonSpritesResponse(@JsonProperty("front_default") String url) {
    }

    public record PokeApiPokemonTypeSlotResponse(PokeApiPokemonTypeResponse type) {

        public record PokeApiPokemonTypeResponse(String name) {
        }
    }
}
