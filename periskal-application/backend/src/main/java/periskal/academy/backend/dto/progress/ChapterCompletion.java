package periskal.academy.backend.dto.progress;

import lombok.Data;

@Data
public class ChapterCompletion {
    
    private String userId;
    private Long trainingId;
    private Long moduleId;
    private Long chapterId;

}
