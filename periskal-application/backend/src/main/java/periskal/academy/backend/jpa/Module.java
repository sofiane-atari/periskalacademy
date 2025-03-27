package periskal.academy.backend.jpa;

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

    private String nameModule;

    private String descriptionModule;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    @JsonBackReference
    private Training training;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Chapter> chapters;

    
    public Module(String name, String description) {
        this.nameModule = name;
        this.descriptionModule = description;
    }

    public Module(String nameModule, String descriptionModule, List<Chapter> chapters) {
        this.nameModule = nameModule;
        this.descriptionModule = descriptionModule;
        this.chapters = chapters;
    }
}
