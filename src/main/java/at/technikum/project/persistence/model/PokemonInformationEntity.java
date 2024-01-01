package at.technikum.project.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PokemonInformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("image_url")
    private String imageUrl;

    private int height;

    @OneToMany(mappedBy = "pokemonInformation", cascade = CascadeType.ALL)
    private List<PokemonTypeEntity> types;

}
