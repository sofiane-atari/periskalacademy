package periskal.academy.backend.service;

import java.util.List;
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
        List<UserExamAttempt> attempts = userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId);

        Optional<UserExamAttempt> active = attempts.stream()
                .filter(UserExamAttempt::hasAttemptsLeft)
                .findFirst();

        if (active.isEmpty()) {
            return false;
        }

        UserExamAttempt attempt = active.get();

        if (!attempt.hasAttemptsLeft()) {
            return false;
        }

        try {
            attempt.useAttempt();
            userExamAttemptRepository.save(attempt);
            return true;
        } catch (IllegalStateException exception) {
            // attempt has run out of uses => send the user back to billing page by
            // returning false
            return false;
        }
    }

}