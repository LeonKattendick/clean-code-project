package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonInformationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonInformationRepository extends CrudRepository<PokemonInformationEntity, Long> {
}
