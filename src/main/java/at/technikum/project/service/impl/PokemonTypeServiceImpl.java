package at.technikum.project.service.impl;

import at.technikum.project.persistence.model.PokemonInformationEntity;
import at.technikum.project.persistence.model.PokemonTypeEntity;
import at.technikum.project.persistence.repository.PokemonTypeRepository;
import at.technikum.project.service.PokemonTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PokemonTypeServiceImpl implements PokemonTypeService {

    private PokemonTypeRepository pokemonTypeRepository;

    @Override
    public List<PokemonTypeEntity> saveTypes(PokemonInformationEntity information, List<String> types) {
        val savedTypes = pokemonTypeRepository.saveAll(
                types.stream().map(
                        v -> PokemonTypeEntity.builder()
                                .pokemonInformation(information)
                                .type(v)
                                .build()
                ).toList()
        );
        log.info("Saved all {} types for information with id {}", types.size(), information.getId());

        return savedTypes;
    }
}
