package periskal.academy.backend.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameModule;

    private String descriptionModule;

    public Module(String name, String description) {
        this.nameModule = name;
        this.descriptionModule = description;
    }

    public Module() {
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
}