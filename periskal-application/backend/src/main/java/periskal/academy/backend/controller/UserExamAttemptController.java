package periskal.academy.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.service.UserExamAttemptService;

@RestController
@RequestMapping("/api/attempts")
@Slf4j
public class UserExamAttemptController {

    private final UserExamAttemptService userExamAttemptController;

    public UserExamAttemptController(UserExamAttemptService userExamAttemptController) {
        this.userExamAttemptController = userExamAttemptController;
    }

    @PostMapping("/{userId}/{examId}/start")
    public ResponseEntity<String> startExam(
            @PathVariable String userId,
            @PathVariable Long examId) {
        boolean success = userExamAttemptController.consumeAttempt(userId, examId);
        if (success) {
            return ResponseEntity.ok("Exam started.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No remaining attempts.");
        }
    }
}
