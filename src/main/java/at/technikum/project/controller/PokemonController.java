package at.technikum.project.controller;

import at.technikum.project.service.PokemonService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    private PokemonService pokemonService;

    @PostMapping("/import")
    public void importPokemon() {
        pokemonService.importPokemon();
    }
}
