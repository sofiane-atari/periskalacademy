package periskal.academy.backend.jpa;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    List<Question> findByExamId(Long examId);
}