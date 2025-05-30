package periskal.academy.backend.jpa;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examTitleEn;
    private String examTitleNl;
    private String examTitleFr;
    private String examTitleDe;

    private String examDescriptionEn;
    private String examDescriptionNl;
    private String examDescriptionFr;
    private String examDescriptionDe;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int passingScore = 100; // Default 100% passing score

    private int price;
    private int examAttempts;
    private int duration;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("exam-questions")
    private List<Question> questions = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "trainingId", unique = true, nullable = true)
    @JsonBackReference("training-exam")
    private Training training;

    public Exam() {
    }

    public Exam(String examTitleEn, String examDescriptionEn, int totalQuestions) {
        this.examTitleEn = examTitleEn;
        this.examDescriptionEn = examDescriptionEn;
        this.totalQuestions = totalQuestions;
    }

    @JsonProperty("trainingId")
    public Long getDerivedTrainingId() {
        return (this.training != null) ? this.training.getId() : null;
    }
}
