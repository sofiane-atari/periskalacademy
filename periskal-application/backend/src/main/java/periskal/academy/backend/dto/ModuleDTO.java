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
    private String nameModule_en;
    private String descriptionModule_en;
    private String nameModule_nl;
    private String descriptionModule_nl;
    private String nameModule_fr;
    private String descriptionModule_fr;
    private String nameModule_de;
    private String descriptionModule_de;
    private List<Chapter> chapters;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.nameModule_en = module.getNameModule_en();
        this.descriptionModule_en = module.getDescriptionModule_en();
        this.nameModule_nl = module.getNameModule_nl();
        this.descriptionModule_nl = module.getDescriptionModule_nl();
        this.nameModule_fr = module.getNameModule_fr();
        this.descriptionModule_fr = module.getDescriptionModule_fr();
        this.nameModule_de = module.getNameModule_de();
        this.descriptionModule_en = module.getDescriptionModule_de();
        this.chapters = module.getChapters();
    }
}
