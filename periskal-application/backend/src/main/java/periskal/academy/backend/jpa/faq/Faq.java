package periskal.academy.backend.jpa.faq;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String questionEn;

    @Column(nullable = false)
    private String answerEn;

    @Column(nullable = false)
    private String questionNl;

    @Column(nullable = false)
    private String answerNl;

    @Column(nullable = false)
    private String questionFr;

    @Column(nullable = false)
    private String answerFr;

    @Column(nullable = false)
    private String questionDe;

    @Column(nullable = false)
    private String answerDe;

    private String category;
}