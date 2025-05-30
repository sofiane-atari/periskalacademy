package periskal.academy.backend.dto;

import java.util.List;
import periskal.academy.backend.jpa.Exam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamDTO {
    private Long id;
    private Long trainingId;
    private String examTitleEn;
    private String examTitleNl;
    private String examTitleFr;
    private String examTitleDe;
    private String examDescriptionEn;
    private String examDescriptionNl;
    private String examDescriptionFr;
    private String examDescriptionDe;
    private int totalQuestions;
    private int passingScore;
    private int price;
    private int examAttempts;
    private int duration;
    private List<QuestionDTO> questions;

    public ExamDTO(Exam exam) {
        this.id = exam.getId();
        this.trainingId = exam.getDerivedTrainingId();
        this.examTitleEn = exam.getExamTitleEn();
        this.examTitleNl = exam.getExamTitleNl();
        this.examTitleFr = exam.getExamTitleFr();
        this.examTitleDe = exam.getExamTitleDe();
        this.examDescriptionEn = exam.getExamDescriptionEn();
        this.examDescriptionNl = exam.getExamDescriptionNl();
        this.examDescriptionFr = exam.getExamDescriptionFr();
        this.examDescriptionDe = exam.getExamDescriptionDe();
        this.totalQuestions = exam.getTotalQuestions();
        this.passingScore = exam.getPassingScore();
        this.price = exam.getPrice();
        this.examAttempts = exam.getExamAttempts();
        this.duration = exam.getDuration();
        this.questions = exam.getQuestions().stream().map(QuestionDTO::new).toList();
    }
} 