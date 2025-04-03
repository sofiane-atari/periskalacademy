package periskal.academy.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ExamDTO {
    private Long id;
    private String examTitle;
    private String examDescription;
    private Long trainingId;

    public ExamDTO(Long id, String examTitle, String examDescription, Long trainingId) {
        this.id = id;
        this.examTitle = examTitle;
        this.examDescription = examDescription;
        this.trainingId = trainingId;
    }
}
