package periskal.academy.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExamRequest {
    private String examTitle;
    private String examDescription;
    private Long trainingId;
    private List<QuestionDTO> questions;
}
