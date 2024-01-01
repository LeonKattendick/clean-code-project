package at.technikum.project.controller;

import at.technikum.project.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    private PokemonService pokemonService;

    @Operation(summary = "Import Pokemon", description = "Imports all Pokemon ids and names provided by the PokeApi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully imported all Pokemon"),
            @ApiResponse(responseCode = "500", description = "An error occurred during fetching, deletion or saving")
    })
    @PostMapping("/import")
    public ResponseEntity<Void> importPokemon() {
        try {
            pokemonService.importPokemon();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "All Pokemon names", description = "Returns a list of all Pokemon names that were imported")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All imported Pokemon names"),
    })
    @GetMapping
    public ResponseEntity<List<String>> getAllPokemonNames() {
        return ResponseEntity.ok(pokemonService.getAllPokemonNames());
    }
}
