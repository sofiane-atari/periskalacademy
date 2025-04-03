package periskal.academy.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import periskal.academy.backend.jpa.Exam;
import periskal.academy.backend.service.ExamService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://frontend.localhost:4200", allowCredentials = "true")
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        Optional<Exam> exam = examService.findById(id);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/training/{trainingId}")
    public ResponseEntity<Exam> getExamByTrainingId(@PathVariable Long trainingId) {
        Optional<Exam> exam = examService.findByTrainingId(trainingId);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/training/{trainingId}/create")
    public ResponseEntity<?> createExamForTraining(@PathVariable Long trainingId, @RequestBody Exam exam) {
        try {
            Exam savedExam = examService.createExamForTraining(trainingId, exam);
            return ResponseEntity.ok(savedExam);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam updatedExam) {
        Exam exam = examService.updateExam(id, updatedExam);
        return exam != null ? ResponseEntity.ok(exam) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id) {
        boolean deleted = examService.deleteExam(id);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Exam deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Exam not found");
            return ResponseEntity.notFound().build();
        }
    }

}
