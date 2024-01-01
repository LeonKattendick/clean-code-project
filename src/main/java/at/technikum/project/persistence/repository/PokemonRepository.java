package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends CrudRepository<PokemonEntity, Long> {

    List<PokemonEntity> findAll();

    Optional<PokemonEntity> findByName(String name);

}
