package periskal.academy.backend.dto.progress;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TrainingProgressDTO {
    

    private Long id;
    private String userId;
    private Long trainingId;
    private List<ModuleProgressDTO> moduleProgress = new ArrayList<>();
    private Double overallPercentComplete;
    private Date lastUpdated;
}
