package periskal.academy.backend.jpa.progress;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleProgressRepository extends CrudRepository<ModuleProgress, Long> {
    
    List<ModuleProgress> findByTrainingProgressId(Long trainingProgressId);
    
    Optional<ModuleProgress> findByTrainingProgressIdAndModuleId(Long trainingProgressId, Long moduleId);

}

