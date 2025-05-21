package periskal.academy.backend.service;

import org.springframework.stereotype.Service;

import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.jpa.ExamRepository;
import periskal.academy.backend.jpa.UserExamAttempt;
import periskal.academy.backend.jpa.UserExamAttemptRepository;

@Service
public class UserExamAttemptService {

    private final UserExamAttemptRepository userExamAttemptRepository;
    private final ExamRepository examRepository;

    public UserExamAttemptService(UserExamAttemptRepository userExamAttemptRepository, ExamRepository examRepository) {
        this.userExamAttemptRepository = userExamAttemptRepository;
        this.examRepository = examRepository;
    }

    public boolean consumeAttempt(String userId, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        UserExamAttempt attempt = userExamAttemptRepository.findByUserIdAndExam(userId, exam)
                .orElseThrow(() -> new RuntimeException("No exam attempts found."));

        if (!attempt.hasAttemptsLeft())
            return false;

        attempt.useAttempt();
        userExamAttemptRepository.save(attempt);
        return true;
    }

}