package at.technikum.project.service;

import at.technikum.project.persistence.model.PokemonEntity;
import at.technikum.project.persistence.model.PokemonInformationEntity;
import org.springframework.stereotype.Service;

@Service
public interface PokemonInformationService {

    PokemonInformationEntity loadInformationForPokemon(PokemonEntity pokemon);

}
