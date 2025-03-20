package periskal.academy.backend.jpa;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameModule;

    private String descriptionModule;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private List<Chapter> chapters;

    public Module() {
    }

    public Module(String name, String description) {
        this.nameModule = name;
        this.descriptionModule = description;
    }

    public Module(String nameModule, String descriptionModule, List<Chapter> chapters) {
        this.nameModule = nameModule;
        this.descriptionModule = descriptionModule;
        this.chapters = chapters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameModule() {
        return nameModule;
    }

    public void setNameModule(String name) {
        this.nameModule = name;
    }

    public String getDescriptionModule() {
        return descriptionModule;
    }

    public void setDescriptionModule(String description) {
        this.descriptionModule = description;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}