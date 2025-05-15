package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.service.CompletedExamService;
import periskal.academy.backend.util.JwtUtil;

@RestController
@RequestMapping("/api/completed-exams")
public class CompletedExamController {
    
    private final CompletedExamService completedExamService;

    
    public CompletedExamController(CompletedExamService completedExamService, JwtUtil jwtUtil) {
        this.completedExamService = completedExamService;
    }
    

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CompletedExamDTO>> getUserCompletedExams(
            @RequestHeader("userId") String userId) {
        List<CompletedExamDTO> completedExams = completedExamService.getCompletedExamsByUserId(userId);
        return ResponseEntity.ok(completedExams);
    }
    

    @PostMapping
    public ResponseEntity<CompletedExamDTO> saveCompletedExam(
            @RequestBody CompletedExamDTO completedExamDTO) {
        CompletedExamDTO savedExam = completedExamService.saveCompletedExam(completedExamDTO);
        return ResponseEntity.ok(savedExam);
    }
    

}
