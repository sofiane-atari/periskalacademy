package periskal.academy.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@Service
@Slf4j
public class UserExamAttemptService {

    private final UserExamAttemptRepository userExamAttemptRepository;

    public UserExamAttemptService(UserExamAttemptRepository userExamAttemptRepository) {
        this.userExamAttemptRepository = userExamAttemptRepository;
    }

    public boolean consumeAttempt(String userId, String examId) {
        List<UserExamAttempt> attempts = userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId);
        log.info("Found {} attempts for userId: {}, examId: {}", attempts.size(), userId, examId);

        Optional<UserExamAttempt> active = attempts.stream()
                .filter(UserExamAttempt::hasAttemptsLeft)
                .findFirst();

        if (active.isEmpty()) {
            log.warn("No active attempts found for userId: {}, examId: {}", userId, examId);
            return false;
        }

        UserExamAttempt attempt = active.get();
        log.info("Consuming attempt for userId: {}, examId: {}, remainingAttempts: {}",
                userId, examId, attempt.getRemainingAttempts());

        try {
            attempt.useAttempt();
            userExamAttemptRepository.save(attempt);
            log.info("Attempt consumed successfully for userId: {}, examId: {}", userId, examId);
            return true;
        } catch (IllegalStateException exception) {
            log.error("Failed to consume attempt for userId: {}, examId: {}: {}",
                    userId, examId, exception.getMessage());
            return false;
        }
    }

    public int getRemainingAttempts(String userId, String examId) {
        List<UserExamAttempt> attempts = userExamAttemptRepository.findAllByUserIdAndExamId(userId, examId);
        log.info("Found {} attempts for userId: {}, examId: {}", attempts.size(), userId, examId);
        for (UserExamAttempt attempt : attempts) {
            log.debug("Attempt: id={}, totalAttempts={}, usedAttempts={}, remainingAttempts={}",
                    attempt.getId(), attempt.getTotalAttempts(), attempt.getUsedAttempts(),
                    attempt.getRemainingAttempts());
        }

        Optional<UserExamAttempt> activeAttempt = attempts.stream()
                .filter(UserExamAttempt::hasAttemptsLeft)
                .findFirst();

        if (activeAttempt.isPresent()) {
            int remaining = activeAttempt.get().getRemainingAttempts();
            log.info("Returning remaining attempts for userId: {}, examId: {}: {}",
                    userId, examId, remaining);
            return remaining;
        } else {
            log.warn("No active attempts found for userId: {}, examId: {}", userId, examId);
            return 0;
        }
    }
}