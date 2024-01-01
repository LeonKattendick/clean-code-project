package at.technikum.project.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PokemonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("poke_api_id")
    private long pokeApiId;

    private String name;

    private int likes;

    private int dislikes;

    @OneToOne
    @JoinColumn(name = "information_id", referencedColumnName = "id")
    private PokemonInformationEntity pokemonInformation;

}
