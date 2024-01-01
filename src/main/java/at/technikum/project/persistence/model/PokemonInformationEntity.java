package at.technikum.project.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class PokemonInformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @JsonProperty("image_url")
    private String imageUrl;

    private int height;

    @With
    @OneToMany(mappedBy = "pokemonInformation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PokemonTypeEntity> types;

}
