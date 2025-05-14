package periskal.academy.backend.jpa.progress;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ModuleProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_progress_id", nullable = false)
    private TrainingProgress trainingProgress;
    
    @Column(name = "module_id", nullable = false)
    private Long moduleId;
    
    @Column(name = "percent_complete")
    private Double percentComplete;
    
    @OneToMany(mappedBy = "moduleProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompletedChapter> completedChapters = new ArrayList<>();

}
