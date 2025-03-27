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

@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Training() {
    }

    private String name_en;

    private String description_en;

    private String name_nl;

    private String description_nl;

    private String name_de;
    private String description_de;

    private String name_fr;
    private String description_fr;

    public String getName_nl() {
        return name_nl;
    }

    public void setName_nl(String nameNL) {
        this.name_nl = nameNL;
    }

    public String getDescription_nl() {
        return description_nl;
    }

    public void setDescription_nl(String descriptionNL) {
        this.description_nl = descriptionNL;
    }

    public String getName_de() {
        return name_de;
    }

    public void setName_de(String nameDE) {
        this.name_de = nameDE;
    }

    public String getDescription_de() {
        return description_de;
    }

    public void setDescription_de(String descriptionDE) {
        this.description_de = descriptionDE;
    }

    public String getName_fr() {
        return name_fr;
    }

    public void setName_fr(String nameFR) {
        this.name_fr = nameFR;
    }

    public String getDescription_fr() {
        return description_fr;
    }

    public void setDescription_fr(String descriptionFR) {
        this.description_fr = descriptionFR;
    }

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();

    public Training(String name_en, String description_en, String name_nl, String description_nl, String name_de,
            String description_de, String name_fr, String description_fr, List<Module> modules) {
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

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> products) {
        this.modules = products;
    }

    public Training(String name, String description, List<Module> products) {
        this.name_en = name;
        this.description_en = description;
        this.modules = products;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name) {
        this.name_en = name;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description) {
        this.description_en = description;
    }
}
