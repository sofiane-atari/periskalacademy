package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String textEn;
    private String textNl;
    private String textFr;
    private String textDe;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonBackReference("exam-questions")
    private Exam exam;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "question-photos")
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "question-answers")
    private List<Answer> answers = new ArrayList<>();

    private int correctAnswer;

    public void addPhoto(Photo p) {
        photos.add(p);
        p.setQuestion(this);
    }

    public void removePhoto(Photo p) {
        photos.remove(p);
        p.setQuestion(null);
    }
}