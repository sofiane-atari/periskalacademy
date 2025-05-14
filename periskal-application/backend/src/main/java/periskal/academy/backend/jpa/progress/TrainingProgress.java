package periskal.academy.backend.jpa.progress;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TrainingProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "training_id", nullable = false)
    private Long trainingId;
    
    @Column(name = "overall_percent_complete")
    private Double overallPercentComplete;
    
    @Column(name = "last_updated")
    private Date lastUpdated;
    
    @OneToMany(mappedBy = "trainingProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleProgress> moduleProgress = new ArrayList<>();

}
