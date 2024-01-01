package at.technikum.project.service;

import at.technikum.project.persistence.model.PokemonInformationEntity;
import at.technikum.project.persistence.model.PokemonTypeEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PokemonTypeService {

    List<PokemonTypeEntity> saveTypes(PokemonInformationEntity information, List<String> types);

}
