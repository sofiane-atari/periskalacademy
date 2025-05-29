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
    private String productCode;
    private String nameEn;
    private String nameNl;
    private String nameFr;
    private String nameDe;
    private String descriptionEn;
    private String descriptionNl;
    private String descriptionFr;
    private String descriptionDe;
    private List<ModuleDTO> modules;

    public TrainingDTO(Training training) {
        this.id = training.getId();
        this.nameEn = training.getNameEn();
        this.modules = training.getModules().stream().map(ModuleDTO::new).toList();
    }
}
