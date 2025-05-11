package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Chapter;
import periskal.academy.backend.jpa.Module;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModuleDTO {
    private Long id;
    private String nameModuleEn;
    private String descriptionModuleEn;
    private String nameModuleNl;
    private String descriptionModuleNl;
    private String nameModuleFr;
    private String descriptionModuleFr;
    private String nameModuleDe;
    private String descriptionModuleDe;
    private List<Chapter> chapters;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.nameModuleEn = module.getNameModuleEn();
        this.descriptionModuleEn = module.getDescriptionModuleEn();
        this.nameModuleNl = module.getNameModuleNl();
        this.descriptionModuleNl = module.getDescriptionModuleNl();
        this.nameModuleFr = module.getNameModuleFr();
        this.descriptionModuleFr = module.getDescriptionModuleFr();
        this.nameModuleDe = module.getNameModuleDe();
        this.descriptionModuleEn = module.getDescriptionModuleDe();
        this.chapters = module.getChapters();
    }
}
