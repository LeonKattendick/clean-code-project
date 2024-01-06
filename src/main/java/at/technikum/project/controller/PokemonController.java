package at.technikum.project.controller;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
            log.error("An error occurred in 'importPokemon'", e);
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

    @Operation(summary = "Get Pokemon by name", description = "Returns a Pokemon for a given name, if it is found in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested Pokemon"),
            @ApiResponse(responseCode = "404", description = "No Pokemon could be found with the provided name"),
            @ApiResponse(responseCode = "500", description = "An error occurred while fetching more information for the Pokemon"),
    })
    @GetMapping("/{name}")
    public ResponseEntity<PokemonEntity> getPokemonByName(@PathVariable String name) {
        try {
            val pokemonOptional = pokemonService.getPokemonByName(name.toLowerCase());
            return pokemonOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("An error occurred in 'getPokemonByName'", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Like Pokemon", description = "Returns the new likes of the Pokemon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The new likes of the Pokemon"),
            @ApiResponse(responseCode = "404", description = "No Pokemon could be found with the provided name"),
            @ApiResponse(responseCode = "500", description = "An error occurred while liking for the Pokemon"),
    })
    @PutMapping("/like/{name}")
    public ResponseEntity<Integer> likePokemon(@PathVariable String name) {
        try {
            val pokemonOptional = pokemonService.likePokemon(name.toLowerCase());
            return pokemonOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("An error occurred in 'likePokemon'", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
