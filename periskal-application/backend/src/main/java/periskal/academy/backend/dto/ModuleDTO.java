package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Module;

public class ModuleDTO {
    private Long id;
    private String nameModule;
    private String descriptionModule;

    public ModuleDTO() {
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

    public void setNameModule(String nameModule) {
        this.nameModule = nameModule;
    }

    public String getDescriptionModule() {
        return descriptionModule;
    }

    public void setDescriptionModule(String descriptionModule) {
        this.descriptionModule = descriptionModule;
    }

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.nameModule = module.getNameModule();
        this.descriptionModule = module.getDescriptionModule();
    }
}
