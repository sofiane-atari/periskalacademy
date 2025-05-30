package periskal.academy.backend.dto;

import java.util.List;
import periskal.academy.backend.jpa.Question;
import periskal.academy.backend.jpa.Photo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String textEn;
    private String textNl;
    private String textFr;
    private String textDe;
    private int correctAnswer;
    private List<Photo> photos;
    private List<AnswerDTO> answers;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.textEn = question.getTextEn();
        this.textNl = question.getTextNl();
        this.textFr = question.getTextFr();
        this.textDe = question.getTextDe();
        this.correctAnswer = question.getCorrectAnswer();
        this.photos = question.getPhotos();
        this.answers = question.getAnswers().stream().map(AnswerDTO::new).toList();
    }
}
