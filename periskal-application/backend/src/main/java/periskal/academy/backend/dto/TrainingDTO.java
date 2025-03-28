package periskal.academy.backend.dto;

import java.util.List;
import periskal.academy.backend.jpa.Training;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrainingDTO {
    private Long id;
    private String product_code;
    private String name_en;
    private String name_nl;
    private String name_fr;
    private String name_de;
    private String description_en;
    private String description_nl;
    private String description_fr;
    private String description_de;
    private List<ModuleDTO> modules;

    public TrainingDTO(Training training) {
        this.id = training.getId();
        this.name_en = training.getName_en();
        this.modules = training.getModules().stream().map(ModuleDTO::new).toList();
    }
}
