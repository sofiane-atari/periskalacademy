package periskal.academy.backend.jpa;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CompletedExam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "training_id", nullable = false)
    private Long trainingId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "required_score", nullable = false)
    private int requiredScore;
    
    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "completed_date", nullable = false)
    private Date completedDate;
    
    @Column(name = "certificate_id", nullable = true)
    private Long certificateId;
    
    public boolean isPassed() {
        return score >= requiredScore;
    }
}
