package periskal.academy.backend.dto;

import periskal.academy.backend.jpa.Answer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private String textEn;
    private String textNl;
    private String textFr;
    private String textDe;

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.textEn = answer.getTextEn();
        this.textNl = answer.getTextNl();
        this.textFr = answer.getTextFr();
        this.textDe = answer.getTextDe();
    }
}
