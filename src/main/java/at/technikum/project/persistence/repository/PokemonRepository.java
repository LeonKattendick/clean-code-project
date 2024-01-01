package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends CrudRepository<PokemonEntity, Long> {
}
