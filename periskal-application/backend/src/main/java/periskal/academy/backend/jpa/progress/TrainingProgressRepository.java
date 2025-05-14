package periskal.academy.backend.jpa.progress;



import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingProgressRepository extends CrudRepository<TrainingProgress, Long> {
    
    List<TrainingProgress> findByUserId(String userId);
    
    Optional<TrainingProgress> findByUserIdAndTrainingId(String userId, Long trainingId);

}
