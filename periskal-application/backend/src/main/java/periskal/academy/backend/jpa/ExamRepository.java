package periskal.academy.backend.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExamRepository extends CrudRepository<Exam, Long> {
    Optional<Exam> findByTrainingId(Long trainingId);

    boolean existsByTrainingId(Long trainingId);

    @EntityGraph(attributePaths = "questions")
    Optional<Exam> findWithQuestionsById(Long id);
}
