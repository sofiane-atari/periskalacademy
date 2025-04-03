package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameModule_en;
    private String nameModule_nl;
    private String nameModule_fr;
    private String nameModule_de;

    private String descriptionModule_en;
    private String descriptionModule_nl;
    private String descriptionModule_fr;
    private String descriptionModule_de;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    @JsonBackReference
    private Training training;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Chapter> chapters = new ArrayList<>();;

    public Module(String name, String description) {
        this.nameModule_en = name;
        this.descriptionModule_en = description;
    }

    public Module(String nameModule, String descriptionModule, List<Chapter> chapters) {
        this.nameModule_en = nameModule;
        this.descriptionModule_en = descriptionModule;
        this.chapters = chapters;
    }
}
