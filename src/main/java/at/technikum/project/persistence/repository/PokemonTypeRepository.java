package at.technikum.project.persistence.repository;

import at.technikum.project.persistence.model.PokemonTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PokemonTypeRepository extends CrudRepository<PokemonTypeEntity, Long> {

    <S extends PokemonTypeEntity> List<S> saveAll(Iterable<S> entities);

}
