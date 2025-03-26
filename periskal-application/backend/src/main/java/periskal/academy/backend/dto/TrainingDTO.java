package periskal.academy.backend.dto;

import java.util.List;

import periskal.academy.backend.jpa.Training;

public class TrainingDTO {
    private Long id;
    private String name;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDTO> modules) {
        this.modules = modules;
    }

    public TrainingDTO(Training training) {
        this.id = training.getId();
        this.name = training.getName_en();
        this.modules = training.getModules().stream().map(ModuleDTO::new).toList();
    }
}
