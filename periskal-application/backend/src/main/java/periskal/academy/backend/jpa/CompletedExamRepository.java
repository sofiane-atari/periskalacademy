package periskal.academy.backend.jpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedExamRepository extends CrudRepository<CompletedExam, Long> {
    

    List<CompletedExam> findByUserId(String userId);

    List<CompletedExam> findByUserIdAndTrainingId(String userId, Long trainingId);



}
