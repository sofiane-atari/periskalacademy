package periskal.academy.backend.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class UserExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String examId;

    private int totalAttempts;
    private int usedAttempts;

    public int getRemainingAttempts() {
        return totalAttempts - usedAttempts;
    }

    public boolean hasAttemptsLeft() {
        return getRemainingAttempts() > 0;
    }

    public void addAttempts(int count) {
        this.totalAttempts += count;
    }

    public void useAttempt() {
        if (!hasAttemptsLeft()) {
            throw new IllegalStateException("No remaining attempts.");
        }
        this.usedAttempts++;
    }
}
