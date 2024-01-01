package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonRepository extends CrudRepository<PokemonEntity, Long> {

    List<PokemonEntity> findAll();

}
