package periskal.academy.backend.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamAttemptRepository extends CrudRepository<UserExamAttempt, Long> {

    Optional<UserExamAttempt> findByUserIdAndExamId(String userId, String examId);

    List<UserExamAttempt> findAllByUserIdAndExamId(String userId, String examId);

    List<UserExamAttempt> findAllByUserId(String userId);

    List<UserExamAttempt> findAllByExamId(String exam);
}
