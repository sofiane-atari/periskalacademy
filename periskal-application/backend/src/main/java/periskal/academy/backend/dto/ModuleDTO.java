package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Module;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModuleDTO {
    private Long id;
    private String nameModule;
    private String descriptionModule;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.nameModule = module.getNameModule_en();
        this.descriptionModule = module.getDescriptionModule_en();
    }
}
