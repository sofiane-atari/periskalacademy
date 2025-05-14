package periskal.academy.backend.dto.progress;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ModuleProgressDTO {
    
    private Long moduleId;
    private Double percentComplete;
    private List<Long> completedChapters = new ArrayList<>();

}
