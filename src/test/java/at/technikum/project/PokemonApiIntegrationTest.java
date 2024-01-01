package at.technikum.project;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.repository.PokemonRepository;
import at.technikum.project.service.HttpService;
import at.technikum.project.util.pokeApi.PokeApiPokemonListResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private PokeApiPokemonListResponse pokeApiPokemonListResponse() {
        return new PokeApiPokemonListResponse(
                0,
                Collections.singletonList(new PokeApiPokemonListResponse.PokeApiPokemonNameResponse("test"))
        );
    }

    private PokemonEntity pokemonEntity() {
        return PokemonEntity.builder()
                .name("test")
                .likes(0)
                .build();
    }
}
