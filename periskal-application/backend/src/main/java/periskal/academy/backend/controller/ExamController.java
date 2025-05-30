package periskal.academy.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ExamRequest;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.security.SecurityRoles;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.service.ExamService;

@RestController
@CrossOrigin(origins = "http://frontend.localhost:4200", allowCredentials = "true")
@RequestMapping("/api/exams")
@Slf4j
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/{id}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        Optional<Exam> exam = examService.findById(id);
        return exam.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
    }

    @GetMapping("/training/{trainingId}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<Exam> getExamByTrainingId(@PathVariable Long trainingId) {
        Optional<Exam> exam = examService.findByTrainingId(trainingId);
        return exam.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found for training id: " + trainingId));
    }

    @PutMapping("/{id}")
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam updatedExam) {
        Exam exam = examService.updateExam(id, updatedExam);
        if (exam == null) {
            throw new ResourceNotFoundException("Exam not found with id: " + id);
        }
        return ResponseEntity.ok(exam);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id) {
        
        examService.deleteExam(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize(SecurityRoles.ADMIN)
    public ResponseEntity<Exam> createExamWithQuestions(@RequestBody ExamRequest examRequest) {
        Exam savedExam = examService.createExam(examRequest);
        return ResponseEntity.ok(savedExam);
    }

}
