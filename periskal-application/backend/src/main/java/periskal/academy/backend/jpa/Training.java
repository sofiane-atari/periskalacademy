package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name_en;
    private String description_en;
    private String name_nl;
    private String description_nl;
    private String name_de;
    private String description_de;
    private String name_fr;
    private String description_fr;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    public Training(String name_en, String description_en, String name_nl, String description_nl,
                    String name_de, String description_de, String name_fr, String description_fr,
                    List<Module> modules) {
        this.name_en = name_en;
        this.description_en = description_en;
        this.name_nl = name_nl;
        this.description_nl = description_nl;
        this.name_de = name_de;
        this.description_de = description_de;
        this.name_fr = name_fr;
        this.description_fr = description_fr;
        this.modules = modules;
    }

    public Training(String name, String description, List<Module> modules) {
        this.name_en = name;
        this.description_en = description;
        this.modules = modules;
    }
}
