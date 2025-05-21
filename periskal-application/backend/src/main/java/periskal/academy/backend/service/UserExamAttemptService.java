package periskal.academy.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@Service
public class UserExamAttemptService {

    private final UserExamAttemptRepository userExamAttemptRepository;

    public UserExamAttemptService(UserExamAttemptRepository userExamAttemptRepository) {
        this.userExamAttemptRepository = userExamAttemptRepository;
    }

    public boolean consumeAttempt(String userId, String examId) {
        Optional<UserExamAttempt> optionalAttempt = userExamAttemptRepository.findByUserIdAndExamId(userId, examId);

        if (optionalAttempt.isEmpty()) {
            return false;
        }

        UserExamAttempt attempt = optionalAttempt.get();

        if (!attempt.hasAttemptsLeft()) {
            return false;
        }

        attempt.useAttempt();
        UserExamAttempt saved = userExamAttemptRepository.save(attempt);
        saved.useAttempt();
        return true;
    }

}