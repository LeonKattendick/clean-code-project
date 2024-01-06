package at.technikum.project;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.model.PokemonInformationEntity;
import at.technikum.project.persistence.repository.PokemonInformationRepository;
import at.technikum.project.persistence.repository.PokemonRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.util.pokeApi.PokeApiPokemonListResponse;
import at.technikum.project.util.pokeApi.PokeApiPokemonResponse;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PokemonApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    HttpService httpService;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private PokemonInformationRepository pokemonInformationRepository;

    @Test
    void import_whenSucceeds_returnOk() throws Exception {
        when(httpService.call(anyString(), any())).thenReturn(pokeApiPokemonListResponse());

        mockMvc.perform(post("/pokemon/import"))
                .andExpect(status().isOk());

        assertEquals(1, pokemonRepository.findAll().size());
    }

    @Test
    void import_whenFails_returnsInternalServerError() throws Exception {
        when(httpService.call(anyString(), any())).thenThrow(new RuntimeException());

        mockMvc.perform(post("/pokemon/import"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllPokemonNames_returnsListFromDatabase() throws Exception {
        pokemonRepository.save(pokemonEntity());

        val mvcResult = mockMvc.perform(get("/pokemon"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("[\"test\"]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getPokemonByName_returnsFromDatabase() throws Exception {
        val pokemon = pokemonRepository.save(pokemonEntity());
        val information = pokemonInformationRepository.save(pokemonInformationEntity());

        pokemonRepository.save(pokemon.withPokemonInformation(information));

        val mvcResult = mockMvc.perform(get("/pokemon/test"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("{\"name\":\"test\",\"likes\":0,\"pokemonInformation\":{\"height\":1,\"types\":[],\"image_url\":\"testUrl\"}}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getPokemonByName_loadsInformationFromApi_whenNotInDatabase() throws Exception {
        when(httpService.call(anyString(), any())).thenReturn(pokeApiPokemonResponse());
        pokemonRepository.save(pokemonEntity());

        val mvcResult = mockMvc.perform(get("/pokemon/test"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("{\"name\":\"test\",\"likes\":0,\"pokemonInformation\":{\"height\":1,\"types\":[],\"image_url\":\"testUrl\"}}",
                mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getPokemonByName_returnsNotFound_ifPokemonNotInDatabase() throws Exception {
        mockMvc.perform(get("/pokemon/notFound"))
                .andExpect(status().isNotFound());
    }

    @Test
    void likePokemon_increasesTheLikesOfAPokemon() throws Exception {
        pokemonRepository.save(pokemonEntity());

        val mvcResult = mockMvc.perform(put("/pokemon/like/test"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("1", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void likePokemon_returnsNotFound_ifPokemonNotInDatabase() throws Exception {
        mockMvc.perform(put("/pokemon/like/notFound"))
                .andExpect(status().isNotFound());
    }

    private PokeApiPokemonListResponse pokeApiPokemonListResponse() {
        return new PokeApiPokemonListResponse(
                0,
                Collections.singletonList(new PokeApiPokemonListResponse.PokeApiPokemonNameResponse("test"))
        );
    }

    private PokeApiPokemonResponse pokeApiPokemonResponse() {
        return new PokeApiPokemonResponse(
                1,
                new PokeApiPokemonResponse.PokeApiPokemonSpritesResponse("testUrl"),
                Collections.emptyList()
        );
    }

    private PokemonEntity pokemonEntity() {
        return PokemonEntity.builder()
                .name("test")
                .likes(0)
                .build();
    }

    private PokemonInformationEntity pokemonInformationEntity() {
        return PokemonInformationEntity.builder()
                .height(1)
                .imageUrl("testUrl")
                .types(Collections.emptyList())
                .build();
    }
}
