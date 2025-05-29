package periskal.academy.backend.jpa.assignment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TrainingAssignmentRepository extends CrudRepository<TrainingAssignment, Long> {
    
    List<TrainingAssignment> findBySkipperId(String skipperId);

    Optional<TrainingAssignment> findBySkipperIdAndTrainingId(String skipperId, Long trainingId);

    List<TrainingAssignment> findByAssignedBy(String fleetManagerId);
    
}


