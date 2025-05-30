package periskal.academy.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.CompletedExamDTO;
import periskal.academy.backend.security.SecurityRoles;
import periskal.academy.backend.service.CompletedExamService;

@RestController
@RequestMapping("/api/completed-exams")
@Slf4j
public class CompletedExamController {
    
    private final CompletedExamService completedExamService;

    
    public CompletedExamController(CompletedExamService completedExamService) {
        this.completedExamService = completedExamService;
    }
    

    @GetMapping("/user/{userId}")
    @PreAuthorize(SecurityRoles.ALL_ROLES)
    public ResponseEntity<List<CompletedExamDTO>> getUserCompletedExams(@PathVariable("userId") String userId) {
        List<CompletedExamDTO> completedExams = completedExamService.getCompletedExamsByUserId(userId);
        return ResponseEntity.ok(completedExams);
    }
    

    @PostMapping
    @PreAuthorize(SecurityRoles.ADMIN_SKIPPER)
    public ResponseEntity<CompletedExamDTO> saveCompletedExam(@RequestBody CompletedExamDTO completedExamDTO) {
        CompletedExamDTO savedExam = completedExamService.saveCompletedExam(completedExamDTO);
        return ResponseEntity.ok(savedExam);
    }
    

}
