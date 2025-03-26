package periskal.academy.backend.dto;

import java.util.List;

import periskal.academy.backend.jpa.Training;

public class TrainingDTO {
    private Long id;
    private String name_en;
    private String name_nl;
    private String name_fr;
    private String name_de;

    public String getName_nl() {
        return name_nl;
    }

    public void setName_nl(String name_nl) {
        this.name_nl = name_nl;
    }

    public String getName_fr() {
        return name_fr;
    }

    public void setName_fr(String name_fr) {
        this.name_fr = name_fr;
    }

    public String getName_de() {
        return name_de;
    }

    public void setName_de(String name_de) {
        this.name_de = name_de;
    }

    private String description_en;
    private String description_nl;
    private String description_fr;
    private String description_de;

    public String getDescription_nl() {
        return description_nl;
    }

    public void setDescription_nl(String description_nl) {
        this.description_nl = description_nl;
    }

    public String getDescription_fr() {
        return description_fr;
    }

    public void setDescription_fr(String description_fr) {
        this.description_fr = description_fr;
    }

    public String getDescription_de() {
        return description_de;
    }

    public void setDescription_de(String description_de) {
        this.description_de = description_de;
    }

    public String getDescription_en() {
        return description_en;
    }

    public void setDescription_en(String description) {
        this.description_en = description;
    }

    private List<ModuleDTO> modules;

    public TrainingDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name) {
        this.name_en = name;
    }

    public List<ModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDTO> modules) {
        this.modules = modules;
    }

    public TrainingDTO(Training training) {
        this.id = training.getId();
        this.name_en = training.getName_en();
        this.modules = training.getModules().stream().map(ModuleDTO::new).toList();
    }
}
