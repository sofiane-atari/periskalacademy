package periskal.academy.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExamRequest {
    private String examTitle;
    private String examDescription;
    private Long trainingId;
    private int totalQuestions;
    private int passingScore = 100; // Default to 100% if not specified
    private List<QuestionDTO> questions;
    private int price;
    private int examAttempts;
    private int duration;
}
