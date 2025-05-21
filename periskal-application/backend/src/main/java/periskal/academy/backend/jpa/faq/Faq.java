package periskal.academy.backend.jpa.faq;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String answer;

    public Faq() {}

    public Faq(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}