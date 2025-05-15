package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examTitle;
    private String examDescription;

    @Column(nullable = false)
    private int totalQuestions;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("exam-questions")
    private List<Question> questions = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "trainingId", unique = true, nullable = true)
    @JsonBackReference("training-exam")
    private Training training;

    public Exam() {
    }

    public Exam(String examTitle, String examDescription, int totalQuestions) {
        this.examTitle = examTitle;
        this.examDescription = examDescription;
    }

    @JsonProperty("trainingId")
    public Long getDerivedTrainingId() {
        return (this.training != null) ? this.training.getId() : null;
    }

}
