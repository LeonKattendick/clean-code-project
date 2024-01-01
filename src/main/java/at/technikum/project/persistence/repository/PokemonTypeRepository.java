package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonTypeRepository extends CrudRepository<PokemonTypeEntity, Long> {
}
