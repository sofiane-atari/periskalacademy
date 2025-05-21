package periskal.academy.backend.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamAttemptRepository extends CrudRepository<UserExamAttempt, Long> {

    Optional<UserExamAttempt> findByUserIdAndExam(String userId, Exam exam);

    List<UserExamAttempt> findAllByUserId(String userId);

    List<UserExamAttempt> findAllByExam(Exam exam);
}
